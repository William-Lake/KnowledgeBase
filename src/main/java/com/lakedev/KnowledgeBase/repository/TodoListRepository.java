package com.lakedev.KnowledgeBase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.lakedev.KnowledgeBase.model.SavedNote;
import com.lakedev.KnowledgeBase.model.TodoList;

public interface TodoListRepository extends CrudRepository<TodoList, Integer>
{
	Page<TodoList> findByNameLikeIgnoreCase(String nameFilter, Pageable pageable);
	
	long countByNameLikeIgnoreCase(String nameFilter);
}
