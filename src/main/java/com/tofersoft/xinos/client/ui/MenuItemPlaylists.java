package com.tofersoft.xinos.client.ui;


import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tofersoft.xinos.client.XinosEntryPoint;


public class MenuItemPlaylists extends HorizontalPanel implements MenuComponentItem, MainContentComponent {

	private static final Logger logger = Logger.getLogger( MenuItemPlaylists.class.getSimpleName() );

	private Label header = new Label( "Playlists" );
	private Label sub = new Label( "" );


	@Override
	protected void onLoad () {
		super.onLoad();

		logger.finest( "Dumpy message so Eclipse won't warn about the logger being unused." );

		Button b = new Button( "Go for it", new ClickHandler() {

			@Override
			public void onClick ( ClickEvent event ) {
				XinosEntryPoint.openPlayer();
			}
		} );

		add( b );
	}


	@Override
	public Widget getHeaderWidget () {
		return header;
	}


	@Override
	public Widget getSubHeaderWidget () {
		return sub;
	}


	@Override
	public MainContentComponent getContentComponent () {
		return this;
	}


	@Override
	public int getPreferredMenuPosition () {
		return 10;
	}


	@Override
	public Widget getContentWidget () {
		return this;
	}

}
