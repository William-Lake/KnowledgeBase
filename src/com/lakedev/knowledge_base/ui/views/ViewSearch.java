
package com.lakedev.knowledge_base.ui.views;

import com.lakedev.knowledge_base.ui.PrimaryLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * ViewSearch
 * 
 * The View where a user provides data
 * to search the Database with.
 * 
 * @author William Lake
 *
 */
public class ViewSearch extends StackPane
{
	private final PrimaryLayout parent;
	
	private final DatePicker datDateSaved;
	
	private final TextField txtTitle;
	
	private final TextField txtContent;
	
	private final Button btnSubmit;	
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 * 			The parent PrimaryLayout class.
	 */
	public ViewSearch(PrimaryLayout parent)
	{
		this.parent = parent;
		
		VBox vBox = new VBox();
		
		datDateSaved = new DatePicker();
		
		txtTitle = new TextField();
		
		txtContent = new TextField();
		
		btnSubmit = new Button("Search");
		
		datDateSaved.setPrefWidth(150);
		
		txtTitle.setPrefWidth(150);
		
		txtContent.setPrefWidth(150);
		
		setOnKeyPressed(new EventHandler<KeyEvent>() // Ensures pressing 'Enter' will trigger a search.
		{
			@Override
			public void handle(KeyEvent event)
			{
				if (event.getCode() == KeyCode.ENTER)
				{
					performSearch();
				}
			}
		});
		
		btnSubmit.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				performSearch();
			}
		});
		
		HBox hbDateSaved = createHBoxForControl(datDateSaved,"Saved Date:");
		
		HBox hbTitle = createHBoxForControl(txtTitle, "Title:");
		
		HBox hbContent = createHBoxForControl(txtContent, "Content:");
		
		HBox hbSubmit = new HBox();
		
		hbSubmit.getChildren().add(btnSubmit);
		
		hbSubmit.setAlignment(Pos.CENTER_RIGHT);
		
		vBox.getChildren().addAll(hbDateSaved,hbTitle,hbContent,hbSubmit);
		
		vBox.setPadding(new Insets(10,10,10,10));
		
		getChildren().addAll(vBox);
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
		
		hBox.setAlignment(Pos.CENTER_RIGHT);
		
		hBox.setPadding(new Insets(5,0,5,0));
		
		return hBox;
	}
	
	/**
	 * Performs a search using the data provided in the Controls,
	 * or lets the user know what is required.
	 */
	private void performSearch()
	{
		if (isValid()) // Check if search data was provided.
		{
			parent.performSearch(datDateSaved.getValue(),txtTitle.getText(),txtContent.getText());
		} else
		{
			Alert alert = new Alert(AlertType.ERROR);
			
			alert.setTitle("Invalid Data");
			
			alert.setHeaderText("Invalid Data");
			
			alert.setContentText("Please provide valid data.");
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Determines if the data provided in the Controls
	 * is valid.
	 * 
	 * @return True if the data is valid.
	 */
	private boolean isValid()
	{
		boolean dataValid = false;
		
		if (datDateSaved.getValue() != null) // Null means nothing, or something invalid, is in the control.
		{
			dataValid = datDateSaved
						.getValue()
						.toString()
						.matches("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}"); // YYYY-MM-DD
		}

		dataValid = dataValid || txtTitle.getText().trim().isEmpty() == false; 
		
		dataValid = dataValid || txtContent.getText().trim().isEmpty() == false;
		
		return dataValid;
	}
}
