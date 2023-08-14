package com.dateBuzz.backend.controller.requestDto.modify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecordedOrder {
    private Long placeId;
    private int newOrders;
}
