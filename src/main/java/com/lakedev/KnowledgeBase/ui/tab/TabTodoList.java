package com.lakedev.KnowledgeBase.ui.tab;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.lakedev.KnowledgeBase.model.SavedNote;
import com.lakedev.KnowledgeBase.model.Task;
import com.lakedev.KnowledgeBase.model.TodoList;
import com.lakedev.KnowledgeBase.repository.SavedNoteRepository;
import com.lakedev.KnowledgeBase.repository.TaskRepository;
import com.lakedev.KnowledgeBase.repository.TodoListRepository;
import com.lakedev.KnowledgeBase.repository.TodoListTaskRepository;
import com.lakedev.KnowledgeBase.ui.dialog.ConfirmationDialog;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.data.provider.Sort;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class TabTodoList extends VerticalLayout
{
	private static final long serialVersionUID = 5136279387587998752L;

	private TodoListRepository todoListRepository;
	
	private TaskRepository taskRepository;
	
	private TodoListTaskRepository todoListTaskRepository;
	
	private TodoList currentTodoList;
	
	private Button btnSave;
	
	private Button btnClear;
	
	private TextField txtTodoListName;
	
	private Grid<Task> grdTask;
	
	private Grid<TodoList> grdTodoList;
	
	/*
	 * Todo 
	 */

	public TabTodoList(TodoListRepository todoListRepository, TaskRepository taskRepository,
			TodoListTaskRepository todoListTaskRepository)
	{
		this.todoListRepository = todoListRepository;
		
		this.taskRepository = taskRepository;
		
		this.todoListTaskRepository = todoListTaskRepository;
		
		// TITLE FIELD ====================================
		
		txtTodoListName = new TextField();
		
		txtTodoListName.setDescription("TodoList Name");
		
		txtTodoListName.setSizeFull();
		
		txtTodoListName.setCaption("TodoList Name");
		
		txtTodoListName.addValueChangeListener((valueChanged) -> 
		{
			btnSave.setEnabled(true);
			
			btnClear.setEnabled(true);
		});
		
		// SAVE BUTTON ==================================
		
		btnSave = new Button(VaadinIcons.CHECK);
		
		btnSave.setDescription("Save Changes");
		
		btnSave.addStyleName("friendly");
		
		btnSave.setEnabled(false);
		
		btnSave.addClickListener((btnClicked) -> 
		{
			// TODO Validate input
			
			/*
			 * TODO Create/Save new todo list
			 * TODO Create/Save each new todo list task
			 * TODO Create/save a new todolist task for each todo list item.
			 */
			
//			TodoList todoList = 
//					
//					currentTodoList == null ?
//							
//							new TodoList() :
//								
//								currentTodoList;
//							
//			todoList.setName(txtTodoListName.getValue());
//			
//			todoList.setTodoListTasks();
//			
//			savedNoteRepository.save(savedNote);
//			
//			grdTodoList.getDataProvider().refreshAll();
//			
//			btnSave.setEnabled(false);
		});
		
		// CLEAR BUTTON ======================================
		
		btnClear = new Button(VaadinIcons.CLOSE_CIRCLE);
		
		btnClear.setDescription("Clear TodoList");
		
		btnClear.setEnabled(false);
		
		btnClear.addStyleName("danger");
		
		btnClear.addClickListener((btnClicked) -> clearTodoList(currentTodoList));
		
		// TODOLIST TABLE =======================================
		
		grdTodoList = new Grid<>();
		
		grdTodoList.setSizeFull();
		
		grdTodoList.setSelectionMode(SelectionMode.SINGLE);
		
		////// BUILDING COLUMNS
		
		// Note Title Column
		grdTodoList
		.addColumn(TodoList::getName)
		.setId("TodoListName")
		.setCaption("Name")
		.setWidthUndefined();
		
		// Delete Button Column
		grdTodoList.addComponentColumn(todoList -> 
		{
			Button btnDelete = new Button(VaadinIcons.TRASH);
			
			btnDelete.setDescription("Delete TodoList");
			
			btnDelete.addStyleName("danger");
			
			btnDelete.addClickListener(clicked -> deleteNote(todoList));
			
			return btnDelete;
			
		})
		.setCaption("Delete")
		.setWidth(90);
		
		grdTodoList
		.getColumns()
		.stream()
		.forEach(column -> column.setHidable(false));
		
		////// ADDING LISTENER
		grdTodoList.addItemClickListener((itemClicked) -> 
		{
			/*
			 * We want a TodoList double clicked in the Table
			 * to be added to the current editor.
			 * We also want to check with the user if unsaved
			 * data is going to be overwritten before doing so.
			 */
			if (itemClicked.getMouseEventDetails().isDoubleClick())
			{
				clearTodoList(itemClicked.getItem());
			} 
		});
		
		grdTodoList.setColumnReorderingAllowed(false);
		
		////// BUILDING JPA DATAPROVIDER
		/*
		 * RESOURCES
		 * https://vaadin.com/directory/component/spring-data-provider-add-on/1.1.0/links
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/backend/PersonRepository.java
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/vaadin/MainUI.java
		 */
	    FilterablePageableDataProvider<TodoList, Object> dataProvider = new FilterablePageableDataProvider<TodoList, Object>() 
	    {
			private static final long serialVersionUID = 4882913761882790088L;

			@Override
	        protected Page<TodoList> fetchFromBackEnd(Query<TodoList, Object> query,Pageable pageable) 
	        {
	            return todoListRepository.findByTodoListNameLikeIgnoreCase(getRepoFilter(), pageable);
	        }

	        @Override
	        protected int sizeInBackEnd(Query<TodoList, Object> query) 
	        {
	            return (int) todoListRepository.countByTodoListNameLikeIgnoreCase(getRepoFilter());
	        }

	        private String getRepoFilter() 
	        {
	        	String filter = getOptionalFilter().orElse("");
	        	
	            return "%" + filter + "%";
	        }

	        @Override
	        protected List<QuerySortOrder> getDefaultSortOrders() 
	        {
	            return Sort.asc("noteTitle").build();
	        }

	    };
	    
	    grdTodoList.setDataProvider(dataProvider);
	    
	    grdTodoList.getDataProvider().refreshAll();
	    
	    ////// ADDING NOTE FILTER CONTROL
	    
		HeaderRow filteringHeader = grdTodoList.appendHeaderRow();
		
		TextField txtNameFilter = new TextField();
		
		txtNameFilter.setDescription("Search/Filter TodoLists");
		
		txtNameFilter.setWidth("100%");
		
		txtNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		
		txtNameFilter.setPlaceholder("Name Filter");
		
		txtNameFilter.addValueChangeListener((valueChanged) -> dataProvider.setFilter(txtNameFilter.getValue()));
		
		// This gathers the Note Title column via it's id, added in the "Building Columns" section.
		filteringHeader
		.getCell("TodoListName")
		.setComponent(txtNameFilter);
		
		// TASK TABLE =======================================
		
		grdTask = new Grid<>();
		
		grdTask.setSizeFull();
		
		grdTask.setSelectionMode(SelectionMode.SINGLE);
		
		////// BUILDING COLUMNS
		
		grdTask.addComponentColumn(task -> 
		{
			CheckBox chkTask = new CheckBox();
			
			
		});
		
		// Note Title Column
		grdTask
		.addColumn(Task::getText)
		.setId("TaskText")
		.setCaption("Text")
		.setWidthUndefined();
		
		// Delete Button Column
		grdTask.addComponentColumn(task -> 
		{
			Button btnDelete = new Button(VaadinIcons.TRASH);
			
			btnDelete.setDescription("Delete Task");
			
			btnDelete.addStyleName("danger");
			
			btnDelete.addClickListener(clicked -> deleteTask(task));
			
			return btnDelete;
			
		})
		.setCaption("Delete")
		.setWidth(90);
		
		grdTask
		.getColumns()
		.stream()
		.forEach(column -> column.setHidable(false));
		

		
		grdTask.setColumnReorderingAllowed(false);
		
		////// BUILDING JPA DATAPROVIDER
		/*
		 * RESOURCES
		 * https://vaadin.com/directory/component/spring-data-provider-add-on/1.1.0/links
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/backend/PersonRepository.java
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/vaadin/MainUI.java
		 */
	    FilterablePageableDataProvider<TodoList, Object> dataProvider = new FilterablePageableDataProvider<TodoList, Object>() 
	    {
			private static final long serialVersionUID = 4882913761882790088L;

			@Override
	        protected Page<TodoList> fetchFromBackEnd(Query<TodoList, Object> query,Pageable pageable) 
	        {
	            return todoListRepository.findByTodoListNameLikeIgnoreCase(getRepoFilter(), pageable);
	        }

	        @Override
	        protected int sizeInBackEnd(Query<TodoList, Object> query) 
	        {
	            return (int) todoListRepository.countByTodoListNameLikeIgnoreCase(getRepoFilter());
	        }

	        private String getRepoFilter() 
	        {
	        	String filter = getOptionalFilter().orElse("");
	        	
	            return "%" + filter + "%";
	        }

	        @Override
	        protected List<QuerySortOrder> getDefaultSortOrders() 
	        {
	            return Sort.asc("noteTitle").build();
	        }

	    };
	    
	    grdTask.setDataProvider(dataProvider);
	    
	    grdTask.getDataProvider().refreshAll();
	    
	    ////// ADDING NOTE FILTER CONTROL
	    
		HeaderRow filteringHeader = grdTask.appendHeaderRow();
		
		TextField txtNameFilter = new TextField();
		
		txtNameFilter.setDescription("Search/Filter TodoLists");
		
		txtNameFilter.setWidth("100%");
		
		txtNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		
		txtNameFilter.setPlaceholder("Name Filter");
		
		txtNameFilter.addValueChangeListener((valueChanged) -> dataProvider.setFilter(txtNameFilter.getValue()));
		
		// This gathers the Note Title column via it's id, added in the "Building Columns" section.
		filteringHeader
		.getCell("TodoListName")
		.setComponent(txtNameFilter);
		
		// HL BUTTON CONTAINER ================================================
		
		HorizontalLayout hlButtonContainer = new HorizontalLayout(btnSave, btnClear);
		
		// ADDING COMPONENTS ===============================================
		
		this.addComponents(
				hlButtonContainer,
				txtTodoListName,
				grdTodoList);
		
		setComponentAlignment(hlButtonContainer, Alignment.MIDDLE_RIGHT);
	}
	
	private void clearTodoList(TodoList todoList)
	{
		if (currentTodoList != null && btnSave.isEnabled())
		{
			ConfirmationDialog confirmationDialog = new ConfirmationDialog("Clear TodoList?");
			
			confirmationDialog.addCloseListener((closeRequest) -> 
			{
				switch(confirmationDialog.getResponse())
				{
				case YES:
					
					clearCurrentTodoList();
					
					break;
				}
			});
			
			UI.getCurrent().addWindow(confirmationDialog);
			
		} else
		{
			clearCurrentTodoList();
		}
	}

	private void deleteNote(TodoList todoList)
	{
		ConfirmationDialog confirmationDialog = new ConfirmationDialog("Really Delete Note?");
		
		confirmationDialog.addCloseListener((closeRequest) -> 
		{
			switch(confirmationDialog.getResponse())
			{
			case YES:
				
				/*
				 * TODO Delete TodoListTasks
				 * TODO Delete TodoList
				 * TODO Delete Tasks
				 */
				
//				savedNoteRepository.deleteById(savedNote.getNoteId());
//				
//				grdTodoList.getDataProvider().refreshAll();
//				
//				if (savedNote.getNoteId() == currentTodoList.getNoteId())
//				{
//					clearCurrentTodoList();
//				}
//				
//				Notification.show("Deleted Note.",savedNote.getNoteTitle(), Notification.Type.TRAY_NOTIFICATION);
				
				break;
				
			}
		});
		
		UI.getCurrent().addWindow(confirmationDialog);
	}
	
	private void deleteTask(Task task)
	{
		// TODO Write code to delete task and reference to TodoList
//		ConfirmationDialog confirmationDialog = new ConfirmationDialog("Really Delete Note?");
//		
//		confirmationDialog.addCloseListener((closeRequest) -> 
//		{
//			switch(confirmationDialog.getResponse())
//			{
//			case YES:
//				
//				/*
//				 * TODO Delete TodoListTasks
//				 * TODO Delete TodoList
//				 * TODO Delete Tasks
//				 */
//				
////				savedNoteRepository.deleteById(savedNote.getNoteId());
////				
////				grdTodoList.getDataProvider().refreshAll();
////				
////				if (savedNote.getNoteId() == currentTodoList.getNoteId())
////				{
////					clearCurrentTodoList();
////				}
////				
////				Notification.show("Deleted Note.",savedNote.getNoteTitle(), Notification.Type.TRAY_NOTIFICATION);
//				
//				break;
//				
//			}
//		});
//		
//		UI.getCurrent().addWindow(confirmationDialog);
	}
	
	private void clearCurrentTodoList()
	{
		currentTodoList = null;
		
		txtTodoListName.clear();
		
		/*
		 * TODO Clear tasks
		 */
		
		btnSave.setEnabled(false);
		
		btnClear.setEnabled(false);
	}
}
