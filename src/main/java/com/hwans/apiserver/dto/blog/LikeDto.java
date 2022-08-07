package com.hwans.apiserver.dto.blog;

import com.hwans.apiserver.dto.account.AccountDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Builder
@ApiModel(description = "좋아요 Dto")
public class LikeDto implements Serializable {
    @ApiModelProperty(value = "좋아요를 한 게시글", required = true)
    @NotBlank
    PostDto post;
    @ApiModelProperty(value = "좋아요를 한 사용자", required = true)
    @NotBlank
    AccountDto account;
}
