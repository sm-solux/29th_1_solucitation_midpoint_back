package com.solucitation.midpoint_backend.domain.community_board.api;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostDetailDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostRequestDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;

import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.service.PostService;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostDetailDto postDetailDto = postService.getPostById(postId);
            return ResponseEntity.ok(postDetailDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 존재하지 않습니다. ID: " + postId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 조회 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 모든 게시글을 생성일 최신순부터 가져옵니다.
     * 이때 로그인되어 있으며 해당 게시글에 좋아요를 눌렀을 경우에는 PostResponseDto 내 likes 필드를 true 로 반환합니다. (기본값 false)
     * 
     * @param authentication 인증정보
     * @return 성공 시 200 OK, 실패 시 서버 에러 반환
     */
    @GetMapping("")
    public ResponseEntity<?> getAllPosts(Authentication authentication) {
        try {
            Member member = null;
            String memberName = authentication.getName();
            Optional<Member> memberOptional = memberService.getMemberByName(memberName);

            member = memberOptional.orElse(null);

            List<PostResponseDto> postResponseDtos = postService.getAllPosts(member);
            return ResponseEntity.ok(postResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 목록 조회 중 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createPost(Authentication authentication,
                                        @Valid @RequestBody PostRequestDto postRequestDto,
                                        @RequestParam("postImages") List<MultipartFile> postImages) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            String memberName = authentication.getName();
            Optional<Member> member = memberService.getMemberByName(memberName);
            if (member.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }

            postService.createPost(postRequestDto, member.get(), postImages);
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글을 성공적으로 등록하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @GetMapping("/search")
//    public ResponseEntity<?> searchByQuery(Authentication authentication, @RequestParam("query") String query) {
//        try {
//            Member member = null;
//            String memberName = authentication.getName();
//            Optional<Member> memberOptional = memberService.getMemberByName(memberName);
//
//            member = memberOptional.orElse(null);
//
//            List<PostResponseDto> postResponseDtos = postService.getPostByQuery(member,query);
//            return ResponseEntity.ok(postResponseDtos);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("게시글 검색 중 오류 발생: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<?> searchByPurpose(Authentication authentication, @RequestParam("purpose") List<Long> purposes ) {
//        try {
//            Member member = null;
//            String memberName = authentication.getName();
//            Optional<Member> memberOptional = memberService.getMemberByName(memberName);
//
//            member = memberOptional.orElse(null);
//
//            List<PostResponseDto> postResponseDtos = postService.getPostByPurpose(member, purposes);
//            return ResponseEntity.ok(postResponseDtos);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("게시글 검색 중 오류 발생: " + e.getMessage());
//        }
//    }
}