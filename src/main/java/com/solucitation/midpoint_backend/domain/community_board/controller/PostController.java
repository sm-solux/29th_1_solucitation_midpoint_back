package com.solucitation.midpoint_backend.domain.community_board.controller;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;
import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.exception.ErrorResponse;

import com.solucitation.midpoint_backend.domain.community_board.service.MemberService;
import com.solucitation.midpoint_backend.domain.community_board.service.PostService;
import com.solucitation.midpoint_backend.domain.community_board.exception.UserNotFoundException;
import com.solucitation.midpoint_backend.domain.community_board.repository.PostRepository;
import com.solucitation.midpoint_backend.domain.community_board.exception.AuthenticationException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/midpoint/api")
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    private final PostRepository postRepository;

    @Autowired
    public PostController(PostService postService, MemberService memberService, PostRepository postRepository) {
        this.postService = postService;
        this.memberService = memberService;
        this.postRepository = postRepository;
    }

    @Transactional
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getAllPost() {
        List<Post> posts = postRepository.findAllPostWithImagesAndPostHashtags();
        List<PostResponseDto> dtos = posts.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

//    @PostMapping("/posts")
//    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequestDto postRequestDto, Member member) {
//        if (userDetails == null) {
//            throw new AuthenticationException("게시글을 작성하기 위해서는 로그인이 필요합니다.");
//        }
//
//        PostCreateDetails customUserDetails = (PostCreateDetails) userDetails;
//        Long userId = customUserDetails.getId();
//
//        Member member = memberService.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("사용자 정보를 찾을 수 없습니다."));
//
//        Post post = postService.createPost(postRequestDto, member);
//        return new ResponseEntity<>(post, HttpStatus.CREATED);
//    }

//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
//        ErrorResponse errorResponse = new ErrorResponse("Unauthorized", e.getMessage());
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }
//
//
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//    }
}

