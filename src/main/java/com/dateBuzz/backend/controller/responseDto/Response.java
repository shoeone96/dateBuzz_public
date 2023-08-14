package com.dateBuzz.backend.controller.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

    private String resultCode;
    private T result;

    /**
     *
     * @return Response("SUCCESS")
     * 성공 후 반환할 값이 없을 때 이용하는 메서드
     */
    public static Response<Void> success() {
        return new Response<Void>("SUCCESS", null);
    }

    /**
     *
     * @param result
     * @return Response<T></T>
     * @param <T>
     *     성공한 결과물과 함게 성공 메시지를 전달하는 메서드
     */
    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }

    public String toStream() {
        if(result == null){
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"" + "\n" +
                    "\"result\":" + null + "}";
        }
        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"" + "\n" +
                "\"result\":" + "\"" + result + "\"" + "}";
    }
}
