package com.example.application;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TodoRepository implements PanacheRepository<Todo> {
}