package com.qing.config.response;

import lombok.Data;

/**
 * @author 竹秋廿四
 * @date 2021/05/12
 */
@Data
public class Result<T> {

    private Integer code;

    private String msg;

    private T obj;

    /**
     * 成功
     */
    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }

    /**
     * 成功，有返回数据
     */
    public static <V> Result<V> success(V obj) {
        Result<V> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setObj(obj);
        return result;
    }

    /**
     * 失败
     */
    public static Result<Void> failure() {
        Result<Void> result = new Result<>();
        result.setCode(ResultCode.FAILURE.getCode());
        result.setMsg(ResultCode.FAILURE.getMsg());
        return result;
    }

    /**
     * 失败，自定义失败信息
     */
    public static Result<Void> failure(String message) {
        Result<Void> result = new Result<>();
        result.setCode(ResultCode.FAILURE.getCode());
        result.setMsg(message);
        return result;
    }

    /**
     * 失败，使用已定义枚举
     */
    public static Result<Void> failure(ResultCode resultCode) {
        Result<Void> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        return result;
    }

    /**
     * 自定义失败
     */
    public static <V> Result<V> failure(ResultCode resultCode, V obj) {
        Result<V> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setObj(obj);
        return result;
    }
}
