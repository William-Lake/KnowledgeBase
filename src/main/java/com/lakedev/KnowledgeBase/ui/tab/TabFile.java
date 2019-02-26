package com.lakedev.KnowledgeBase.ui.tab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.lakedev.KnowledgeBase.model.SavedFile;
import com.lakedev.KnowledgeBase.repository.SavedFileRepository;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.data.provider.Sort;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.dnd.FileDropTarget;
import com.vaadin.ui.themes.ValoTheme;

public class TabFile extends VerticalLayout
{
	private SavedFileRepository savedFileRepository;
	
	private ProgressBar progressBar;
	
	private Grid<SavedFile> grdFile;
	
	private static final long serialVersionUID = -8549068869084269501L;
	
	public TabFile(SavedFileRepository savedFileRepository)
	{
		this.savedFileRepository = savedFileRepository;
		
		// PROGRESS BAR ==================================
		
		progressBar = new ProgressBar();
		
		progressBar.setIndeterminate(true);
		
		progressBar.setVisible(false);
		
		// FILE TABLE ==========================================
		
		grdFile = new Grid<>();
		
		/////////// JPA DataProvider
		/*
		 * https://vaadin.com/directory/component/spring-data-provider-add-on/1.1.0/links
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/backend/PersonRepository.java
		 * https://github.com/Artur-/spring-data-vaadin-crud/blob/master/src/main/java/crud/vaadin/MainUI.java
		 */
	    FilterablePageableDataProvider<SavedFile, Object> dataProvider = new FilterablePageableDataProvider<SavedFile, Object>() 
	    {
			private static final long serialVersionUID = -8772364939651082074L;

			@Override
	        protected Page<SavedFile> fetchFromBackEnd(Query<SavedFile, Object> query,Pageable pageable) 
	        {
	            return savedFileRepository.findByFileNameLikeIgnoreCase(getRepoFilter(), pageable);
	        }

	        @Override
	        protected int sizeInBackEnd(Query<SavedFile, Object> query) 
	        {
	            return (int) savedFileRepository.countByFileNameLikeIgnoreCase(getRepoFilter());
	        }

	        private String getRepoFilter() 
	        {
	        	String filter = getOptionalFilter().orElse("");
	        	
	            return "%" + filter + "%";
	        }

	        @Override
	        protected List<QuerySortOrder> getDefaultSortOrders() 
	        {
	            return Sort.asc("fileName").build();
	        }
	        
	    };
	    
	    grdFile.setDataProvider(dataProvider);
	    
	    grdFile.getDataProvider().refreshAll();
	    
		grdFile.setSizeFull();
		
		grdFile.setSelectionMode(SelectionMode.SINGLE);
		
		grdFile.addColumn(SavedFile::getFileId).setId("fileId").setCaption("Id");
		
		grdFile.addColumn(SavedFile::getFileName).setId("fileName").setCaption("FileName");
		
		grdFile.setColumnReorderingAllowed(true);
		
		grdFile.getColumns().stream().forEach(column -> column.setHidable(true));
		
		HeaderRow filteringHeader = grdFile.appendHeaderRow();
		
		TextField fileNameFilter = new TextField();
		
		fileNameFilter.setWidth("100%");
		
		fileNameFilter.addStyleName(ValoTheme.TEXTFIELD_TINY);
		
		fileNameFilter.setPlaceholder("FileName Filter");
		
		fileNameFilter.addValueChangeListener((valueChanged) -> dataProvider.setFilter(fileNameFilter.getValue()));
		
		filteringHeader.getCell("fileName").setComponent(fileNameFilter);
		
		grdFile.addItemClickListener((itemClicked) -> 
		{
			if (itemClicked.getMouseEventDetails().isDoubleClick())
			{
				SavedFile savedFile = itemClicked.getItem();
				
				String userDir = System.getProperty("user.home");
				
				Path filePath = Paths.get(userDir, savedFile.getFileName());
				
				Notification.show("Downloading", String.format("Downloading %s to %s.", savedFile.getFileName(),filePath.toString()), Notification.Type.TRAY_NOTIFICATION);
				
				try
				{
					Files.write(Paths.get(userDir,savedFile.getFileName()), savedFile.getFileData());
					
					Notification.show("Finished", String.format("Completed download of %s to %s.", savedFile.getFileName(),filePath.toString()), Notification.Type.TRAY_NOTIFICATION);
				} catch (IOException e)
				{
					e.printStackTrace();
					
					Notification.show("ERROR","Error while Downloading File: " + e.getMessage(),Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		
		// FILE DROP TARGET ================================================
		
		new FileDropTarget<>(grdFile, (fileDropped) -> 
		{
			fileDropped.getFiles().forEach(file -> 
			{
				final String fileName = file.getFileName();
				
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				
				final StreamVariable streamVariable = new StreamVariable()
				{

					@Override
					public OutputStream getOutputStream()
					{
						return byteArrayOutputStream;
					}

					@Override
					public boolean listenProgress()
					{
						return false;
					}

					@Override
					public void onProgress(StreamingProgressEvent event)
					{
						
					}

					@Override
					public void streamingStarted(StreamingStartEvent event)
					{
						Notification.show(String.format("Uploading %s", fileName), Notification.Type.TRAY_NOTIFICATION);
					}

					@Override
					public void streamingFinished(StreamingEndEvent event)
					{
						progressBar.setVisible(false);
						
						saveFile(fileName, byteArrayOutputStream);
					}

					@Override
					public void streamingFailed(StreamingErrorEvent event)
					{
						progressBar.setVisible(false);
					}

					@Override
					public boolean isInterrupted()
					{
						return false;
					}
					
				};
				
				file.setStreamVariable(streamVariable);
				
				progressBar.setVisible(true);
			});
			
			grdFile.getDataProvider().refreshAll();
		});
		
		// ADDING COMPONENTS ===============================================		
		
		addComponents(
				progressBar,
				grdFile);
	}

	private void saveFile(final String fileName, final ByteArrayOutputStream byteArrayOutputStream)
	{
		SavedFile savedFile = savedFileRepository.findByFileNameIgnoreCase(fileName);
		
		savedFile = 
				
				savedFile == null ?
						
						new SavedFile() :
							
							savedFile;
						
		savedFile.setFileName(fileName);
						
		savedFile.setFileData(byteArrayOutputStream.toByteArray());
		
		savedFileRepository.save(savedFile);
		
	}

}
