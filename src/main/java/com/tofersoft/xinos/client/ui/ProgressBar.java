package com.tofersoft.xinos.client.ui;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * A horizontal progress bar comprising three components. The top and tallest
 * indicates overall track playback. The second and third are micro-sized. The
 * second bar indicates the current-buffer and the third indicates the secondary
 * pre-buffer (the next track).
 *
 * @author cmetyko
 *
 */
public class ProgressBar implements IsWidget {

	private LayoutPanel root = null;

	private final HTML playback = new HTML();
	private final HTML buffer = new HTML();
	private final HTML prebuffer = new HTML();

	private boolean bufferIsIndefinite = false;
	private double bufferX = 0.0d;

	private boolean prebufferIsIndefinite = false;
	private double prebufferX = 0.0d;

	private void setup () {

		playback.setHeight( "8px" );
		playback.addStyleName( "xinosProgressBarPlayback" );

		buffer.setHeight( "4px" );
		buffer.addStyleName( "xinosProgressBarBuffer" );

		prebuffer.setHeight( "4px" );
		prebuffer.addStyleName( "xinosProgressBarPrebuffer" );

		root = new LayoutPanel();

		root.add( playback );
		root.add( buffer );
		root.add( prebuffer );

		root.setWidgetTopBottom( playback, 0.0d, Unit.PX, 8.0d, Unit.PX );
		root.setWidgetTopBottom( buffer, 8.0d, Unit.PX, 4.0d, Unit.PX );
		root.setWidgetBottomHeight( prebuffer, 0.0d, Unit.PX, 4.0d, Unit.PX );

		root.setHeight( "16px" );

		setPlayback( 0.0d );
		setBuffer( 0.0d );
		setPrebuffer( 0.0d );

		root.addStyleName( "xinosProgressBar" );
	}


	public void setPlayback ( double percent ) {
		if ( percent <= 0.0d ) {
			root.setWidgetLeftWidth( playback, 0.0d, Unit.PX, 0.0d, Unit.PCT );
		} else {
			root.setWidgetLeftWidth( playback, 0.0d, Unit.PX, percent, Unit.PCT );
		}
		checkIndefinite();
	}


	private void checkIndefinite() {
		if ( bufferIsIndefinite ) {
			bufferX += 10.0d;
			if ( bufferX > 90.0d ) {
				bufferX = 0.0d;
			}
			root.setWidgetLeftWidth( buffer, bufferX, Unit.PCT, 10.0d, Unit.PCT );
		}

		if ( prebufferIsIndefinite ) {
			prebufferX += 10.0d;
			if ( prebufferX > 90.0d ) {
				prebufferX = 0.0d;
			}
			root.setWidgetLeftWidth( prebuffer, prebufferX, Unit.PCT, 10.0d, Unit.PCT );
		}
	}


	public void setBuffer ( double percent ) {
		if ( percent <= 0.0d ) {
			if ( ! bufferIsIndefinite ) {
				bufferX = 0.0d;
			}
			bufferIsIndefinite = true;
			root.setWidgetLeftWidth( buffer, 0.0d, Unit.PX, 0.0d, Unit.PCT );
		} else {
			bufferIsIndefinite = false;
			root.setWidgetLeftWidth( buffer, 0.0d, Unit.PX, percent, Unit.PCT );
		}
	}


	public void setPrebuffer ( double percent ) {
		if ( percent <= 0.0d ) {
			if ( ! prebufferIsIndefinite ) {
				prebufferX = 0.0d;
			}
			prebufferIsIndefinite = true;
			root.setWidgetLeftWidth( prebuffer, 0.0d, Unit.PX, 0.0d, Unit.PCT );
		} else {
			prebufferIsIndefinite = false;
			root.setWidgetLeftWidth( prebuffer, 0.0d, Unit.PX, percent, Unit.PCT );
		}
	}


	@Override
	public Widget asWidget () {
		if (root == null) {
			setup();
		}

		return root;
	}

}
