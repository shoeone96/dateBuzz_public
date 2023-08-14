package com.dateBuzz.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATED_NAME(HttpStatus.CONFLICT, "이미 존재하는 유저 정보입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "해당 유저가 작성한 게시물이 아닙니다."),
    S3_UPLOAD_PROBLEM(HttpStatus.CONFLICT, "이미지 업로드 중 문제가 발생했습니다."),
    PYTHON_READING_PROBLEM(HttpStatus.CONFLICT, "파이썬 파일 확인 중 문제가 발생했습니다."),
    PATH_PROBLEM(HttpStatus.CONFLICT, "파이썬 파일의 경로가 잘못되었습니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),
    MAX_PLACES(HttpStatus.UNAUTHORIZED, "5개 초과의 장소를 등록하실 수 없습니다."),
    HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 해시태그입니다."),
    RABBITMQ_ERROR(HttpStatus.CONFLICT, "RabbitMQ Error");
    private HttpStatus status;
    private String message;
}
