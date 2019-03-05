package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.repository.CrudRepository;

import com.lakedev.KnowledgeBase.model.TodoList;

public interface TodoListRepository extends CrudRepository<TodoList, Integer>
{

}
