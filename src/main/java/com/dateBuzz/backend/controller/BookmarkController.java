package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.responseDto.BookmarkResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    @PostMapping("community/{recordId}/bookmark")
    public Response<BookmarkResponseDto> bookmarkRecord(@PathVariable Long recordId, Authentication authentication){
        return Response.success(bookmarkService.bookmark(recordId, authentication.getName()));
    }
}
