package com.ddi.assessment.news.api.dto;

public class ResultResponse <T> {

    private boolean success;       // 성공 여부
    private String message;        // 응답 메시지
    private T data;                // 응답 데이터

    private ResultResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>(true, "요청에 성공했습니다.", data);
    }

    public static <T> ResultResponse<T> success(String message, T data) {
        return new ResultResponse<>(true, message, data);
    }

    public static <T> ResultResponse<T> success() {
        return new ResultResponse<>(true, "요청에 성공했습니다.", null);
    }

    public static <T> ResultResponse<T> failure(String message) {
        return new ResultResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
