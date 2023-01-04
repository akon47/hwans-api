package com.hwans.apiserver.common;

/**
 * 공통적으로 사용할 상수 필드
 */
public final class Constants {
    public static final String API_PREFIX = "/api";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "X-Auth-Refresh-Token";
    public static final Long ACCESS_TOKEN_EXPIRES_TIME = 60 * 30 * 1000L;
    public static final Long REFRESH_TOKEN_EXPIRES_TIME = 60 * 60 * 24 * 30 * 1000L;
    public static final Long REGISTER_TOKEN_EXPIRES_TIME = 60 * 60 * 24 * 1000L;
    public static final Long EMAIL_VERIFY_CODE_EXPIRES_TIME = 60 * 3 * 1000L;
    public static final Long PASSWORD_RESET_TOKEN_EXPIRES_TIME = 60 * 3 * 1000L;

    public static final Long MAX_ATTACHMENT_FILE_SIZE = 1024 * 1024 * 100L; // 100MB
}
