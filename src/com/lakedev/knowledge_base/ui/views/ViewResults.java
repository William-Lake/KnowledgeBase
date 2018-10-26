package com.lakedev.knowledge_base.ui.views;

import java.time.LocalDate;
import java.util.List;

import com.lakedev.knowledge_base.Knowledge;
import com.lakedev.knowledge_base.ui.PrimaryLayout;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * ViewResults
 * 
 * The View which displays Search Results 
 * to the User.
 * 
 * @author William Lake
 *
 */
public class ViewResults extends StackPane
{
	private final TableView<Knowledge> tblKnowledge;
	
	private final PrimaryLayout parent;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 * 			The parent PrimaryLayout
	 */
	public ViewResults(PrimaryLayout parent)
	{
		this.parent = parent;
		
		tblKnowledge = new TableView<>();
		
		TableColumn colId = new TableColumn("Id");
		
		colId.setCellValueFactory(new PropertyValueFactory<Knowledge,Integer>("knowledgeId"));
		
		TableColumn colDateSaved = new TableColumn("Date Saved");
		
		colDateSaved.setCellValueFactory(new PropertyValueFactory<Knowledge,LocalDate>("dateSaved"));
		
		colDateSaved.setPrefWidth(95);
		
		TableColumn colTitle = new TableColumn("Title");
		
		colTitle.setCellValueFactory(new PropertyValueFactory<Knowledge,String>("title"));
		
		colTitle.setPrefWidth(400);
		
		tblKnowledge.getColumns().addAll(colId,colDateSaved,colTitle);
		
		tblKnowledge.setOnMouseClicked(new EventHandler<MouseEvent>() // Double clicking an item will display it
		{
			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
				{
					parent.displayKnowledge(tblKnowledge.getSelectionModel().getSelectedItem());
				}
			}
		});
		
		tblKnowledge.setOnKeyPressed(new EventHandler<KeyEvent>() // Pressing Enter when an item is selected will display it.
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER && tblKnowledge.getSelectionModel().getSelectedItem() != null)
				{
					parent.displayKnowledge(tblKnowledge.getSelectionModel().getSelectedItem());
				}
			}
		});
		
		getChildren().addAll(tblKnowledge);
	}

	/**
	 * Displays the given KnowledgeList on the table.
	 * 
	 * Called by PrimaryLayout.performSearch()
	 * 
	 * @param knowledgeList
	 * 			The Collection of Knowledge to display.
	 */
	public void displayKnowledge(List<Knowledge> knowledgeList)
	{
		tblKnowledge.setItems(FXCollections.observableArrayList(knowledgeList));
	}
}
