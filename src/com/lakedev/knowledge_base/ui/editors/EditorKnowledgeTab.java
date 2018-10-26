package com.lakedev.knowledge_base.ui.editors;

import com.lakedev.knowledge_base.Knowledge;
import com.lakedev.knowledge_base.database.DataSource;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * EditorKnowledgeTab
 * 
 * Displays a given piece of knowledge for editing.
 * 
 * @author William Lake
 *
 */
public class EditorKnowledgeTab extends Tab
{
	private final EditorKnowledge parent;
	
	private final Knowledge knowledge;
	
	private final Button btnSave;
	
	private final TextField txtTitle;
	
	private final TextArea txtContent;
	
	private boolean dirty;
	
	/**
	 * Constructor
	 * 
	 * Called by EditorKnowledge.displayKnowledge()
	 * 
	 * @param knowledge
	 * 			The knowledge to display.
	 * @param parent
	 * 			EditorKnowledge
	 */
	public EditorKnowledgeTab(Knowledge knowledge, EditorKnowledge parent)
	{
		this.knowledge = knowledge;
		
		this.parent = parent;
		
		setText((knowledge.getKnowledgeId() != 0) ? String.valueOf(knowledge.getKnowledgeId()) : "NEW");
		
		VBox vBox = new VBox();
		
		HBox hbHeader = new HBox();
		
		HBox hbHeaderButtons = new HBox();
		
		Region rgnHeader = new Region();
		
		btnSave = new Button("Save");
		
		Button btnDelete = new Button("Delete");
		
		Label lblDateSaved = new Label();
		
		txtTitle = new TextField();
		
		txtContent = new TextArea();
		
		txtContent.setWrapText(true);
		
		lblDateSaved.setPrefWidth(150);
		
		hbHeader.prefWidthProperty().bind(parent.prefWidthProperty());
		
		hbHeaderButtons.setAlignment(Pos.CENTER_RIGHT);
		
		hbHeaderButtons.setPadding(new Insets(0, 10, 0, 0));
		
		HBox.setHgrow(rgnHeader, Priority.ALWAYS);
		
		boolean isNewKnowledge = knowledge.getKnowledgeId() == 0;
		
		btnSave.setDisable(isNewKnowledge == false);
		
		btnDelete.setDisable(isNewKnowledge);
		
		btnDelete.setStyle("-fx-background-color: #F08080; ");
		
		btnDelete.setTranslateX(10);
		
		txtTitle.prefWidthProperty().bind(parent.prefWidthProperty());
		
		lblDateSaved.setText(knowledge.getDateSaved().toString());
		
		txtTitle.setText(knowledge.getTitle());
		
		txtContent.setText(knowledge.getContent());
		
		HBox hbDateSaved = createHBoxForControl(lblDateSaved,"Saved Date:");
		
		VBox vbTitle = createVBoxForControl(txtTitle, "Title:");
		
		VBox vbContent = createVBoxForControl(txtContent, "Content:");
		
		VBox.setVgrow(txtContent, Priority.ALWAYS);
		
		VBox.setVgrow(vbContent, Priority.ALWAYS);
		
		btnSave.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				save();
			}
		});
		
		btnDelete.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				DataSource.getInstance().deleteKnowledge(knowledge);
				
				close();
			}
		});
		
		txtTitle.setOnKeyTyped(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (dirty == false) setDirty(true);
			}
		});
		
		txtContent.setOnKeyTyped(new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (dirty == false) setDirty(true);
			}
		});
		
		hbHeaderButtons.getChildren().addAll(btnSave,btnDelete);
		
		hbHeader.getChildren().addAll(hbDateSaved,rgnHeader,hbHeaderButtons);
		
		vBox.getChildren().addAll(hbHeader,vbTitle,vbContent);
		
		vBox.setPadding(new Insets(10,10,10,10));
		
		setContent(vBox);
	}
	
	/**
	 * Called when the delete button is pressed.
	 * Used because I couldn't find a way to have
	 * a closure hook for the individual tabs.
	 */
	public void close()
	{
		parent.close(this);
	}
	
	/**
	 * Creates a VBox and label for the given control.
	 * 
	 * Called in the constructor.
	 * 
	 * @param control
	 * 			The control to build the VBox for.
	 * @param labelText
	 * 			The label text.
	 * @return The completed VBox.
	 */
	private VBox createVBoxForControl(Control control, String labelText)
	{
		VBox vBox = new VBox();
		
		Label lbl = new Label(labelText);
		
		lbl.setPadding(new Insets(5,5,5,5));
		
		vBox.getChildren().addAll(lbl,control);
		
		vBox.setAlignment(Pos.CENTER_LEFT);
		
		vBox.setPadding(new Insets(5,0,5,0));
		
		return vBox;
	}
	
	/**
	 * Creates a HBox and label for the given control.
	 * 
	 * Called in the constructor.
	 * 
	 * @param control
	 * 			The control to build the VBox for.
	 * @param labelText
	 * 			The label text.
	 * @return The completed HBox.
	 */
	private HBox createHBoxForControl(Control control, String labelText)
	{
		HBox hBox = new HBox();
		
		Label lbl = new Label(labelText);
		
		lbl.setPadding(new Insets(5,5,5,5));
		
		hBox.getChildren().addAll(lbl,control);
		
		hBox.setAlignment(Pos.CENTER_LEFT);
		
		hBox.setPadding(new Insets(5,0,5,0));
		
		return hBox;
	}
	
	/**
	 * Determines if the data provided is valid for saving.
	 * 
	 * Called by save().
	 * 
	 * @return True if the provided knowledge is valid.
	 */
	private boolean dataValid()
	{
		boolean dataValid = true;
		
		dataValid = txtTitle.getText().trim().isEmpty() == false;
		
		dataValid = dataValid && txtContent.getText().trim().isEmpty() == false;
		
		return dataValid;
	}
	
	/**
	 * Saves the data on this Tab.
	 * 
	 * Called in EditorKnowledge.save() and 
	 * locally when the save() button is pressed.
	 */
	public void save()
	{
		if (dataValid()) // If the data is valid,
		{
			knowledge.setTitle(txtTitle.getText()); // Put everything on the Tab into the Knowledge object,
			
			knowledge.setContent(txtContent.getText());
			
			if (knowledge.getKnowledgeId() == 0) DataSource.getInstance().saveKnowledge(knowledge); // Save new,
			
			else DataSource.getInstance().updateKnowledge(knowledge); // Update Existing,
			
			setDirty(false); // Display the change.
			
		} else
		{
			Alert alert = new Alert(AlertType.ERROR);
			
			alert.setTitle("Invalid Data");
			
			alert.setHeaderText("Invalid Data");
			
			alert.setContentText("Please provide data to save.");
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Ensures the tab's text shows that changes have been
	 * made via a dirty marker(*), or clears the dirty marker
	 * once the changes have been saved. Also triggers a 
	 * process which results in the File -> save option
	 * becoming disabled/enabled depending on whether
	 * this tab is dirty.
	 * 
	 * @param isDirty
	 * 			Whether the tab has unsaved edits.
	 */
	public void setDirty(boolean isDirty)
	{
		this.dirty = isDirty;
		
		String tabText = getText();
		
		if (isDirty)
		{
			setText(tabText + "*");
			
			btnSave.setDisable(false);
		} else
		{
			setText(String.valueOf(knowledge.getKnowledgeId()));
			
			btnSave.setDisable(true);
		}
		
		parent.setSaveEnabled(isDirty);
	}
	
	/**
	 * Provides whether this tab is dirty.
	 * 
	 * Called primarily in EditorKnowledge.
	 * 
	 * @return True if this Tab is dirty.
	 */
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 * Provides the knowledge being edited on the tab.
	 * 
	 * Called in EditorKnowledge.displayKnowledge()
	 * 
	 * @return The Knowledge being edited.
	 */
	public Knowledge getKnowledge()
	{
		return knowledge;
	}
	
}
