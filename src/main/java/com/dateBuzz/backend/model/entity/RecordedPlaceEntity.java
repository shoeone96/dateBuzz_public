package com.dateBuzz.backend.model.entity;


import com.dateBuzz.backend.controller.requestDto.RecordedPlaceRequestDto;
import com.dateBuzz.backend.controller.requestDto.modify.ModifyRecordedPlaceInfoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"recorded_place\"")
@Getter
public class RecordedPlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private RecordEntity record;

    private int orders;
    private String placeName;
    private String address;
    private String addressGu;
    @Column(name = "address_x")
    private String addressX;
    @Column(name = "address_y")
    private String addressY;
    private String placeContent;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static RecordedPlaceEntity fromRecordedRequestDtoAndRecordEntity(RecordedPlaceRequestDto recordedPlaceDto, RecordEntity record) {
        RecordedPlaceEntity recordedPlace = new RecordedPlaceEntity();
        recordedPlace.record = record;
        recordedPlace.orders = recordedPlaceDto.getOrders();
        recordedPlace.placeName = recordedPlaceDto.getPlaceName();
        recordedPlace.address = recordedPlaceDto.getAddress();
        recordedPlace.addressGu = recordedPlaceDto.getAddressGu();
        recordedPlace.addressX = recordedPlaceDto.getAddressX();
        recordedPlace.addressY = recordedPlaceDto.getAddressY();
        recordedPlace.placeContent = recordedPlaceDto.getPlaceContent();
        return recordedPlace;
    }

    @PrePersist
    void registeredAt(){
        this.createdAt = Timestamp.from(Instant.now());
    }
    @PreUpdate
    void updatedAt(){
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public void modifyPlaceInfo(ModifyRecordedPlaceInfoRequestDto requestDto) {
        this.placeContent = requestDto.getNewPlaceContent();
    }

    public void reduceOrder(){
        this.orders --;
    }

    public void increaseOrder(){
        this.orders ++;
    }

    public void changeOrder(int newOrder){
        this.orders = newOrder;
    }
}
