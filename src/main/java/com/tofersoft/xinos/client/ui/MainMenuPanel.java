package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.StackLayoutPanel;


public class MainMenuPanel extends StackLayoutPanel {

	private static final Logger logger = Logger.getLogger( MainMenuPanel.class.getSimpleName() );

	private final MainContentPanel mainContentPanel;

	private List<MenuComponentItem> menuComponents;

	private boolean pauseFire = true;


	public MainMenuPanel ( MainContentPanel mainContentPanel ) {
		super( Unit.PX );

		this.mainContentPanel = mainContentPanel;

		menuComponents = new ArrayList<MenuComponentItem>();
	}


	@Override
	protected void onLoad () {
		super.onLoad();

		this.addSelectionHandler( new SelectionHandler<Integer>() {

			@Override
			public void onSelection ( SelectionEvent<Integer> event ) {
				handleOnSelection( event );
			}
		} );
	}


	public void addMenuComponent ( MenuComponentItem menuComponent ) {

		pauseFire = true;
		menuComponents.add( menuComponent );

		this.add( menuComponent.getSubHeaderWidget(), menuComponent.getHeaderWidget(), 40.0d );

		mainContentPanel.addContentComponent( menuComponent.getContentComponent() );

		this.onResize();
		pauseFire = false;
	}


	private void handleOnSelection ( SelectionEvent<Integer> event ) {
		if (!pauseFire) {
			logger.info( "SELECTION = " + event.getSelectedItem() );
			mainContentPanel.selectComponent( event.getSelectedItem() );
		}
	}

}
