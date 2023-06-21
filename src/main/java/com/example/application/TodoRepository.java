package com.example.application;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TodoRepository implements PanacheRepository<Todo> {
}