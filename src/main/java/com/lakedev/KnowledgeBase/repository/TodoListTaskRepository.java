package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.repository.CrudRepository;

import com.lakedev.KnowledgeBase.model.TodoListTask;

public interface TodoListTaskRepository extends CrudRepository<TodoListTask, Integer>
{

}

