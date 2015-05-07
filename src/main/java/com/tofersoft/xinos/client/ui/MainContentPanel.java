package com.tofersoft.xinos.client.ui;


import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;


public class MainContentPanel extends TabLayoutPanel {

	private static final Logger logger = Logger.getLogger( MainContentPanel.class.getSimpleName() );


	public MainContentPanel () {
		super( 0.0d, Unit.PX );

		this.setAnimationDuration( 1000 );
	}


	@Override
	protected void onLoad () {
		super.onLoad();

	}


	public void addContentComponent ( MainContentComponent contentComponent ) {
		this.add( contentComponent.getContentWidget(), "" );
	}


	public void selectComponent ( int index ) {

		if (index >= this.getWidgetCount())
			return;

		this.selectTab( index, false );

		this.onResize();
	}

}
