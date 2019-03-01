package com.lakedev.KnowledgeBase.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lakedev.KnowledgeBase.KnowledgeBaseApplication;
import com.lakedev.KnowledgeBase.repository.SavedFileRepository;
import com.lakedev.KnowledgeBase.repository.SavedNoteRepository;
import com.lakedev.KnowledgeBase.ui.tab.TabFile;
import com.lakedev.KnowledgeBase.ui.tab.TabNote;
import com.lakedev.KnowledgeBase.util.DbStatus;
import com.lakedev.KnowledgeBase.util.DbUtil;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@RequestMapping(value="/")
@SpringUI
public class PrimaryUI extends UI
{

	private static final long serialVersionUID = 5336491868802277718L;

	private VerticalLayout vlContainer;
	
	private TabSheet tsContainer;
	
	private TabNote tabNote;
	
	private TabFile tabFile;
	
	@Autowired
	private SavedFileRepository savedFileRepository;
	
	@Autowired
	private SavedNoteRepository savedNoteRepository;
	
	@Override
	protected void init(VaadinRequest request)
	{
		DbStatus dbStatus = DbUtil.checkDb(savedFileRepository,savedNoteRepository);
		
		if (dbStatus == DbStatus.OK)
		{
			buildUI();
		} else if (dbStatus == DbStatus.PARTIAL_DB)
		{
			// TODO Alert user, provide them option to export db data.
		}
	}
	
	private void buildUI()
	{
		// LAYOUT CONTAINER =========================================
		vlContainer = new VerticalLayout();

		vlContainer.setWidth("100%");
		
		// HEADER LABEL =============================================
		
		Label lblHeader = new Label("KnowledgeBase");
		
		lblHeader.addStyleName("h1");
		
		// HEADER REPO LINK/ICON ====================================
		
		Link lnkSource = new Link("", new ExternalResource("https://github.com/William-Lake/KnowledgeBase"));
		
		lnkSource.setDescription("KnowledgeBase Repo");
		
		lnkSource.setIcon(new ThemeResource("img/GitHub-Mark-32px.png"));
		
		// TABS =====================================================
		tabNote = new TabNote(savedNoteRepository);
		
		tabFile = new TabFile(savedFileRepository);
		
		// TAB SHEET ================================================
		tsContainer = new TabSheet();
		
		tsContainer.setHeight(100.0f,Unit.PERCENTAGE);
		
		tsContainer.addStyleNames(ValoTheme.TABSHEET_FRAMED, ValoTheme.TABSHEET_PADDED_TABBAR);
		
		tsContainer.addTab(tabNote, "Notes", VaadinIcons.PENCIL);
		
		tsContainer.addTab(tabFile, "Files", VaadinIcons.BRIEFCASE);
		
		// ADDING COMPONENTS ========================================
		
		vlContainer.addComponents(
				lblHeader,
				tsContainer,
				lnkSource
				);
		
		vlContainer.setComponentAlignment(lnkSource, Alignment.TOP_RIGHT);
		
		setContent(vlContainer);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = PrimaryUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

}