package com.garit.instagram.domain.deviceToken;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "device_token_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "member_device_token_value_unique_constraint",
                        columnNames = {"fk_member_id", "device_token_value"}
                )
        }
)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "device_token_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @Column(name = "device_token_value", length = 200)
    @NotNull
    private String deviceTokenValue;

    @Enumerated(value = STRING)
    @Column(name = "device_token_status")
    @NotNull
    private DeviceTokenStatus deviceTokenStatus;

}

