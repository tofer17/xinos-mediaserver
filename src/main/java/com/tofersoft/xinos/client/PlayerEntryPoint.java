package com.tofersoft.xinos.client;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.dom.client.SourceElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.event.dom.client.LoadedMetadataEvent;
import com.google.gwt.event.dom.client.LoadedMetadataHandler;
import com.google.gwt.event.dom.client.ProgressEvent;
import com.google.gwt.event.dom.client.ProgressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.dom.client.TimeRanges;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.client.ui.Images;
import com.tofersoft.xinos.client.ui.ProgressBar;
import com.tofersoft.xinos.client.ui.TrackProps;
import com.tofersoft.xinos.shared.dm.Track;


public class PlayerEntryPoint implements EntryPoint, SelectHandler {

	private static final Logger logger = Logger.getLogger( PlayerEntryPoint.class.getSimpleName() );

	private static PlayerEntryPoint instance = null;

	private final List<Integer> queue = new ArrayList<Integer>();

	private Audio audioA;
	private Audio audioB;

	private Audio audioPlaying;
	private Audio audioBuffering;

	private SourceElement audioSourceA = null;
	private SourceElement audioSourceB = null;

	private SourceElement sourcePlaying;
	private SourceElement sourceBuffering;

	private boolean abReadyToPlay = false;
	private boolean abFullyBuffered = false;

	private Grid<Track> grid;

	final TextButton prev = new TextButton( "", Images.get().previous() );
	final TextButton play = new TextButton( "", Images.get().loading() );
	final TextButton next = new TextButton( "", Images.get().next() );

	final ProgressBar buffProg = new ProgressBar();

	final Slider volume = new Slider( false );

	static {
		logger.setLevel( Level.INFO );
	}


	@Override
	public void onModuleLoad () {

		Window.addWindowClosingHandler( new ClosingHandler() {

			@Override
			public void onWindowClosing ( ClosingEvent event ) {
				logger.info( "Player is closing..." );
				alertPlayerClosing();
				logger.info( "...parent window was notified." );
			}
		} );

		exportMethods();

		if (instance == null) {
			instance = this;
		}

		setup();

		addPlayer();

		String tracksString = getTrackList();
		if (!"".equals( tracksString )) {
			playTracks( tracksString );
		}

	}


	private Audio setupAudio () {
		final Audio audio = Audio.createIfSupported();

		audio.setAutoplay( false );
		audio.setEnabled( true );
		audio.setControls( true );
		audio.setVisible( false );
		audio.setWidth( "100%" );
		audio.setPreload( MediaElement.PRELOAD_AUTO );

		audio.addLoadedMetadataHandler( new LoadedMetadataHandler() {

			@Override
			public void onLoadedMetadata ( LoadedMetadataEvent event ) {
				logger.finest( event.toDebugString() + " " + audio.getDuration() + " -> " + audio.getNetworkState() );

				doProgCheck( audio );

				handleLoadedMetadata( audio );

			}
		} );

		audio.addEndedHandler( new EndedHandler() {

			@Override
			public void onEnded ( EndedEvent event ) {
				logger.info( "Playback ended." );
				handlePlaybackEnded( audio );
			}
		} );

		audio.addProgressHandler( new ProgressHandler() {

			@Override
			public void onProgress ( ProgressEvent event ) {
				logger.finest( "PROG:'" + event.toDebugString() + "' " + audio.getDuration() + " -> " + audio.getNetworkState() );

				doProgCheck( audio );
			}
		} );

		audio.addCanPlayThroughHandler( new CanPlayThroughHandler() {

			@Override
			public void onCanPlayThrough ( CanPlayThroughEvent event ) {
				logger.finest( "OCPT: '" + event.toString() + "' " + audio.getDuration() + " -> " + audio.getNetworkState() );
				doProgCheck( audio );
			}
		} );

		return audio;
	}


	private void setup () {

		audioA = setupAudio();
		audioSourceA = audioA.addSource( "" );

		audioB = setupAudio();
		audioSourceB = audioB.addSource( "" );

		audioPlaying = audioA;
		audioBuffering = audioB;

		sourcePlaying = audioSourceA;
		sourceBuffering = audioSourceB;

		// final TrackProperties props = GWT.create( TrackProperties.class );
		final TrackProps.TrackPropertyAccess props = TrackProps.get;

		final ColumnConfig<Track, String> titleColumn = new ColumnConfig<Track, String>( props.title(), 120, "Title" );
		final ColumnConfig<Track, String> albumColumn = new ColumnConfig<Track, String>( props.album(), 100, "Album" );
		final ColumnConfig<Track, Integer> trackSeqColumn = new ColumnConfig<Track, Integer>( props.trackSeq(), 50, "Track #" );
		final ColumnConfig<Track, Integer> discSeqColumn = new ColumnConfig<Track, Integer>( props.discSeq(), 50, "Disc #" );

		final List<ColumnConfig<Track, ?>> cols = new ArrayList<ColumnConfig<Track, ?>>();
		cols.add( titleColumn );
		cols.add( albumColumn );
		cols.add( trackSeqColumn );
		cols.add( discSeqColumn );

		final ListStore<Track> store = new ListStore<Track>( props.key() );

		final ColumnModel<Track> cm = new ColumnModel<Track>( cols );

		grid = new Grid<Track>( store, cm );

		grid.getView().setAutoExpandColumn( titleColumn );

		prev.setSize( "24px", "24px" );
		play.setSize( "24px", "24px" );
		next.setSize( "24px", "24px" );
		volume.setWidth( "5.5em" );

		volume.setIncrement( 1 );
		volume.setValue( 75 );

		final LayoutPanel root = new LayoutPanel();

		prev.addSelectHandler( this );
		play.addSelectHandler( this );
		play.setEnabled( false );
		next.addSelectHandler( this );

		volume.addValueChangeHandler( new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange ( ValueChangeEvent<Integer> event ) {
				final int volInt = event.getValue();
				final double vol = (double) ((double) volInt / 100.0d);
				audioA.setVolume( vol );
				audioB.setVolume( vol );
			}
		} );

		final LayoutPanel controls = new LayoutPanel();
		controls.add( audioA );
		controls.add( audioB );
		controls.add( prev );
		controls.add( play );
		controls.add( next );
		controls.add( buffProg );
		controls.add( volume );

		controls.setWidgetLeftWidth( prev, 0.0d, Unit.EM, 2.0d, Unit.EM );
		controls.setWidgetLeftWidth( play, 2.0d, Unit.EM, 4.0d, Unit.EM );
		controls.setWidgetLeftWidth( next, 4.0d, Unit.EM, 6.0d, Unit.EM );
		controls.setWidgetLeftWidth( buffProg, 6.0d, Unit.EM, 8.0d, Unit.EM );
		controls.setWidgetRightWidth( volume, 2.5d, Unit.EM, 8.0d, Unit.EM );

		final VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add( grid );
		vlc.getScrollSupport().setScrollMode( ScrollMode.AUTO );

		root.add( controls );
		root.add( vlc );

		root.setWidgetTopHeight( controls, 0.25d, Unit.EM, 3.0d, Unit.EM );
		root.setWidgetTopBottom( vlc, 3.25d, Unit.EM, 0.0d, Unit.PX );

		RootLayoutPanel.get().add( root );

		final Timer t = new Timer() {

			@Override
			public void run () {
				final double duration = audioPlaying.getDuration();
				if (Double.isNaN( duration ) || duration <= 0.0d) {
					buffProg.setPlayback( 0.0d );
				} else {
					buffProg.setPlayback( (audioPlaying.getCurrentTime() / duration) * 100.0d );
				}
			}
		};

		t.scheduleRepeating( 500 );
	}


	private void doProgCheck ( Audio audio ) {

		double[][] dubs = new double[audio.getBuffered().length()][2];
		double aDur = 100.0d / audio.getDuration();
		double d1 = 0.0d;

		if (dubs.length == 0) {
			logger.fine( "Empty buffer" );
		}

		for (int i = 0; i < dubs.length; i++) {
			TimeRanges tr = audio.getBuffered();
			double d0 = aDur * tr.start( i );
			d1 = aDur * (tr.end( i ) - tr.start( i ));
			logger.fine( "i=" + i + " d0=" + d0 + " d1=" + d1 );
		}

		if (d1 <= 0.0d)
			return;

		if (audio == audioPlaying) {
			buffProg.setBuffer( d1 );
		} else {
			buffProg.setPrebuffer( d1 );
		}

		if (d1 >= 99.9d) {
			handleFullyBuffered( audio );
		}

	}


	private void handleLoadedMetadata ( Audio audio ) {
		if (audio == audioPlaying) {

			audio.play();

			play.setEnabled( true );
			play.setIcon( Images.get().pause() );
			play.setTitle( "Pause" );

		} else {
			abReadyToPlay = true;
		}
	}


	private void handleFullyBuffered ( Audio audio ) {
		if (audio == audioPlaying) {
			// Instruct audioBuffering to stream next track
			Track nextTrack = peekNext();

			buffProg.setPrebuffer( -1.0d );
			final String streamURL = GWT.getModuleBaseURL() + "stream?t=" + nextTrack.getId() + "&x=" + System.currentTimeMillis();

			sourceBuffering.setSrc( streamURL );

			logger.info( "Set buffering url to '" + streamURL + "'" );
			audioBuffering.load();

		} else {
			abFullyBuffered = true;
		}
	}


	private void handlePlaybackEnded ( Audio audio ) {
		if (audio != audioPlaying) {
			logger.log( Level.SEVERE, "The buffering audio element just finished playing!!" );
			return;
		}

		grid.getSelectionModel().select( peekNext(), false );

		final Audio flipAudio = audioPlaying;
		audioPlaying = audioBuffering;
		audioBuffering = flipAudio;

		final SourceElement flipSource = sourcePlaying;
		sourcePlaying = sourceBuffering;
		sourceBuffering = flipSource;

		if (abReadyToPlay) {
			audioPlaying.play();
		}

		abReadyToPlay = false;

		if (abFullyBuffered) {
			handleFullyBuffered( audioPlaying );
		}

		abFullyBuffered = false;
	}


	@Override
	public void onSelect ( SelectEvent event ) {

		if (event.getSource() == play) {

			if (audioPlaying.isPaused()) {
				audioPlaying.play();
				play.setIcon( Images.get().pause() );
				play.setTitle( "Pause" );
			} else {
				audioPlaying.pause();
				play.setIcon( Images.get().play() );
				play.setTitle( "Play" );
			}

		} else if (event.getSource() == prev) {
			logger.info( "Prev clicked." );

		} else if (event.getSource() == next) {
			handlePlaybackEnded( audioPlaying );
		}

	}


	private Track peekNext () {
		Track selected = grid.getSelectionModel().getSelectedItem();
		int selectedIndex = 0;
		List<Track> store = grid.getStore().getAll();
		for (int i = 0; i < store.size(); i++) {
			Track t = store.get( i );
			if (selected == t) {
				selectedIndex = i;
			}
		}

		selectedIndex++;

		if (selectedIndex >= store.size()) {
			selectedIndex = 0;
		}

		return store.get( selectedIndex );

	}


	private void refreshGrid () {

		AsyncCallback<List<Track>> callback = new AsyncCallback<List<Track>>() {

			@Override
			public void onSuccess ( List<Track> result ) {

				ListStore<Track> store = grid.getStore();
				store.clear();

				for (Track track : result) {
					store.add( track );
				}

				final String streamURL = GWT.getModuleBaseURL() + "stream?t=" + store.get( 0 ).getId() + "&x=" + System.currentTimeMillis();
				logger.info( "Setting streamURL to '" + streamURL + "'" );

				buffProg.setBuffer( -1.0d );
				audioSourceA.setSrc( streamURL );
				audioA.load();

				grid.getSelectionModel().select( 0, false );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Fudge!", caught );
			}
		};

		XinosService.core().getTracks( queue, callback );

	}


	public static void enqueueTracks ( String tracks ) {

		List<Integer> trackIds = new ArrayList<Integer>();

		for (String s : tracks.split( "," )) {
			Integer trackId = new Integer( s );
			trackIds.add( trackId );
			logger.info( "Enqueued " + trackId );
		}

		instance.queue.addAll( trackIds );

		instance.refreshGrid();
	};


	public static void playTracks ( String tracks ) {

		instance.queue.clear();
		enqueueTracks( tracks );
	}


	//@formatter:off
	private static native void exportMethods () /*-{
		$wnd.enqueueTracks = $entry(@com.tofersoft.xinos.client.PlayerEntryPoint::enqueueTracks(Ljava/lang/String;));
		$wnd.playTracks = $entry(@com.tofersoft.xinos.client.PlayerEntryPoint::playTracks(Ljava/lang/String;));

	}-*/;


	private native static String getTrackList () /*-{
		return $wnd.opener.getTrackList();
	}-*/;


	private native static void addPlayer () /*-{
		$wnd.opener.addPlayer( $wnd.self );
	}-*/;

	private native static void alertPlayerClosing () /*-{
		$wnd.opener.alertPlayerClosing( $wnd.self );
	}-*/;
	//@formatter:on

}
