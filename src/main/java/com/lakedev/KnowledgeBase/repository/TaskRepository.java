package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.repository.CrudRepository;

import com.lakedev.KnowledgeBase.model.Task;

public interface TaskRepository extends CrudRepository<Task, Integer>
{

}
