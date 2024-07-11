package com.solucitation.midpoint_backend.domain.community_board.service;

import com.solucitation.midpoint_backend.domain.community_board.dto.PostDetailDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostRequestDto;
import com.solucitation.midpoint_backend.domain.community_board.dto.PostResponseDto;
import com.solucitation.midpoint_backend.domain.community_board.entity.Hashtag;
import com.solucitation.midpoint_backend.domain.community_board.entity.Image;
import com.solucitation.midpoint_backend.domain.community_board.entity.Post;
import com.solucitation.midpoint_backend.domain.community_board.entity.PostHashtag;
import com.solucitation.midpoint_backend.domain.community_board.repository.HashtagRepository;
import com.solucitation.midpoint_backend.domain.community_board.repository.ImageRepository;
import com.solucitation.midpoint_backend.domain.community_board.repository.LikesRepository;
import com.solucitation.midpoint_backend.domain.community_board.repository.PostRepository;
import com.solucitation.midpoint_backend.domain.file.service.S3Service;
import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                        postDto.setLikes(likesRepository.isMemberLikesPost(post.getId(), member.getId()));
                    }
                    return postDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createPost(PostRequestDto postRequestDto, Member member, List<MultipartFile> postImages) {
        postRequestDto.validatePostHashtags();
        postRequestDto.validateImages();
        LocalDateTime time = LocalDateTime.now();

        List<PostHashtag> postHashtags = new ArrayList<>();
        List<Image> images = new ArrayList<>();

        Post post = Post.builder()
                .member(member)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .createDate(time)
                .updateDate(time)
                .postHashtags(postHashtags)
                .images(images)
                .build();

        postHashtags = postRequestDto.getPostHashtag().stream()
                .map(tagId -> {
                    Hashtag hashtag = hashtagRepository.findById(tagId)
                            .orElseThrow(() -> new IllegalArgumentException("해당 해시태그는 존재하지 않습니다."));
                    return new PostHashtag(post, hashtag); // Post 객체 설정
                })
                .toList();

        // 게시글 이미지 업로드 및 저장
        if (!postImages.isEmpty()) {
            try {
                for (MultipartFile postImage : postImages) {
                    String postImageUrl = s3Service.upload("post-images", postImage.getOriginalFilename(), postImage);
                    Image image = Image.builder()
                            .imageUrl(postImageUrl).member(member).build();
                    imageRepository.save(image);
                    images.add(image);
                }
            } catch (IOException e){
                log.error("게시글 이미지 업로드 실패: {}", e.getMessage());
                throw new RuntimeException("게시글 이미지 업로드에 실패하였습니다.");
            }
        }

        postHashtags.forEach(post::addPostHashtag);
        images.forEach(post::addImage);
        postRepository.save(post);
    }

//    @Transactional(readOnly = true)
//    public List<PostResponseDto> getPostByQuery(Member member, String query) {
//        List<Post> posts = postRepository.findAllPostByQuery(query);
//
//        return posts.stream()
//                .map(post -> {
//                    PostResponseDto postDto = new PostResponseDto(post);
//                    if (member != null) {
//                        postDto.setLikes(likesRepository.isMemberLikesPost(post.getId(), member.getId()));
//                    }
//                    return postDto;
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<PostResponseDto> getPostByPurpose(Member member, List<Long> purposes) {
//        List<Post> posts = postRepository.findAllPostByPurpose(purposes, purposes.size());
//
//        return posts.stream()
//                .map(post -> {
//                    PostResponseDto postDto = new PostResponseDto(post);
//                    if (member != null) {
//                        postDto.setLikes(likesRepository.isMemberLikesPost(post.getId(), member.getId()));
//                    }
//                    return postDto;
//                })
//                .collect(Collectors.toList());
//    }
}