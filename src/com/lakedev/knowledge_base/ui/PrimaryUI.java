package com.lakedev.knowledge_base.ui;

import java.io.File;
import java.nio.file.Paths;

import com.lakedev.knowledge_base.database.DataSource;
import com.lakedev.knowledge_base.ui.dialogs.DBExceptDialog;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * UI
 * 
 * The base UI for the Knowledge Base App.
 * 
 * @author William Lake
 *
 */
public class PrimaryUI	extends Application
{
	private static final int WIDTH = 1000;
	
	private static final int HEIGHT = 830;
	
	private PrimaryLayout primaryLayout;
	
	/**
	 * Main Method.
	 * 
	 * @param args
	 * 			Command Line arguments.
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

	/**
	 * Creates and launches the app's primary stage.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		// Build primary UI
		
		primaryStage.setTitle("Knowledge Base");
		
		primaryLayout = new PrimaryLayout(WIDTH,HEIGHT,this);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			
			@Override
			public void handle(WindowEvent event)
			{
				quit();
			}
		});
		
		primaryStage.setScene(new Scene(primaryLayout,WIDTH,HEIGHT));
		
		primaryStage.show();
		
		resolveDBConnection(primaryStage);
		
		/*
		 * This has to happen AFTER resolving the DB connection or else
		 * you'll get UI issues with the File menu if the app 
		 * creates or picks a new DB on launch.
		 */
		primaryStage.setResizable(false);
	}
	
	/**
	 * Ensures a connection to the DB can be made right away,
	 * if not provides the use some options.
	 * 
	 * Called by the start() method.
	 * 
	 * @param primaryStage
	 * 			The primary stage, required when creating the FileChooser.
	 */
	private void resolveDBConnection(Stage primaryStage)
	{
		// Provides the .jar file's working directory but does NOT work in Eclipse.
		String currentWorkingDir = 
				Paths
				.get
				(
						System
						.getProperty("java.class.path"))
				.getParent()
				.toString();
		
		// However, this DOES work in Eclipse, so it can be uncommented for workspace testing.
//		String currentWorkingDir = Paths.get("").toAbsolutePath().toString();
		
		File expectedDbFile = Paths.get(currentWorkingDir,"KnowledgeBase.db").toFile();
		
		boolean connectionSuccessful = DataSource.getInstance().createDBConnection(expectedDbFile);
		
		while (connectionSuccessful == false) // While you're unable to connect to the DB,
		{
			DBExceptDialog dbExceptDialog = new DBExceptDialog(); // Provide the user with options.
			
			if (dbExceptDialog.getResult().equals(DBExceptDialog.PICK_DB)) // If the user wants to pick their DB file,
			{
				FileChooser fileChooser = new FileChooser();
				
				fileChooser.setTitle("Select Database File");
				
				fileChooser.getExtensionFilters().add(new ExtensionFilter("Database Files", "*.db")); // Ensure only .db files are displayed,
				
				File selectedFile = fileChooser.showOpenDialog(primaryStage); // Gather what the user selected.
				
				if (selectedFile != null) connectionSuccessful = DataSource.getInstance().createDBConnection(selectedFile); // Try again if the user selected something.
				
			} else if (dbExceptDialog.getResult().equals(DBExceptDialog.MAKE_NEW_DB)) // If the user wants to make a new DB file,
			{
				DataSource.getInstance().createNewDB();
				
				connectionSuccessful = true;
			} else if (dbExceptDialog.getResult().equals(DBExceptDialog.QUIT)) // If the user wants to quit.
			{
				System.exit(0); // Platform.exit() doesn't actually escape the while loop here.
			}
		}
	}

	/**
	 * Quits the application, triggered by the PrimaryLayout's 
	 * Menu Bar's File -> Quit option. Ensures the user has a chance to
	 * save unsaved changes.
	 */
	public void quit()
	{
		boolean doQuit = true;
		
		if (primaryLayout.hasDirtyTabs())
		{
			Alert alert = new Alert(AlertType.CONFIRMATION);
			
			alert.setTitle("Unsaved Changes");
			
			alert.setHeaderText("Unsaved Changes");
			
			alert.setContentText("There are unsaved changes, are you sure you want to exit?");
			
			alert.showAndWait();
			
			doQuit = alert.getResult() == ButtonType.OK;
		} 
		
		if (doQuit)
		{
			DataSource.getInstance().closeDBConnection();
			
			Platform.exit();
		}
	}
}
