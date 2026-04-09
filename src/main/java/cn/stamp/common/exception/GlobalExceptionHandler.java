package cn.stamp.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.stamp.common.api.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     *
     * @param e 方法参数无效异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        return ApiResponse.fail(msg);
    }

    /**
     * 处理未登录或登录失效异常
     *
     * @param e 未登录异常
     * @return 统一响应结果
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<Void> handleNotLoginException(NotLoginException e) {
        return ApiResponse.fail("未登录或登录已失效");
    }

    /**
     * 处理非法参数异常（业务异常）
     *
     * @param e 非法参数异常
     * @return 统一响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleBizException(IllegalArgumentException e) {
        return ApiResponse.fail(e.getMessage());
    }

    /**
     * 上传文件超过配置大小
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<Void> handleMaxUpload(MaxUploadSizeExceededException e) {
        return ApiResponse.fail("上传文件过大，请压缩后重试或调整服务端限制");
    }

    /**
     * 处理系统未知异常
     *
     * @param e 通用异常
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.fail("系统异常: " + e.getMessage());
    }
}

