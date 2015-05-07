package com.tofersoft.xinos.client.ui;


import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;


public class LogoPanel extends VerticalPanel {

	public LogoPanel () {
		super();
	}


	@Override
	protected void onLoad () {
		super.onLoad();

		final Image image = new Image( Images.get().logo() );

		this.add( image );
	}

}
