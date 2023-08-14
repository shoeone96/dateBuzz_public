package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.modify.AddPlacesRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.DeleteRecordedPlaceRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordedPlaceOrderRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordedPlaceRequestDto;
import com.dateBuzz.backend.controller.responseDto.Response;
import com.dateBuzz.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class RecordedPlaceController {

    private final RecordService recordService;
    @DeleteMapping("/{recordId}/placeDelete")
    private Response<Void> delete(@PathVariable Long recordId, Authentication authentication, @RequestBody DeleteRecordedPlaceRequestDto requestDto){
        return Response.success(recordService.deletePlace(recordId, authentication.getName(), requestDto));
    }

    @PostMapping("/{recordId}/addPlace")
    private Response<Void> addPlace(@PathVariable Long recordId, Authentication authentication, @RequestBody AddPlacesRequestDto requestDto){
        return Response.success(recordService.addPlace(recordId, authentication.getName(), requestDto));
    }

    @PatchMapping("/{recordId}/modifyPlace")
    private Response<Void> fixPlace(@PathVariable Long recordId, Authentication authentication, @RequestBody ModifyRecordedPlaceRequestDto requestDto){
        return Response.success(recordService.modifyPlace(recordId, authentication.getName(), requestDto));
    }

    @PatchMapping("/{recordId}/orderChange")
    private Response<Void> changeOrder(@PathVariable Long recordId, Authentication authentication, @RequestBody ModifyRecordedPlaceOrderRequestDto requestDto){
        return Response.success(recordService.changeOrder(recordId, authentication.getName(), requestDto));
    }

}
