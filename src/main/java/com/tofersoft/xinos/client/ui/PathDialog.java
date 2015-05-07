package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.client.ui.PathBrowserDialog.BrowserDialogHideEvent;
import com.tofersoft.xinos.client.ui.PathBrowserDialog.BrowserDialogHideHandler;


public class PathDialog extends Dialog implements DialogHideHandler {

	private static final Logger logger = Logger.getLogger( PathDialog.class.getSimpleName() );

	private final TextBox pathField = new TextBox();
	private String startPath;
	private final List<PathDialogHideHandler> handlers;


	public PathDialog () {
		this( null, null );
	}


	public PathDialog ( String title ) {
		this( title, null );
	}


	public PathDialog ( String title, String path ) {
		super();

		handlers = new ArrayList<PathDialog.PathDialogHideHandler>();

		setHeadingText( title );

		startPath = path;
		addDialogHideHandler( this );
	}


	@Override
	public void show () {

		setModal( true );
		setBodyBorder( true );
		setBlinkModal( true );
		setWidth( 400 );
		setHeight( 225 );
		setHideOnButtonClick( true );
		getHeader().setIcon( Images.get().folderOpen() );

		final Label label = new Label( "Select path" );

		if (startPath == null || startPath.length() < 1) {
			fetchUserHome( pathField );
		} else {
			pathField.setValue( startPath );
		}
		pathField.setWidth( "95%" );

		final TextButton browseButton = new TextButton( "..." ) {

			@Override
			protected void onClick ( Event event ) {
				super.onClick( event );

				final PathBrowserDialog browseWindow = new PathBrowserDialog( pathField.getValue() );
				browseWindow.show();
				browseWindow.center();
				browseWindow.addBrowserDialogHideHandler( new BrowserDialogHideHandler() {

					@Override
					public void onDialogHide ( BrowserDialogHideEvent event ) {
						handleBrowserDialog( event );
					}
				} );
			}

		};
		browseButton.setTitle( "Browse..." );

		ToolBar tb = new ToolBar();
		tb.add( browseButton );

		final LayoutPanel root = new LayoutPanel();
		root.add( label );
		root.add( pathField );
		root.add( tb );

		root.setWidgetTopHeight( label, 0.0d, Unit.PX, 2.0d, Unit.EM );

		root.setWidgetTopHeight( pathField, 2.5d, Unit.EM, 2.0d, Unit.EM );
		root.setWidgetTopHeight( tb, 2.0d, Unit.EM, 2.5d, Unit.EM );

		root.setWidgetLeftRight( pathField, 0.0d, Unit.EM, 3.0d, Unit.EM );
		root.setWidgetRightWidth( tb, 0.0d, Unit.PX, 3.0d, Unit.EM );

		setWidget( root );

		setPredefinedButtons( PredefinedButton.OK, PredefinedButton.CANCEL );

		super.show();
	}


	private void handleBrowserDialog ( BrowserDialogHideEvent event ) {

		if (event.getHideButton() == PredefinedButton.OK) {
			pathField.setValue( event.getPath() );
		}

	}


	private void fetchUserHome ( final TextBox pathField ) {
		AsyncCallback<String> callback = new AsyncCallback<String>() {

			@Override
			public void onSuccess ( String result ) {
				pathField.setValue( result );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Darn it!", caught );
			}
		};

		XinosService.core().fetchUserHome( callback );
	}


	public void addPathDialogHideHandler ( PathDialogHideHandler handler ) {
		handlers.add( handler );
	}


	@Override
	public void onDialogHide ( DialogHideEvent event ) {
		final PathDialogHideEvent newEvent = new PathDialogHideEvent( event, pathField.getValue() );
		for (PathDialogHideHandler handler : handlers)
			handler.onDialogHide( newEvent );
	}

	public interface PathDialogHideHandler {

		public void onDialogHide ( PathDialogHideEvent event );
	}

	public class PathDialogHideEvent extends DialogHideEvent {

		private final DialogHideEvent parentEvent;
		private final String path;


		protected PathDialogHideEvent ( DialogHideEvent parentEvent, String path ) {
			super();

			this.parentEvent = parentEvent;
			this.path = path;
		}


		public String getPath () {
			return path;
		}


		@Override
		public Type<DialogHideHandler> getAssociatedType () {
			return parentEvent.getAssociatedType();
		}


		@Override
		public PredefinedButton getHideButton () {
			return parentEvent.getHideButton();
		}


		@Override
		public Component getSource () {
			return parentEvent.getSource();
		}


		@Override
		public String toDebugString () {
			return parentEvent.toDebugString();
		}


		@Override
		public String toString () {
			return parentEvent.toString();
		}

	}

}
