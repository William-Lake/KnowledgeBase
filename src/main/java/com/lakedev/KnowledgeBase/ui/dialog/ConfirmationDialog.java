package com.lakedev.KnowledgeBase.ui.dialog;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfirmationDialog extends Window
{
	private static final long serialVersionUID = 3692583773954963354L;
	
	private Response response = Response.NONE;
	
	public ConfirmationDialog(String prompt)
	{
		setResizable(false);
		
		setModal(true);
		
		center();
		
		VerticalLayout vlContainer = new VerticalLayout();
		
		Button btnYes = new Button(VaadinIcons.CHECK);
		
		btnYes.setDescription("Yes");
		
		btnYes.addStyleName("friendly");
		
		btnYes.addClickListener((clicked) -> setResponse(Response.YES));
		
		Button btnNo = new Button(VaadinIcons.CLOSE_CIRCLE);
		
		btnNo.addStyleName("danger");
		
		btnNo.setDescription("No");
		
		btnNo.addClickListener((clicked) -> setResponse(Response.NO));
		
		vlContainer.addComponents(
				new Label(prompt),
				new HorizontalLayout(
						btnYes,
						btnNo));
		
		setContent(vlContainer);
	}
	
	private void setResponse(Response response)
	{
		this.response = response;
		
		close();
	}
	
	public Response getResponse()
	{
		return this.response;
	}
	
}
