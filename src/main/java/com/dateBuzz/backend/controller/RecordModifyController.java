package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordRequestDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class RecordModifyController {

    private final RecordService recordService;

    @PatchMapping("/{recordId}")
    public Response<Void> modifyRecord(@PathVariable Long recordId, @RequestBody ModifyRecordRequestDto requestDto, Authentication authentication){
        recordService.modifyRecord(recordId, requestDto, authentication.getName());
        return Response.success();
    }
}
