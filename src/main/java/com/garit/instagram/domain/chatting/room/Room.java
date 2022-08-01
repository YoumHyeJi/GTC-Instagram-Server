package com.garit.instagram.domain.chatting.room;

import com.garit.instagram.domain.base.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Table(name = "room_tb")
public class Room extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "room_id")
    private Long id;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "room_status")
    private RoomStatus roomStatus;

}
