package com.garit.instagram.domain.base;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @NotNull
    @Enumerated(value = EnumType.STRING)
    protected Status status;

    @NotNull
    @Column(name = "create_date")
    protected LocalDateTime createDate;

    @Column(name = "update_date")
    protected LocalDateTime updateDate;

    @Column(name = "delete_date")
    protected LocalDateTime deleteDate;

    public void changeUpdateAt(){
        this.updateDate = LocalDateTime.now();
    }

    public void changeDeleteAt(){
        this.deleteDate = LocalDateTime.now();
    }
}
