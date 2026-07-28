package com.caminha.javadailyexpenses.postgresutils.utils;

import lombok.Data;
//import org.springframework.data.annotation.Version;
//import org.springframework.data.domain.Persistable;
//import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.time.LocalDateTime;

//For a class extending an interface we use implements
//For an Interface extending an interface we use extends
// implements is only from Class to Interface
@Data
public abstract class PersistableEntity<T> implements Serializable
//        Persistable<T>
{
//    @Column("created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
//    @Column("updated_at")
    public LocalDateTime updatedAt = createdAt;
//    @Version
    public Integer version = 0;

//    @Override
    public boolean isNew(){
        return createdAt == updatedAt && version == 0;
    }

    public void updated() {
        this.updatedAt = LocalDateTime.now();
    }
}
