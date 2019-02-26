package com.lakedev.KnowledgeBase.ui.tab;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.lakedev.KnowledgeBase.model.SavedNote;
import com.lakedev.KnowledgeBase.repository.SavedNoteRepository;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.data.provider.Sort;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class TabNote extends VerticalLayout
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8897838960646382558L;
	
	private SavedNote currentNote;
	
	private SavedNoteRepository savedNoteRepository;
	
	private Button btnSave;
	
	private Button btnClear;
	
	private TextField txtNoteTitle;
	
	private RichTextArea rtaNoteText;
	
	private Grid<SavedNote> grdNote;
	
	public TabNote(SavedNoteRepository savedNoteRepository)
	{
		this.savedNoteRepository = savedNoteRepository;
		
		// TITLE FIELD ====================================
		
		txtNoteTitle = new TextField();
		
		txtNoteTitle.setDescription("Note Title");
		
		txtNoteTitle.setSizeFull();
		
		txtNoteTitle.setCaption("Note Title");
		
		txtNoteTitle.addValueChangeListener((valueChanged) -> 
		{
			btnSave.setEnabled(true);
			
			btnClear.setEnabled(true);
		});
		
		// NOTE DATA ======================================

		rtaNoteText = new RichTextArea();
		
		rtaNoteText.setDescription("Note Data");
		
		rtaNoteText.setSizeFull();
		
		rtaNoteText.setCaption("Note Text");
		
		rtaNoteText.addValueChangeListener((valueChanged) -> 
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
			 * If the rtaEditor data is null
			 * 		Create a new SavedNote
			 * 		Set the data in it
			 * 		set it as the rta editor's data
			 * Else
			 * 		Get the saved note
			 * 		set the data in it
			 * 	
			 * 
			 */
			
			SavedNote savedNote = 
					
					currentNote == null ?
							
							new SavedNote() :
								
								currentNote;
							
			savedNote.setNoteTitle(txtNoteTitle.getValue());
			
			savedNote.setNoteText(rtaNoteText.getValue());
			
			savedNoteRepository.save(savedNote);
			
			grdNote.getDataProvider().refreshAll();
			
			btnSave.setEnabled(false);
		});
		
		// CLEAR BUTTON ======================================
		
		btnClear = new Button(VaadinIcons.CLOSE_CIRCLE);
		
		btnClear.setDescription("Clear Note");
		
		btnClear.setEnabled(false);
		
		btnClear.addStyleName("danger");
		
		btnClear.addClickListener((btnClicked) -> 
		{
			boolean doClear = true;
			
			if (currentNote != null && btnSave.isEnabled())
			{
				doClear = confirm("Really Clear?");
			}
			
			if (doClear) clearCurrentNote();

		});
		
		// NOTE TABLE =======================================
		
		grdNote = new Grid<>();
		
		grdNote.setSizeFull();
		
		grdNote.setSelectionMode(SelectionMode.SINGLE);
		
		////// BUILDING COLUMNS
		
		// Note Title Column
		grdNote
		.addColumn(SavedNote::getNoteTitle)
		.setId("NoteTitle")
		.setCaption("Title")
		.setWidthUndefined();
		
		// Delete Button Column
		grdNote.addComponentColumn(savedNote -> 
		{
			Button btnDelete = new Button(VaadinIcons.TRASH);
			
			btnDelete.setDescription("Delete Note");
			
			btnDelete.addStyleName("danger");
			
			btnDelete.addClickListener(clicked -> deleteNote(savedNote));
			
			return btnDelete;
			
		})
		.setCaption("Delete")
		.setWidth(90);
		
		grdNote
		.getColumns()
		.stream()
		.forEach(column -> column.setHidable(false));
		
		////// ADDING LISTENER
		grdNote.addItemClickListener((itemClicked) -> 
		{
			/*
			 * We want a note double clicked in the Note Table
			 * to be added to the current editor.
			 * We also want to check with the user if unsaved
			 * data is going to be overwritten before doing so.
			 */
			if (itemClicked.getMouseEventDetails().isDoubleClick())
			{
				boolean doClear = true;
				
				if (currentNote != null && btnSave.isEnabled())
				{
					doClear = confirm("Really Clear?");
				} 
				
				if (doClear)
				{
					SavedNote selectedNote = itemClicked.getItem();
					
					txtNoteTitle.setValue(selectedNote.getNoteTitle());
					
					rtaNoteText.setValue(selectedNote.getNoteText());
					
					currentNote = selectedNote;
					
					btnSave.setEnabled(false);
					
					btnClear.setEnabled(true);
				}
				
			} 
		});
		
		grdNote.setColumnReorderingAllowed(false);
		
		////// BUILDING JPA DATAPROVIDER
		/*
		 * RESOURCES
		 * https://vaadin.com/directory/component/spring-data-provider-add-on/1.1.0/links
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/backend/PersonRepository.java
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/vaadin/MainUI.java
		 */
	    FilterablePageableDataProvider<SavedNote, Object> dataProvider = new FilterablePageableDataProvider<SavedNote, Object>() 
	    {
			private static final long serialVersionUID = 4882913761882790088L;

			@Override
	        protected Page<SavedNote> fetchFromBackEnd(Query<SavedNote, Object> query,Pageable pageable) 
	        {
	            return savedNoteRepository.findByNoteTitleLikeIgnoreCase(getRepoFilter(), pageable);
	        }

	        @Override
	        protected int sizeInBackEnd(Query<SavedNote, Object> query) 
	        {
	            return (int) savedNoteRepository.countByNoteTitleLikeIgnoreCase(getRepoFilter());
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
	    
	    grdNote.setDataProvider(dataProvider);
	    
	    grdNote.getDataProvider().refreshAll();
	    
	    ////// ADDING NOTE FILTER CONTROL
	    
		HeaderRow filteringHeader = grdNote.appendHeaderRow();
		
		TextField txtTitleFilter = new TextField();
		
		txtTitleFilter.setDescription("Search/Filter Notes");
		
		txtTitleFilter.setWidth("100%");
		
		txtTitleFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		
		txtTitleFilter.setPlaceholder("Title Filter");
		
		txtTitleFilter.addValueChangeListener((valueChanged) -> dataProvider.setFilter(txtTitleFilter.getValue()));
		
		// This gathers the Note Title column via it's id, added in the "Building Columns" section.
		filteringHeader
		.getCell("NoteTitle")
		.setComponent(txtTitleFilter);
		
		// HL BUTTON CONTAINER
		
		HorizontalLayout hlButtonContainer = new HorizontalLayout(btnSave, btnClear);
		
		// ADDING COMPONENTS ===============================================
		
		this.addComponents(
				hlButtonContainer,
				txtNoteTitle,
				rtaNoteText,
				grdNote);
		
		setComponentAlignment(hlButtonContainer, Alignment.MIDDLE_RIGHT);
	}
	
	private void deleteNote(SavedNote savedNote)
	{
		if (confirm("Really Delete?"))
		{
			savedNoteRepository.deleteById(savedNote.getNoteId());
			
			grdNote.getDataProvider().refreshAll();
			
			if (savedNote.getNoteId() == currentNote.getNoteId())
			{
				clearCurrentNote();
			}
		}
	}
	
	private void clearCurrentNote()
	{
		currentNote = null;
		
		txtNoteTitle.clear();
		
		rtaNoteText.clear();
		
		btnSave.setEnabled(false);
		
		btnClear.setEnabled(false);
	}

	private boolean confirm(String prompt)
	{
		// TODO Create a custom dialog that collects this input from the user.
		
		return true;
	}
}
