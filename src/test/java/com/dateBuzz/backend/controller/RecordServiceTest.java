package com.dateBuzz.backend.controller;

import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordRequestDto;
import com.dateBuzz.backend.service.RecordService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecordServiceTest {

    @Autowired
    RecordService recordService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Transactional
    @Rollback
    void recordDeleteTest() throws Exception {
        //given
        long deleteId = 102L;


        //when
        Long test02 = recordService.deleteArticle(deleteId, "test02");

        //then
        Assertions.assertThat(test02).isEqualTo(102L);
        mockMvc.perform(get("/community?page=0&size=2&sort=latest").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Transactional
    @Rollback
    void recordModifyTest() throws Exception {
        //given
        long modifyingId = 102L;
        ModifyRecordRequestDto dto = new ModifyRecordRequestDto("changedTitle", "changedDate", "changedRecordContent");

        //when
        recordService.modifyRecord(modifyingId, dto,"test02");

        //then
        mockMvc.perform(get("/community?page=0&size=2&sort=latest").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
