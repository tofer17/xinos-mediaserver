package com.tofersoft.xinos.client.ui;


import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.tofersoft.xinos.client.XinosEntryPoint;
import com.tofersoft.xinos.shared.dm.Categorical;


public class MenuItemMusic extends BorderLayoutContainer implements MenuComponentItem, MainContentComponent {

	private static final Logger logger = Logger.getLogger( MenuItemMusic.class.getSimpleName() );

	private Label header = new Label( "Music" );

	private ContentPanel north = new ContentPanel();
	private BorderLayoutContainer northBLC = new BorderLayoutContainer();
	private ContentPanel south = new ContentPanel();

	private ContentPanel alpha = new ContentPanel();
	private ContentPanel beta = new ContentPanel();
	private ContentPanel gamma = new ContentPanel();

	private TrackBrowser trackBrowser = null;

	private com.google.gwt.user.client.ui.Grid sub = null;

	private TextButton playButton;
	private TextButton enqueueButton;
	private TextButton streamButton;
	private TextButton downloadButton;


	public MenuItemMusic () {
		super();
		setupSub();
	}


	private void setupSub () {

		playButton = new TextButton( "Play" );
		playButton.setTitle( "Play current selection (or everything that is displayed if nothing is selected)" );
		playButton.setEnabled( true );
		playButton.setWidth( "100%" );
		playButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handlePlayEvent( event );
			}
		} );

		enqueueButton = new TextButton( "Queue" );
		enqueueButton.setTitle( "Enqueue current selection to player (or everything that is displayed if nothing is selected)" );
		enqueueButton.setEnabled( true );
		enqueueButton.setWidth( "100%" );
		enqueueButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleEnqueueEvent( event );
			}
		} );

		streamButton = new TextButton( "Stream Playlist" );
		streamButton.setTitle( "Stream current selection as playlist (or everything that is displayed if nothing is selected)" );
		streamButton.setEnabled( true );
		streamButton.setWidth( "100%" );
		streamButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleStreamEvent( event );
			}
		} );

		downloadButton = new TextButton( "Download" );
		downloadButton.setTitle( "Transcode, zip, and download selection (or everything that is displayed if nothing is selected)" );
		downloadButton.setEnabled( false );
		downloadButton.setWidth( "100%" );
		downloadButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				;
			}
		} );

		sub = new com.google.gwt.user.client.ui.Grid( 4, 1 );
		sub.setWidth( "100%" );

		sub.setWidget( 0, 0, playButton );
		sub.setWidget( 1, 0, enqueueButton );
		sub.setWidget( 2, 0, streamButton );
		sub.setWidget( 3, 0, downloadButton );

	}


	private void handlePlayEvent ( SelectEvent event ) {
		String tracksString = trackBrowser.getTracksString();
		logger.info( "PLAY: TracksString is '" + tracksString + "'" );
		XinosEntryPoint.playTracks( tracksString );
	}


	private void handleEnqueueEvent ( SelectEvent event ) {
		String tracksString = trackBrowser.getTracksString();
		logger.info( "QUEUE: TracksString is '" + tracksString + "'" );
		XinosEntryPoint.enqueueTracks( tracksString );
	}


	private void handleStreamEvent ( SelectEvent event ) {
		String tracksString = trackBrowser.getTracksString();
		logger.info( "QUEUE: TracksString is '" + tracksString + "'" );

		String url = GWT.getHostPageBaseURL() + "Xinos/play?ids=" + tracksString;

		logger.info( "THE URL IS: '" + url + "'" );

		Window.Location.assign( url );

	}


	@Override
	protected void onLoad () {
		super.onLoad();

		alpha.setHeaderVisible( false );
		alpha.setBodyBorder( false );

		beta.setHeaderVisible( false );
		beta.setBodyBorder( false );

		gamma.setHeaderVisible( false );
		gamma.setBodyBorder( false );

		BorderLayoutData westBLD = new BorderLayoutData( 0.33d );
		westBLD.setCollapsible( false );
		westBLD.setSplit( true );
		westBLD.setMargins( new Margins( 0, 4, 0, 2 ) );

		BorderLayoutData eastBLD = new BorderLayoutData( 0.33d );
		eastBLD.setCollapsible( false );
		eastBLD.setSplit( true );
		eastBLD.setMargins( new Margins( 2 ) );
		eastBLD.setMargins( new Margins( 0, 2, 0, 4 ) );

		northBLC.setBorders( true );

		northBLC.setWestWidget( alpha, westBLD );
		northBLC.setEastWidget( gamma, eastBLD );
		northBLC.setCenterWidget( beta, new MarginData( 0, 2, 0, 2 ) );

		north.setHeaderVisible( false );
		north.setWidget( northBLC );

		BorderLayoutData rootBLD = new BorderLayoutData( 0.25 );
		rootBLD.setCollapsible( false );
		rootBLD.setSplit( true );
		rootBLD.setMargins( new Margins( 0, 0, 2, 0 ) );

		setBorders( false );

		south.setHeaderVisible( false );
		south.setBodyBorder( false );

		setNorthWidget( north, rootBLD );

		setCenterWidget( south, new MarginData( 2, 2, 0, 2 ) );

		CategoricalBrowser alphaBrowser = new CategoricalBrowser( Categorical.GENRE );
		CategoricalBrowser betaBrowser = new CategoricalBrowser( Categorical.ARTIST );
		CategoricalBrowser gammaBrowser = new CategoricalBrowser( Categorical.ALBUM );

		gammaBrowser.setParentBrowser( betaBrowser );
		betaBrowser.setParentBrowser( alphaBrowser );

		alpha.add( alphaBrowser );
		beta.add( betaBrowser );
		gamma.add( gammaBrowser );

		trackBrowser = new TrackBrowser();
		trackBrowser.setParentBrowser( gammaBrowser );

		south.add( trackBrowser );

		alphaBrowser.onParentSelectionChanged();
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
