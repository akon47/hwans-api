package com.hwans.apiserver.controller;

import com.hwans.apiserver.common.Constants;
import com.hwans.apiserver.dto.blog.*;
import com.hwans.apiserver.dto.common.SliceDto;
import com.hwans.apiserver.service.account.AccountService;
import com.hwans.apiserver.service.blog.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@Api(tags = "블로그")
@RequestMapping(value = Constants.API_PREFIX)
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final AccountService accountService;

    @ApiOperation(value = "전체 블로그 게시글 조회", notes = "전체 블로그 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/posts")
    public SliceDto<SimplePostDto> getAllPost(@ApiParam(value = "페이징 조회를 위한 CursorId") @RequestParam(required = false) Optional<UUID> cursorId,
                                              @ApiParam(value = "조회할 최대 페이지 수") @RequestParam(required = false, defaultValue = "20") int size) {
        return blogService.getAllPosts(cursorId, size);
    }

    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/posts")
    public PostDto createPost(@ApiParam(value = "게시글", required = true) @RequestBody PostRequestDto postRequestDto) {
        var currentAccountEmail = accountService.getCurrentAccountEmail();
        return blogService.createPost(currentAccountEmail, postRequestDto);
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.", tags = "블로그")
    @PutMapping(value = "/v1/blog/posts/{postUrl}")
    public PostDto modifyPost(@ApiParam(value = "게시글 Url") @PathVariable String postUrl,
                              @ApiParam(value = "게시글", required = true) @RequestBody PostRequestDto postRequestDto) {
        var account = accountService.getCurrentAccount();
        return blogService.modifyPost(account.getBlogId(), postUrl, postRequestDto);
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.", tags = "블로그")
    @DeleteMapping(value = "/v1/blog/posts/{postUrl}")
    public void modifyPost(@ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        var account = accountService.getCurrentAccount();
        blogService.deletePost(account.getBlogId(), postUrl);
    }

    @ApiOperation(value = "블로그 게시글 조회", notes = "블로그 게시글을 조회한다.", tags = "블로그")
    @GetMapping(value = "/v1/blog/{blogId}/posts/{postUrl}")
    public PostDto getPost(@ApiParam(value = "블로그 Id") @PathVariable String blogId,
                           @ApiParam(value = "게시글 Url") @PathVariable String postUrl) {
        return blogService.getPost(blogId, postUrl);
    }

    @ApiOperation(value = "댓글 작성", notes = "게시글에 댓글을 작성한다.", tags = "블로그")
    @PostMapping(value = "/v1/blog/{blogId}/{postUrl}/comments")
    public CommentDto createComment(@ApiParam(value = "블로그 Id") @PathVariable String blogId,
                                 @ApiParam(value = "게시글 Url") @PathVariable String postUrl,
                                 @ApiParam(value = "댓글", required = true) @RequestBody CommentRequestDto commentRequestDto) {
        return blogService.createComment(blogId, postUrl, commentRequestDto);
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글을 수정한다.", tags = "블로그")
    @PutMapping(value = "/v1/blog/comments/{commentId}")
    public CommentDto modifyComment(@ApiParam(value = "댓글 Id") @PathVariable String commentId,
                                 @ApiParam(value = "댓글", required = true) @RequestBody CommentRequestDto commentRequestDto) {
        return blogService.modifyComment(commentId, commentRequestDto);
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 수정한다.", tags = "블로그")
    @DeleteMapping(value = "/v1/blog/comments/{commentId}")
    public void deleteComment(@ApiParam(value = "댓글 Id") @PathVariable String commentId) {
        blogService.deleteComment(commentId);
    }
}
