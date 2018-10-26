package com.lakedev.knowledge_base.ui;

import java.time.LocalDate;
import java.util.List;

import com.lakedev.knowledge_base.Knowledge;
import com.lakedev.knowledge_base.database.DataSource;
import com.lakedev.knowledge_base.ui.editors.EditorKnowledge;
import com.lakedev.knowledge_base.ui.views.ViewResults;
import com.lakedev.knowledge_base.ui.views.ViewSearch;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * PrimaryLayout
 * 
 * The Primary Layout of the Knowledge Base App.
 * 
 * Owned by the UI class.
 * 
 * @author William Lake
 *
 */
public class PrimaryLayout extends StackPane
{
	private final ViewResults viewResults;
	
	private final EditorKnowledge editorKnowledge;
	
	private MenuItem mniSaveKnowledge;
	
	private final PrimaryUI parent;
	
	private LocalDate currentSearchDate;
	
	private String currentSearchTitle;
	
	private String currentSearchContent;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 * 			Desired App width
	 * @param height
	 * 			Desired App height
	 * @param parent
	 * 			The parent container
	 */
	public PrimaryLayout(int width, int height,PrimaryUI parent)
	{
		this.parent = parent;
		
		setPrefWidth(width);
		
		setPrefHeight(height);
		
		VBox vbPrimaryContainer = new VBox();
		
		MenuBar mnuBar = createMenuBar();
		
		SplitPane pneSearchResults = new SplitPane();
		
		SplitPane pneKnowledgeSplit = new SplitPane();
		
		pneKnowledgeSplit.setPrefHeight(height);
		
		ViewSearch viewSearch = new ViewSearch(this);
		
		viewResults = new ViewResults(this);
		
		editorKnowledge = new EditorKnowledge(this);
		
		pneSearchResults.getItems().addAll(viewSearch,viewResults);
		
		pneKnowledgeSplit.getItems().addAll(pneSearchResults,editorKnowledge);
		
		pneSearchResults.setDividerPositions(0.3f);
		
		pneKnowledgeSplit.setDividerPositions(0.3f);
		
		pneKnowledgeSplit.setOrientation(Orientation.VERTICAL);
		
		vbPrimaryContainer.getChildren().addAll(mnuBar,pneKnowledgeSplit);
		
		getChildren().add(vbPrimaryContainer);
	}

	/**
	 * Creates the MenuBar for the app.
	 * 
	 * @return The created MenuBar.
	 */
	private MenuBar createMenuBar()
	{
		MenuBar mnuBar = new MenuBar();
		
		Menu mnuFile = new Menu("File"); // File
		
		MenuItem mniAddKnowledge = new MenuItem("Add Knowledge"); // File -> Add Knowledge
		
		mniAddKnowledge.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				editorKnowledge.addNewKnowledge();
			}
		});
		
		mniSaveKnowledge = new MenuItem("Save Knowledge"); // File -> Save Knowledge
		
		mniSaveKnowledge.setDisable(true);
		
		mniSaveKnowledge.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				editorKnowledge.saveChanges();
				
				mniSaveKnowledge.setDisable(true);
			}
		});
		
		MenuItem mniQuit = new MenuItem("Quit"); // File -> Quit
		
		mniQuit.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				parent.quit();
			}
		});
		
		mnuFile.getItems().addAll(mniAddKnowledge,mniSaveKnowledge, mniQuit);
		
		mnuBar.getMenus().add(mnuFile);
		
		return mnuBar;
	}

	/**
	 * Facilitates a search using the given data,
	 * as triggered by the SearchView.
	 * 
	 * Called by the refresh() method and 
	 * ViewSearch.performSearch()
	 * 
	 * @param date
	 * 			The date to use in the search.
	 * @param title
	 * 			The title to use in the search.
	 * @param content
	 * 			The content to use in the search.
	 */
	public void performSearch(LocalDate date, String title, String content)
	{
		// Save these locally so they can be used to refresh the views.
		currentSearchDate = date;
		
		currentSearchTitle = title;
		
		currentSearchContent = content;
		
		List<Knowledge> knowledgeList = DataSource.getInstance().gatherKnowledge(date,title,content);
		
		viewResults.displayKnowledge(knowledgeList);
	}

	/**
	 * Facilitates displaying the given knowledge,
	 * as triggered by the ResultsView.
	 * 
	 * Called by ViewSearch
	 * 
	 * @param knowledge
	 * 			The knowledge to display.
	 */
	public void displayKnowledge(Knowledge knowledge)
	{
		editorKnowledge.displayKnowledge(knowledge);
	}

	/**
	 * Refreshes the views, I.e. reperforms the search so 
	 * that the views display the most up to date data.
	 * 
	 * Called by EditorKnowledge.close() and 
	 * EditorKnowledge.saveChanges().
	 */
	public void refresh()
	{
		performSearch(currentSearchDate, currentSearchTitle, currentSearchContent);
	}
	
	/**
	 * Sets the enabled status of the File -> Save Knowledge option.
	 * 
	 * Enabled when unsaved edits exist. Disabled otherwise.
	 * 
	 * Called by EditorKnowledge.displayKnowledge() and
	 * EditorKnowledge.setSaveEnabled()
	 * 
	 * @param saveEnabled
	 * 			The enabled status of the save option.
	 */
	public void setSaveEnabled(boolean saveEnabled)
	{
		mniSaveKnowledge.setDisable(saveEnabled == false);
	}

	/**
	 * Determines where unsaved edits exist
	 * in one of the displayed tabs.
	 * 
	 * Called by UI.quit()
	 * 
	 * @return True if there are dirty tabs.
	 */
	public boolean hasDirtyTabs()
	{
		return editorKnowledge.hasDirtyTabs();
	}
}
