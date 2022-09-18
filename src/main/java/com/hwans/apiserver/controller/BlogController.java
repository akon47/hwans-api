package com.hwans.apiserver.controller;

import com.hwans.apiserver.common.Constants;
import com.hwans.apiserver.common.errors.errorcode.ErrorCodes;
import com.hwans.apiserver.common.errors.exception.RestApiException;
import com.hwans.apiserver.dto.blog.*;
import com.hwans.apiserver.dto.common.SliceDto;
import com.hwans.apiserver.entity.blog.OpenType;
import com.hwans.apiserver.service.authentication.CurrentAuthenticationDetails;
import com.hwans.apiserver.service.authentication.CurrentAuthenticationDetailsOrElseNull;
import com.hwans.apiserver.service.authentication.UserAuthenticationDetails;
import com.hwans.apiserver.service.blog.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@Api(tags = "블로그")
@RequestMapping(value = Constants.API_PREFIX)
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @ApiOperation(value = "전체 블로그 게시글 조회", notes = "전체 블로그 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/posts")
    public SliceDto<SimplePostDto> getAllPosts(@ApiParam(value = "페이징 조회를 위한 CursorId") @RequestParam(required = false) Optional<UUID> cursorId,
                                               @ApiParam(value = "조회할 최대 페이지 수") @RequestParam(required = false, defaultValue = "20") int size) {
        return blogService.getAllPosts(cursorId, size);
    }

    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/posts")
    public PostDto createPost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                              @ApiParam(value = "게시글", required = true) @RequestBody @Valid final PostRequestDto postRequestDto) {
        return blogService.createPost(userAuthenticationDetails.getId(), postRequestDto);
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.", tags = "블로그")
    @PutMapping(value = "/v1/blog/posts/{postUrl}")
    public PostDto modifyPost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                              @ApiParam(value = "게시글 Url") @PathVariable String postUrl,
                              @ApiParam(value = "게시글", required = true) @RequestBody @Valid final PostRequestDto postRequestDto) {
        return blogService.modifyPost(userAuthenticationDetails.getBlogId(), postUrl, postRequestDto);
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.", tags = "블로그")
    @DeleteMapping(value = "/v1/blog/posts/{postUrl}")
    public void modifyPost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                           @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        blogService.deletePost(userAuthenticationDetails.getBlogId(), postUrl);
    }

    @ApiOperation(value = "특정 블로그 전체 게시글 조회", notes = "특정 블로그 전체 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}/posts")
    public SliceDto<SimplePostDto> getBlogPosts(@CurrentAuthenticationDetailsOrElseNull UserAuthenticationDetails userAuthenticationDetails,
                                                @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                                                @ApiParam(value = "페이징 조회를 위한 CursorId") @RequestParam(required = false) Optional<UUID> cursorId,
                                                @ApiParam(value = "조회할 최대 페이지 수") @RequestParam(required = false, defaultValue = "20") int size) {
        boolean findPublicPostOnly = userAuthenticationDetails == null || !userAuthenticationDetails.getBlogId().equals(blogId);
        return blogService.getBlogPosts(blogId, cursorId, size, findPublicPostOnly);
    }

    @ApiOperation(value = "특정 블로그 주인이 좋아요 한 전체 게시글 조회", notes = "특정 블로그 주인이 좋아요 한 전체 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}/likes")
    public SliceDto<SimplePostDto> getBloggerLikePosts(@ApiParam(value = "블로그 Id") @PathVariable String blogId,
                                                       @ApiParam(value = "페이징 조회를 위한 CursorId") @RequestParam(required = false) Optional<UUID> cursorId,
                                                       @ApiParam(value = "조회할 최대 페이지 수") @RequestParam(required = false, defaultValue = "20") int size) {
        return blogService.getBloggerLikePosts(blogId, cursorId, size);
    }

    @ApiOperation(value = "블로그 게시글 조회", notes = "블로그 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}/posts/{postUrl}")
    public PostDto getPost(@CurrentAuthenticationDetailsOrElseNull UserAuthenticationDetails userAuthenticationDetails,
                           @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                           @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        boolean findPublicPostOnly = userAuthenticationDetails == null || !userAuthenticationDetails.getBlogId().equals(blogId);
        var post = blogService.getPost(blogId, postUrl);
        if (findPublicPostOnly && post.getOpenType() != OpenType.PUBLIC) {
            new RestApiException(ErrorCodes.NotFound.NOT_FOUND_POST);
        }
        return post;
    }

    @ApiOperation(value = "블로그 정보 조회", notes = "블로그 정보를 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}")
    public BlogDetailsDto getBlogDetails(@ApiParam(value = "블로그 Id") @PathVariable String blogId) {
        return blogService.getBlogDetails(blogId);
    }

    @ApiOperation(value = "댓글 작성", notes = "게시글에 댓글을 작성한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/{blogId}/posts/{postUrl}/comments")
    public CommentDto createComment(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                                    @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                                    @ApiParam(value = "게시글 Url") @PathVariable String postUrl,
                                    @ApiParam(value = "댓글", required = true) @RequestBody @Valid final CommentRequestDto commentRequestDto) {
        return blogService.createComment(userAuthenticationDetails.getId(), blogId, postUrl, commentRequestDto);
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글을 수정한다.", tags = "블로그")
    @PutMapping(value = "/v1/blog/comments/{commentId}")
    public CommentDto modifyComment(@ApiParam(value = "댓글 Id") @PathVariable UUID commentId,
                                    @ApiParam(value = "댓글", required = true) @RequestBody @Valid final CommentRequestDto commentRequestDto) {
        return blogService.modifyComment(commentId, commentRequestDto);
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.", tags = "블로그")
    @DeleteMapping(value = "/v1/blog/comments/{commentId}")
    public void deleteComment(@ApiParam(value = "댓글 Id") @PathVariable UUID commentId) {
        blogService.deleteComment(commentId);
    }

    @ApiOperation(value = "댓글 조회", notes = "댓글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/comments/{commentId}")
    public CommentDto getComment(@ApiParam(value = "댓글 Id") @PathVariable UUID commentId) {
        return blogService.getComment(commentId);
    }

    @ApiOperation(value = "대댓글 작성", notes = "댓글에 댓글을 작성한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/comments/{commentId}")
    public CommentDto createCommentToComment(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                                             @ApiParam(value = "댓글 Id") @PathVariable UUID commentId,
                                             @ApiParam(value = "댓글", required = true) @RequestBody @Valid final CommentRequestDto commentRequestDto) {
        return blogService.createComment(userAuthenticationDetails.getId(), commentId, commentRequestDto);
    }

    @ApiOperation(value = "게시글 좋아요 하기", notes = "게시글을 좋아요 한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/{blogId}/posts/{postUrl}/likes")
    public void likePost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                         @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                         @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        blogService.likePost(userAuthenticationDetails.getId(), blogId, postUrl);
    }

    @ApiOperation(value = "게시글 좋아요 취소하기", notes = "게시글 좋아요를 취소한다.", tags = "블로그")
    @DeleteMapping(value = "/v1/blog/{blogId}/posts/{postUrl}/likes")
    public void unlikePost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                           @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                           @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        blogService.unlikePost(userAuthenticationDetails.getId(), blogId, postUrl);
    }

    @ApiOperation(value = "게시글에 좋아요를 했는지 여부", notes = "게시글에 좋아요를 했는지 여부를 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}/posts/{postUrl}/likes")
    public ResponseEntity isLikePost(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                                     @ApiParam(value = "블로그 Id") @PathVariable String blogId,
                                     @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        var exists = blogService.isLikePost(userAuthenticationDetails.getId(), blogId, postUrl);
        return exists ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
