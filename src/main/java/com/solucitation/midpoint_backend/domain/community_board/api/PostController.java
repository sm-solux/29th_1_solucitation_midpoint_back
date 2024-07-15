package com.solucitation.midpoint_backend.domain.community_board.api;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostDetailDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostRequestDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;

import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.service.PostService;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.repository.MemberRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    
    /**
     * 모든 게시글을 요약된 형태로 생성일 최신순부터 가져옵니다.
     * 이때 로그인되어 있으며 해당 게시글에 좋아요를 눌렀을 경우에는 PostResponseDto 내 likes 필드를 true로 반환합니다. (기본값 false)
     *
     * @param authentication 인증정보
     * @return 성공 시 200 OK와 함께 게시글 목록을 반환하며, 실패 시 500 Internal Server Error를 반환합니다.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllPosts(Authentication authentication) {
        try {
            Member member = null;

            if (!(authentication == null || !authentication.isAuthenticated())) {
                String memberEmail = authentication.getName();
                member = memberService.getMemberByEmail(memberEmail);
            }

            List<PostResponseDto> postResponseDtos = postService.getAllPosts(member);
            return ResponseEntity.ok(postResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 조회 중 오류가 발생하였습니다 : " + e.getMessage());
        }
    }

    /**
     * 특정 게시글 내용 전체를 가져옵니다.
     *
     * @param postId 게시글 번호
     * @return 성공 시 200 OK와 함께 게시글 상세 정보를 반환하며, 실패 시 404 Not Found 또는 500 Internal Server Error를 반환합니다.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            PostDetailDto postDetailDto = postService.getPostById(postId);
            return ResponseEntity.ok(postDetailDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 조회 중 오류가 발생하였습니다 : " + e.getMessage());
        }
    }
}