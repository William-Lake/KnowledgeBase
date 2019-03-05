package com.lakedev.KnowledgeBase.ui.tab;

import com.lakedev.KnowledgeBase.repository.TaskRepository;
import com.lakedev.KnowledgeBase.repository.TodoListRepository;
import com.lakedev.KnowledgeBase.repository.TodoListTaskRepository;
import com.vaadin.ui.VerticalLayout;

public class TabTodoList extends VerticalLayout
{

	private TodoListRepository todoListRepository;
	
	private TaskRepository taskRepository;
	
	private TodoListTaskRepository todoListTaskRepository;

	public TabTodoList(TodoListRepository todoListRepository, TaskRepository taskRepository,
			TodoListTaskRepository todoListTaskRepository)
	{
		this.todoListRepository = todoListRepository;
		
		this.taskRepository = taskRepository;
		
		this.todoListTaskRepository = todoListTaskRepository;
	}
	
}
