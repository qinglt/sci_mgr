package com.qing.config.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 竹秋廿四
 * @date 2021/05/12
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功提示码
    SUCCESS(200, "success"),

    // 未登录
    NOT_LOGIN(400, "Not logged in"),

    // 黑名单
    BLACK_USER(401, "black user"),

    // 无权限
    FORBIDDEN(403, "no authentication"),

    // 失败
    FAILURE(500, "failure"),

    // 超时
    TIMEOUT(599, "timeout"),

    // 服务器内部程序错误
    PROGRAM_INSIDE_EXCEPTION(501, "Program internal exception"),

    // 请求错误
    REQUEST_PARAM_ERROR(502, "Request parameter error");


    private final Integer code;
    private final String msg;
}
