package com.lakedev.knowledge_base.ui.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.lakedev.knowledge_base.database.DataSource;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * DBExceptDialog
 * 
 * Lets the user know that a .db file 
 * couldn't be found/connected to and
 * provides them the option to pick an
 * existing file, create a new one,
 * or quit.
 * 
 * @author William Lake.
 *
 */
public class DBExceptDialog extends Dialog
{
	public static final ButtonType PICK_DB = new ButtonType("Pick DB");
	
	public static final ButtonType MAKE_NEW_DB = new ButtonType("Make New DB");
	
	public static final ButtonType QUIT = new ButtonType("Quit");
	
	/**
	 * Constructor
	 * 
	 * Called by UI.resolveDBConnection()
	 */
	public DBExceptDialog()
	{
		setHeaderText("DB Connection Exception");
		
		setTitle("DBException");

		getDialogPane().getButtonTypes().addAll(PICK_DB,MAKE_NEW_DB,QUIT);
		
		// http://code.makery.ch/blog/javafx-dialogs-official/
		StringWriter stringWriter = new StringWriter();
		
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		DataSource
		.getInstance()
		.getConnectionException()
		.printStackTrace(printWriter);
		
		Label lblException = new Label("Stack Trace");
		
		TextArea txtException = new TextArea(stringWriter.toString());
		
		txtException.setEditable(false);
		
		txtException.setWrapText(true);
		
		txtException.setMaxWidth(Double.MAX_VALUE);
		
		txtException.setMaxHeight(Double.MAX_VALUE);
		
		VBox.setVgrow(txtException, Priority.ALWAYS);
		
		Label lblOptions = new Label("Please choose from the following resolution options:");
		
		lblOptions.setPadding(new Insets(10,5,10,5));
		
		VBox vbContent = new VBox();
		
		vbContent.setMaxWidth(Double.MAX_VALUE);
		
		vbContent.getChildren().addAll(lblException,txtException,lblOptions);
		
		getDialogPane().setExpandableContent(vbContent);
		
		getDialogPane().setExpanded(true);
		
		showAndWait();
	}
}
