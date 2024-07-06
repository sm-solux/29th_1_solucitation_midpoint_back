package com.solucitation.midpoint_backend.domain.community_board.service;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostRequestDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;
import com.solucitation.midpoint_backend.domain.community_board.entity.Hashtag;
import com.solucitation.midpoint_backend.domain.community_board.entity.Image;
import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.entity.PostHashtag;
import com.solucitation.midpoint_backend.domain.community_board.repository.HashtagRepository;
import com.solucitation.midpoint_backend.domain.community_board.repository.PostRepository;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired private PostRepository postRepository;
    @Autowired private HashtagRepository hashtagRepository;

    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findPostWithImagesById(postId);

        if (post == null) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }

        return new PostResponseDto(post);
    }

    @Transactional
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAllPostWithImagesAndPostHashtags();
        return posts.stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public Post createPost(PostRequestDto postRequestDto, Member member) {
//        postRequestDto.validatePostHashtags();
//        postRequestDto.validateImages();
//        LocalDateTime time = LocalDateTime.now();
//
//        List<PostHashtag> postHashtags = new ArrayList<>();
//        List<Image> images = new ArrayList<Image>();
//
//        Post post = Post.builder()
//                .member(member)
//                .title(postRequestDto.getTitle())
//                .content(postRequestDto.getContent())
//                .createDate(time)
//                .updateDate(time)
//                .postHashtags(postHashtags)
//                .build();
//
//        postHashtags = postRequestDto.getPostHashtag().stream()
//                .map(tagId -> {
//                    Hashtag hashtag = hashtagRepository.findById(tagId)
//                            .orElseThrow(() -> new IllegalArgumentException("해당 해시태그는 존재하지 않습니다."));
//                    return new PostHashtag(post, hashtag); // Post 객체 설정
//                })
//                .toList();
//
//         images = postRequestDto.getImages().stream()
//                .map(path -> new Image(path, member, time, time))
//                .toList();
//
//        postHashtags.forEach(post::addPostHashtag);
//        images.forEach(post::addImage);
//
//        return postRepository.save(post);
//    }
}
