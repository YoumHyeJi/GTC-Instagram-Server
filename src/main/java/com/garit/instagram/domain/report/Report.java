package com.garit.instagram.domain.report;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")

@Entity
@Getter
@Table(name = "report_tb")
public abstract class Report extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "reason")
    private ReportReason reportReason;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "report_status")
    private ReportStatus reportStatus;
}
