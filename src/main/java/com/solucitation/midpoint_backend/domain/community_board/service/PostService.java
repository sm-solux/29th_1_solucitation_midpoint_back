package com.solucitation.midpoint_backend.domain.community_board.service;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostDetailDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostRequestDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;
import com.solucitation.midpoint_backend.domain.community_board.entity.*;
import com.solucitation.midpoint_backend.domain.community_board.repository.*;
import com.solucitation.midpoint_backend.domain.file.service.S3Service;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import com.solucitation.midpoint_backend.domain.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final HashtagRepository hashtagRepository;
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final MemberService memberService;
    private final PostHashtagRepository postHashtagsRepository;

    @Transactional(readOnly = true)
    public PostDetailDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        List<String> images = post.getImages().stream()
                .map(Image::getImageUrl)
                .toList();

        List<Long> hashtags = post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getId())
                .toList();

        int likeCnt = likesRepository.countByPostIdAndIsLiked(postId);

        return new PostDetailDto(
                post.getMember().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getCreateDate(),
                hashtags,
                images,
                likeCnt
        );
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts(Member member) {
        List<Post> posts = postRepository.findAllPostWithImagesAndPostHashtags();

        return posts.stream()
                .map(post -> {
                    PostResponseDto postDto = new PostResponseDto(post);
                    if (member != null) {
                        postDto.setLikes(likesRepository.isMemberLikesPostByEmail(post.getId(), member.getEmail()));
                    }
                    return postDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean changeLikes(String memberEmail,  Long postId) {
        boolean existingLike = likesRepository.isMemberLikesPostByEmail(postId, memberEmail);

        if (existingLike) {
            likesRepository.deleteByMemberEmailAndPostId(memberEmail, postId);
            return false;
        } else {
            Member member = memberService.getMemberByEmail(memberEmail);
            Optional<Post> post = postRepository.findById(postId);
            if (member != null && post.isPresent()) {
                Likes likes = new Likes(post.get(), member);
                likesRepository.save(likes);
                return true;
            }
        }
        throw new IllegalArgumentException("좋아요 상태를 변경하는 중 오류가 발생하였습니다.");
    }
}