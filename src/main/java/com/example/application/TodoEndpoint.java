package com.example.application;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.Nonnull;

@BrowserCallable
@AnonymousAllowed
public class TodoEndpoint {
    private TodoRepository repository;

    public TodoEndpoint(TodoRepository repository) {
        this.repository = repository;
    }

    public @Nonnull List<@Nonnull Todo> findAll() {
        return repository.listAll();
    }

    @Transactional
    public Todo create(@Valid Todo todo) {
        repository.persist(todo);
        return todo;
    }

    @Transactional
    public Todo update(Todo todo) {
        Todo entity = repository.findById(todo.getId());
        entity.setDone(todo.isDone());
        entity.setTask(todo.getTask());
        return entity;
    }
}