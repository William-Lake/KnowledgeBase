package com.lakedev.knowledge_base.ui.editors;

import java.util.ArrayList;
import java.util.List;

import com.lakedev.knowledge_base.Knowledge;
import com.lakedev.knowledge_base.ui.PrimaryLayout;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * EditorKnowledge
 * 
 * Parent of the EditorKnowledgeTabs.
 * 
 * Although this really qualifies as a View, 
 * it houses Editors.
 * 
 * @author William Lake
 *
 */
public class EditorKnowledge extends TabPane
{
	private final PrimaryLayout parent;
	
	private final List<EditorKnowledgeTab> editorKnowledgeTabs;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 * 			The PrimaryLayout parent
	 */
	public EditorKnowledge(PrimaryLayout parent)
	{
		this.parent = parent;
		
		editorKnowledgeTabs = new ArrayList<>();
		
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}
	
	/**
	 * Builds and displays an EditorKnowledgeTab for
	 * the given knowledge as long as it's not
	 * already being displayed.
	 * 
	 * Called by PrimaryLayout.displayKnowledge() and
	 * addNewKnowledge()
	 * 
	 * @param knowledge
	 * 			The knowledge to build a Tab for and display.
	 */
	public void displayKnowledge(Knowledge knowledge)
	{
		// If the knowledge is already being displayed then don't show it.
		if (
				getTabs()
				.stream()
				.filter(tab -> 
				{
					EditorKnowledgeTab editorKnowledgeTab = (EditorKnowledgeTab) tab;
					
					int knowledgeId = editorKnowledgeTab.getKnowledge().getKnowledgeId();
					
					if (knowledgeId == 0) return false; // Ensures multiple 'NEW' knowledge tabs can be generated.
					
					return editorKnowledgeTab.getKnowledge().getKnowledgeId() == knowledge.getKnowledgeId();
				})
				.count() > 0
				) return;
		
		EditorKnowledgeTab editorKnowledgeTab = new EditorKnowledgeTab(knowledge,this);
		
		// Ensure dirty tabs aren't closed without a chance to save.
		editorKnowledgeTab.setOnCloseRequest(new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				boolean doClose = true;
				
				if (editorKnowledgeTab.isDirty())
				{
					Alert alert = new Alert(AlertType.CONFIRMATION);
					
					alert.setTitle("Unsaved Changes");
					
					alert.setHeaderText("Unsaved Changes");
					
					alert.setContentText("There are unsaved changes, are you sure you want to close the tab?");
					
					alert.showAndWait();
					
					doClose = alert.getResult() == ButtonType.OK;
				}
				
				if (doClose) editorKnowledgeTabs.remove(editorKnowledgeTab);
				
				else event.consume();
			}
		});
		
		// Ensure the File -> Save option reflects the currently displayed Tab.
		editorKnowledgeTab.setOnSelectionChanged(new EventHandler<Event>()
		{
			@Override
			public void handle(Event event)
			{
				parent.setSaveEnabled(editorKnowledgeTab.isDirty());
			}
		});
		
		editorKnowledgeTabs.add(editorKnowledgeTab);
		
		getTabs().add(editorKnowledgeTab);
	}

	/**
	 * Creates a EditorKnowledgeTab for brand new Knowledge.
	 * 
	 * Called when File -> Add new knowledge is selected.
	 */
	public void addNewKnowledge()
	{
		displayKnowledge(new Knowledge());
	}

	/**
	 * Saves changes for the currently displayed tab.
	 * 
	 * Called when File -> Save is selected.
	 */
	public void saveChanges()
	{
		Tab currentTab = 
				getTabs()
				.stream()
				.filter(tab -> tab.isSelected())
				.findFirst()
				.get();
		
		((EditorKnowledgeTab) currentTab).save();
		
		((EditorKnowledgeTab) currentTab).setDirty(false);
		
		parent.refresh();
	}
	
	/**
	 * Tells the parent to disable/enable the File -> save option.
	 * 
	 * Called by the EditorKnowledgeTab when it determines if 
	 * it is dirty.
	 * 
	 * @param saveEnabled
	 */
	public void setSaveEnabled(boolean saveEnabled)
	{
		parent.setSaveEnabled(saveEnabled);
	}

	/**
	 * Determines if any of the tabs are dirty.
	 * 
	 * Called by the PrimaryLayout, 
	 * who has it called by the UI,
	 * who is checking if any unsaved changes
	 * need a chance to be saved before quitting.
	 * 
	 * @return True if there are dirty tabs.
	 */
	public boolean hasDirtyTabs()
	{
		return 
				getTabs()
				.stream()
				.filter(tab -> 
				{
					EditorKnowledgeTab editorKnowledgeTab = (EditorKnowledgeTab) tab;
					
					return editorKnowledgeTab.isDirty();
				}).count() > 0;
	}

	/**
	 * Closes the given tab.
	 * 
	 * Called by the Tab itself when it's Delete button is pressed.
	 * 
	 * @param editorKnowledgeTab
	 * 			The tab to close.
	 */
	public void close(EditorKnowledgeTab editorKnowledgeTab)
	{
		getTabs().remove(editorKnowledgeTab);
		
		editorKnowledgeTabs.remove(editorKnowledgeTab);
		
		parent.refresh();
	}
}
