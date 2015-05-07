package com.tofersoft.xinos.client;


import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.tofersoft.xinos.client.ui.MenuItemAbout;
import com.tofersoft.xinos.client.ui.HeaderPanel;
import com.tofersoft.xinos.client.ui.LogoPanel;
import com.tofersoft.xinos.client.ui.MainContentPanel;
import com.tofersoft.xinos.client.ui.MainMenuPanel;
import com.tofersoft.xinos.client.ui.MenuItemMedia;
import com.tofersoft.xinos.client.ui.MenuItemMusic;
import com.tofersoft.xinos.client.ui.MenuItemPlaylists;
import com.tofersoft.xinos.client.ui.MenuItemUpload;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class XinosEntryPoint implements EntryPoint {

	private static final Logger logger = Logger.getLogger( XinosEntryPoint.class.getSimpleName() );

	private static JavaScriptObject playerJSO = null;
	private static String tracksString = "";

	private final LogoPanel logo = new LogoPanel();
	private final MainContentPanel mainContent = new MainContentPanel();
	private final MainMenuPanel mainMenu = new MainMenuPanel( mainContent );
	private final HeaderPanel header = new HeaderPanel();

	private final DockLayoutPanel dock = new DockLayoutPanel( Unit.PX );


	@Override
	public void onModuleLoad () {

		exportMethods();

		final LayoutPanel west = new LayoutPanel();
		west.add( logo );
		west.add( mainMenu );

		west.setWidgetTopHeight( logo, 0.0d, Unit.PX, 83.0, Unit.PX );
		west.setWidgetTopBottom( mainMenu, 83.0d, Unit.PX, 0.0, Unit.PX );

		final LayoutPanel east = new LayoutPanel();
		east.add( header );
		east.add( mainContent );

		east.setWidgetTopHeight( header, 0.0d, Unit.EM, 2.0d, Unit.EM );
		east.setWidgetTopBottom( mainContent, 2.0d, Unit.EM, 0.0d, Unit.PX );

		dock.addWest( west, 120.0d );
		dock.add( east );

		final RootLayoutPanel root = RootLayoutPanel.get();

		root.add( dock );

		loadMenus();

		root.onResize();
		root.forceLayout();

	}


	public static String callback ( String s ) {
		logger.info( "XinosEntryPoint called-back with: '" + s + "'" );
		return "Hello '" + s + "'";
	}


	public static String getTrackList () {
		return tracksString;
	}


	public static void addPlayer ( JavaScriptObject jso ) {
		playerJSO = jso;
		// enqueueTracks( jso, "1,2,3,4,5" );
	}

	public static void alertPlayerClosing ( JavaScriptObject jso ) {
		if ( jso == playerJSO ) {
			playerJSO = null;
		}
	}


	public static void openPlayer () {
		String url = GWT.getHostPageBaseURL() + "Player.html";
		if (!GWT.isProdMode()) {
			url += "?gwt.codesvr=" + Window.Location.getParameter( "gwt.codesvr" );
		}

		logger.info( "Opening player at '" + url + "'" );

		Window.open( url, "XinosPlayer", "dialog=yes,width=400,height=300,minimizable=yes,menubar=no" );

	}


	public static void enqueueTracks ( String tracksString ) {
		if ( playerJSO == null ) {
			XinosEntryPoint.tracksString = tracksString;
			openPlayer();
		} else {
			enqueueTracks( playerJSO, tracksString );
		}
	}


	public static void playTracks ( String tracksString ) {
		if ( playerJSO == null ) {
			XinosEntryPoint.tracksString = tracksString;
			openPlayer();
		} else {
			playTracks( playerJSO, tracksString );
		}
	}


	//@formatter:off
	private static native void enqueueTracks (JavaScriptObject playerJSO, String tracks) /*-{
		playerJSO.enqueueTracks( tracks );
	}-*/;


	private static native void playTracks (JavaScriptObject playerJSO, String tracks) /*-{
		playerJSO.playTracks( tracks );
	}-*/;


	private static native void exportMethods () /*-{
		$wnd.callback = $entry(@com.tofersoft.xinos.client.XinosEntryPoint::callback(Ljava/lang/String;));
		$wnd.getTrackList = $entry(@com.tofersoft.xinos.client.XinosEntryPoint::getTrackList());
		$wnd.addPlayer = $entry(@com.tofersoft.xinos.client.XinosEntryPoint::addPlayer(Lcom/google/gwt/core/client/JavaScriptObject;));
		$wnd.alertPlayerClosing = $entry(@com.tofersoft.xinos.client.XinosEntryPoint::alertPlayerClosing(Lcom/google/gwt/core/client/JavaScriptObject;));
	}-*/;
	//@formatter:on

	private void loadMenus () {

		mainMenu.addMenuComponent( new MenuItemUpload() );
		mainMenu.addMenuComponent( new MenuItemMusic() );
		mainMenu.addMenuComponent( new MenuItemAbout() );
		mainMenu.addMenuComponent( new MenuItemMedia() );
		mainMenu.addMenuComponent( new MenuItemPlaylists() );

		dock.onResize();
		mainMenu.onResize();
		mainContent.onResize();
		dock.onResize();
	}


	public static void yes () {
		logger.info( "YES!" );
	}

}
