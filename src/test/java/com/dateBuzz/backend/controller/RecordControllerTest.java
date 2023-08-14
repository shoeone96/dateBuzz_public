package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.*;
import com.dateBuzz.backend.controller.responseDto.UserLoginResponseDto;
import com.dateBuzz.backend.service.RecordService;
import com.dateBuzz.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordService recordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @Rollback(value = false)
    void 게시글_작성_받아오기() throws Exception {

        //Todo: mocking 데이터 삽입

        String title = "title";
        String dateDate = "dateDate";
        String recordedContent = "recordedContent";

        userService.login(new UserLoginRequestDto("test02", "test123"));


        List<HashtagRequestDto> hashtagList = new ArrayList<>();
        hashtagList.add(new HashtagRequestDto("tagName1", "ACTIVITY"));
        hashtagList.add(new HashtagRequestDto("tagName2", "ACTIVITY"));

        List<RecordedPlaceRequestDto> placeList = new ArrayList<>();
        int order1 = 1;
        int order2 = 2;
        int order3 = 3;
        String placeName = "placeName";
        String address = "address";
        String addressGu = "addressGu";
        String addressX = "addressX";
        String addressY = "addressY";
        String placeContent = "placeContent";

        PlaceImageRequestDto img1 = PlaceImageRequestDto
                .builder()
                .orders(1)
                .fileName("name")
                .contentType("json")
                .build();
        PlaceImageRequestDto img2 = PlaceImageRequestDto
                .builder()
                .orders(2)
                .fileName("name")
                .contentType("json")
                .build();
        List<PlaceImageRequestDto> imgList = new ArrayList<>();
        imgList.add(img1);
        imgList.add(img2);

        RecordedPlaceRequestDto place1 = RecordedPlaceRequestDto.builder()
                .orders(order1)
                .placeName(placeName)
                .address(address)
                .addressGu(addressGu)
                .addressX(addressX)
                .addressY(addressY)
                .placeContent(placeContent)
                .images(imgList)
                .build();
        RecordedPlaceRequestDto place2 = RecordedPlaceRequestDto.builder()
                .orders(order2)
                .placeName(placeName)
                .address(address)
                .addressGu(addressGu)
                .addressX(addressX)
                .addressY(addressY)
                .placeContent(placeContent)
                .images(imgList)
                .build();
        RecordedPlaceRequestDto place3 = RecordedPlaceRequestDto.builder()
                .orders(order3)
                .placeName(placeName)
                .address(address)
                .addressGu(addressGu)
                .addressX(addressX)
                .addressY(addressY)
                .placeContent(placeContent)
                .build();
        placeList.add(place1);
        placeList.add(place2);
        placeList.add(place3);

        String exposore = "YES";
        new RecordRequestDto();

        for (int i = 0; i < 100; i++) {
            recordService.writes(RecordRequestDto.builder()
                    .recordedContent(recordedContent + 1)
                    .recordedPlaces(placeList)
                    .exposure(exposore)
                    .hashtags(hashtagList)
                    .title(title)
                    .dateDate(dateDate)
                    .build(), "test02");

        }
    }

    @Test
    void getList_100() throws Exception {
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/community?page=0&size=2&sort=latest").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getList() throws Exception {
        mockMvc.perform(get("/community?page=0&size=2&sort=latest").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getOne() throws Exception {
        mockMvc.perform(get("/community/100").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
