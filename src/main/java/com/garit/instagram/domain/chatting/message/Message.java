package com.garit.instagram.domain.chatting.message;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.chatting.roomMember.RoomMember;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "message_tb")
public class Message extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_room_member_id")
    @NotNull
    private RoomMember roomMember;

    @NotNull
    private String content;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "message_status")
    private MessageStatus messageStatus;

}
