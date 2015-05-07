package com.tofersoft.xinos.client.ui;


import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;


public class HeaderPanel extends HorizontalPanel {

	@Override
	protected void onLoad () {
		super.onLoad();

		this.add( new Label( "Header, dude" ) );
	}

}
