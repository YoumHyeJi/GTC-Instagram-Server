package com.garit.instagram.domain.chatting.roomMember;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.chatting.room.Room;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "room_member_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "room_member_unique_constraint",
                        columnNames = {"fk_room_id", "fk_member_id"}
                )
        }
)
public class RoomMember extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "room_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_room_id")
    @NotNull
    private Room room;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @Enumerated(value = STRING)
    @Column(name = "room_member_status")
    @NotNull
    private RoomMemberStatus roomMemberStatus;
}
