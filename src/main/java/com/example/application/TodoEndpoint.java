package com.example.application;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Endpoint
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