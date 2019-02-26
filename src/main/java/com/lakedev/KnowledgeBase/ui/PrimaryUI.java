package com.lakedev.KnowledgeBase.ui;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lakedev.KnowledgeBase.repository.SavedFileRepository;
import com.lakedev.KnowledgeBase.repository.SavedNoteRepository;
import com.lakedev.KnowledgeBase.ui.tab.TabFile;
import com.lakedev.KnowledgeBase.ui.tab.TabNote;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
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
		vlContainer = new VerticalLayout();

		vlContainer.setWidth("100%");
		
		Label lblHeader = new Label("KnowledgeBase");
		
		lblHeader.addStyleName("h1");
		
		tabNote = new TabNote(savedNoteRepository);
		
		tabFile = new TabFile(savedFileRepository);
		
		tsContainer = new TabSheet();
		
		tsContainer.setHeight(100.0f,Unit.PERCENTAGE);
		
		tsContainer.addStyleNames(ValoTheme.TABSHEET_FRAMED, ValoTheme.TABSHEET_PADDED_TABBAR);
		
		tsContainer.addTab(tabNote, "Notes", VaadinIcons.PENCIL);
		
		tsContainer.addTab(tabFile, "Files", VaadinIcons.BRIEFCASE);
		
		Link lnkSource = new Link("", new ExternalResource("https://github.com/William-Lake/KnowledgeBase"));
		
		lnkSource.setDescription("KnowledgeBase Repo");
		
		lnkSource.setIcon(new ThemeResource("img/GitHub-Mark-32px.png"));
		
		vlContainer.addComponents(
				lblHeader,
				tsContainer,
				lnkSource);

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