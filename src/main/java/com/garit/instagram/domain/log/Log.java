package com.garit.instagram.domain.log;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")

@Entity
@Getter
@Table(name = "log_tb")
public abstract class Log {

    @Id @GeneratedValue
    @Column(name = "log_id")
    private Long id;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "crud_method")
    private CrudMethod crudMethod;

    @NotNull
    private String content;

    @NotNull
    @Column(name = "create_date")
    private LocalDateTime createDate;
}
