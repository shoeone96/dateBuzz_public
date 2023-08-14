package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.RecordRequestDto;
import com.dateBuzz.backend.controller.responseDto.RecordResponseDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.RecordService;
import com.dateBuzz.backend.util.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("")
    private Response<CustomPage<RecordResponseDto>> list(@PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable,
                                                   @RequestParam("sort") String sort){
        return Response.success(recordService.getList(pageable, sort));
    }

    @GetMapping("/login")
    private Response<CustomPage<RecordResponseDto>> listLogin(@PageableDefault(size = 5, sort = "recordedID", direction = Sort.Direction.ASC) Pageable pageable, Authentication authentication,
                                                              @RequestParam("sort") String sort){
        return Response.success(recordService.getListLogin(pageable, authentication.getName(), sort));
    }

    @PostMapping("")
    private Response<Void> records(@RequestBody RecordRequestDto requestDto, Authentication authentication) {
        recordService.writes(requestDto, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{recordId}")
    private Response<RecordResponseDto> getDetail(@PathVariable Long recordId){
        return Response.success(recordService.getRecord(recordId));
    }

    @GetMapping("/login/{recordId}")
    private Response<RecordResponseDto> getDetailLogin(@PathVariable Long recordId, Authentication authentication){
        return Response.success(recordService.getRecordLogin(recordId, authentication.getName()));
    }

    @DeleteMapping("/{recordId}")
    private Response<Long> delete(@PathVariable Long recordId, Authentication authentication){
        return Response.success(recordService.deleteArticle(recordId, authentication.getName()));
    }
}
