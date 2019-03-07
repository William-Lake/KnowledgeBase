package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.lakedev.KnowledgeBase.model.Task;
import com.lakedev.KnowledgeBase.model.TodoList;

public interface TaskRepository extends CrudRepository<Task, Integer>
{
	Page<Task> findByTextLikeIgnoreCase(String textFilter, Pageable pageable);
	
	long countByTextLikeIgnoreCase(String textFilter);
}
