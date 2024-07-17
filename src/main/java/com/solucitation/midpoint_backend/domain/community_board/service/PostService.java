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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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

    @Transactional
    public void createPost(PostRequestDto postRequestDto, Member member, List<MultipartFile> postImages) {
        postRequestDto.validatePostHashtags();

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

        postRepository.save(post);

        postHashtags = addHashtags(post, postRequestDto.getPostHashtag());
        images = addImages(post, member, postImages);

        try {
            log.info("게시글 등록 성공");
        } catch (Exception e) {
            throw new RuntimeException("게시글 등록 실패");
        }
    }

    @Transactional
    public List<Image> addImages(Post post, Member member,  List<MultipartFile> postImages) {
        List<Image> images = new ArrayList<>();
        if (!postImages.isEmpty()) {
            try {
                for (MultipartFile postImage : postImages) {
                    if (postImage.isEmpty()) {
                        log.error("게시글 이미지 업로드 실패");
                        throw new RuntimeException("이미지가 존재하지 않습니다.");
                    }
                    String postImageUrl = s3Service.upload("post-images", postImage.getOriginalFilename(), postImage);

                    Image image = Image.builder()
                            .imageUrl(postImageUrl).member(member).post(post).build();
                    imageRepository.save(image);
                    images.add(image);
                }
            } catch (IOException e){
                log.error("게시글 이미지 업로드 실패: {}", e.getMessage());
                throw new RuntimeException("게시글 이미지 업로드에 실패하였습니다.");
            }
        }
        return images;
    }

    @Transactional
    public List<PostHashtag> addHashtags(Post post, List<Long> hashtags) {
        List<PostHashtag> postHashtags = new ArrayList<>();
        for (Long tagId : hashtags) {
            Hashtag hashtag = hashtagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 해시태그는 존재하지 않습니다."));
            PostHashtag postHashtag = new PostHashtag(post, hashtag);
            postHashtagsRepository.save(postHashtag);
            postHashtags.add(postHashtag);
        }
        return postHashtags;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostByPurpose(Member member, List<Long> purposes) {
        if (purposes.isEmpty()) {
            throw new IllegalArgumentException("최소 하나 이상의 해시태그를 선택해야 합니다.");
        }
        for (Long tagId : purposes) { // 해시태그 유효성 검사
            Hashtag hashtag = hashtagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 해시태그는 존재하지 않습니다."));
        }
        List<Post> posts = postRepository.findAllPostByPurpose(purposes);

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

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostByQuery(Member member, String query) {
        if (query.isEmpty() || query.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        String[] words = query.split("\\s+");

        Set<Post> resultSet = wordsToPosts(words);

        // Set을 List로 변환하고 postId를 기준으로 내림차순 정렬합니다.
        List<Post> sortedPosts = resultSet.stream()
                .sorted(Comparator.comparing(Post::getId).reversed())
                .collect(Collectors.toList());

        List<PostResponseDto> postResponseDtos = sortedPosts.stream()
                .map(post -> {
                    PostResponseDto postDto = new PostResponseDto(post);
                    if (member != null) {
                        postDto.setLikes(likesRepository.isMemberLikesPostByEmail(post.getId(), member.getEmail()));
                    }
                    return postDto;
                }).collect(Collectors.toList());

        return postResponseDtos;
    }

    private Set<Post> wordsToPosts(String[] words) {
        Set<Post> resultSet = new LinkedHashSet<>(); // 순서 보장을 위해 LinkedHashSet 사용

        for (String word : words) {
            List<Post> posts = postRepository.findAllPostByQuery(word);
            resultSet.addAll(posts);
        }

        return resultSet;
    }

}