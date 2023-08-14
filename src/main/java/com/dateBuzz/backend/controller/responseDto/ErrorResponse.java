package com.dateBuzz.backend.controller.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse<T>  {

    private String reason;
    private T errorCategory;

    /**
     * @param errorCode
     * @return Response(errorCode, null)
     * 에러 발생 시 에러 코드를 반환하는 메서드
     */
    public static ErrorResponse<HttpStatus> error(String errorCode, HttpStatus httpStatus){
        return new ErrorResponse<>(errorCode, httpStatus);
    }

    public static ErrorResponse<String> error(String errorCode){
        return new ErrorResponse<>(errorCode, null);
    }
    public String toStream() {
        if(errorCategory == null){
            return "{" +
                    "\"resultCode\":" + "\"" + reason + "\"" + "\n" +
                    "\"result\":" + null + "}";
        }
        return "{" +
                "\"resultCode\":" + "\"" + reason + "\"" + "\n" +
                "\"result\":" + "\"" + errorCategory + "\"" + "}";
    }
}
