package com.lakedev.knowledge_base.ui.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * ErrorDialog
 * 
 * Shares data related to an Exception thrown with the user.
 * 
 * @author William Lake
 *
 */
public class ErrorDialog extends Dialog
{
	/**
	 * Constructor
	 * 
	 * @param exception
	 * 			The exception to display to the user.
	 */
	public ErrorDialog(Exception exception)
	{
		setHeaderText("Error");
		
		setTitle(exception.getClass().getSimpleName());
		
		getDialogPane().getButtonTypes().add(ButtonType.OK);
		
		// http://code.makery.ch/blog/javafx-dialogs-official/
		StringWriter stringWriter = new StringWriter();
		
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		exception.printStackTrace(printWriter);
		
		Label lblException = new Label("Stack Trace");
		
		TextArea txtException = new TextArea(stringWriter.toString());
		
		txtException.setEditable(false);
		
		txtException.setWrapText(true);
		
		txtException.setMaxWidth(Double.MAX_VALUE);
		
		txtException.setMaxHeight(Double.MAX_VALUE);
		
		VBox.setVgrow(txtException, Priority.ALWAYS);
		
		VBox vbContent = new VBox();
		
		vbContent.setMaxWidth(Double.MAX_VALUE);
		
		vbContent.getChildren().addAll(lblException,txtException);
		
		getDialogPane().setExpandableContent(vbContent);
		
		getDialogPane().setExpanded(true);
		
		showAndWait();
	}
}
