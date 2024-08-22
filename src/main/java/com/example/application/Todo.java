package com.example.application;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import com.vaadin.hilla.Nullable;

@Entity
public class Todo extends PanacheEntity {
    private boolean done = false;
    @NotBlank
    private String task;

    public Todo() {
    }

    public Todo(String task) {
        this.task = task;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}