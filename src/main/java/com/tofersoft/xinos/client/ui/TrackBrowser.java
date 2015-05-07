package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.NumberCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;


public class TrackBrowser implements CatTrackBrowser, IsWidget {

	private static final Logger logger = Logger.getLogger( TrackBrowser.class.getSimpleName() );

	private static final String LOADING_TEXT = "...loading...";
	private static final NumberFormat bytesFo = NumberFormat.getFormat( "#.00" );

	private GridSelectionModel<Track> selectionModel;

	private Grid<Track> trackData;

	private CatTrackBrowser parent;

	private VerticalLayoutContainer root = null;
	private Label infoLabel = null;

	private GridFilters<Track> filters = null;


	public TrackBrowser () {
		super();

		trackData = null;
	}


	private void setupColumn ( ColumnConfig<Track, ?> cc, int width, boolean hidden ) {
		cc.setWidth( width );
		cc.setHidden( hidden );
	}


	public String getTracksString () {

		List<Track> tracks = trackData.getSelectionModel().getSelectedItems();
		if (tracks.isEmpty())
			tracks = trackData.getStore().getAll();

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Track track : tracks) {
			sb.append( first ? "" : "," ).append( track.getId() );
			first = false;
		}

		return sb.toString();
	}


	private void setup () {

		infoLabel = new Label( LOADING_TEXT );

		// final TrackProperties props = GWT.create( TrackProperties.class );
		final TrackProps.TrackPropertyAccess props = TrackProps.get;

		final ColumnConfig<Track, String> titleColumn = new ColumnConfig<Track, String>( props.title(), 120, "Title" );
		final ColumnConfig<Track, String> albumColumn = new ColumnConfig<Track, String>( props.album(), 100, "Album" );
		final ColumnConfig<Track, String> sequenceColumn = new ColumnConfig<Track, String>( props.sequence(), 50, "#" );
		final ColumnConfig<Track, String> albumArtistColumn = new ColumnConfig<Track, String>( props.albumArtist(), 50, "Album Artist" );
		final ColumnConfig<Track, Integer> bitRateColumn = new ColumnConfig<Track, Integer>( props.bitRate(), 50, "Bit Rate" );
		final ColumnConfig<Track, Integer> channelsColumn = new ColumnConfig<Track, Integer>( props.channels(), 50, "Channels" );
		final ColumnConfig<Track, String> commentColumn = new ColumnConfig<Track, String>( props.comment(), 50, "Comment" );
		final ColumnConfig<Track, Boolean> compilationColumn = new ColumnConfig<Track, Boolean>( props.compilation(), 50, "Compilation" );
		final ColumnConfig<Track, String> composerColumn = new ColumnConfig<Track, String>( props.composer(), 50, "Composer" );
		final ColumnConfig<Track, String> copyrightColumn = new ColumnConfig<Track, String>( props.copyright(), 50, "Copyright" );
		final ColumnConfig<Track, Date> creationTimeColumn = new ColumnConfig<Track, Date>( props.creationTime(), 50, "Creation Time" );
		final ColumnConfig<Track, Date> dateColumn = new ColumnConfig<Track, Date>( props.date(), 50, "Year" );
		final ColumnConfig<Track, String> encoderColumn = new ColumnConfig<Track, String>( props.encoder(), 50, "Encoder" );
		final ColumnConfig<Track, String> genreColumn = new ColumnConfig<Track, String>( props.genre(), 50, "Genre" );
		//final ColumnConfig<Track, String> groupingColumn = new ColumnConfig<Track, String>( props.grouping(), 50, "Grouping" );
		final ColumnConfig<Track, String> publisherColumn = new ColumnConfig<Track, String>( props.publisher(), 50, "Publisher" );
		final ColumnConfig<Track, Integer> sampleRateColumn = new ColumnConfig<Track, Integer>( props.sampleRate(), 50, "Sample Rate" );
		final ColumnConfig<Track, Long> durationColumn = new ColumnConfig<Track, Long>( props.millis(), 50, "Duration" );
		final ColumnConfig<Track, Long> fileSizeColumn = new ColumnConfig<Track, Long>( props.size(), 50, "File Size" );
		final ColumnConfig<Track, String> filenameColumn = new ColumnConfig<Track, String>( props.filename(), 50, "Filename" );
		final ColumnConfig<Track, Date> lastModColumn = new ColumnConfig<Track, Date>( props.modified(), 50, "Modified" );
		final ColumnConfig<Track, String> formatColumn = new ColumnConfig<Track, String>( props.format(), 50, "Format" );
		final ColumnConfig<Track, String> artistColumn = new ColumnConfig<Track, String>( props.artist(), 50, "Artist" );
		final ColumnConfig<Track, Integer> idColumn = new ColumnConfig<Track, Integer>( props.id(), 50, "Id" );

		durationColumn.setCell( new NumberCell<Long>() {

			@Override
			public void render ( Context context, Long value, SafeHtmlBuilder sb ) {
				sb.appendEscaped( Track.formatMillisToDuration( value ) );
			}

		} );

		fileSizeColumn.setCell( new NumberCell<Long>() {

			@Override
			public void render ( Context context, Long value, SafeHtmlBuilder sb ) {
				sb.appendEscaped( formatBytesToString( value ) );

			}

		} );

		bitRateColumn.setCell( new NumberCell<Integer>() {

			@Override
			public void render ( Context context, Integer value, SafeHtmlBuilder sb ) {
				int i = (int) Math.floor( value / 1000.0d );
				sb.append( i ).appendEscaped( " kbps" );
			}

		} );

		// @formatter:off
		setupColumn( idColumn,            90, false  );
		setupColumn( titleColumn,        220, false );
		setupColumn( albumColumn,        220, false );
		setupColumn( artistColumn,       220, false );
		setupColumn( sequenceColumn,      90, false );
		setupColumn( albumArtistColumn,  120, true  );
		setupColumn( formatColumn,        90, true  );
		setupColumn( bitRateColumn,       90, true  );
		setupColumn( channelsColumn,      90, true  );
		setupColumn( commentColumn,      120, true  );
		setupColumn( compilationColumn,  120, true  );
		setupColumn( composerColumn,     120, true  );
		setupColumn( copyrightColumn,    120, true  );
		setupColumn( creationTimeColumn, 150, true  );
		setupColumn( dateColumn,          90, true  );
		setupColumn( encoderColumn,       90, true  );
		setupColumn( genreColumn,         90, true  );
		//setupColumn( groupingColumn,      90, true  );
		setupColumn( publisherColumn,     90, true  );
		setupColumn( sampleRateColumn,   120, true  );
		setupColumn( durationColumn,      90, false );
		setupColumn( fileSizeColumn,      90, true  );
		setupColumn( filenameColumn,     120, true  );
		setupColumn( lastModColumn,       90, true  );
		// @formatter:on

		sequenceColumn.setComparator( new Comparator<String>() {

			@Override
			public int compare ( String o1, String o2 ) {
				// "1/2 (3/4)" <> "3/4 (1/?)" ( ? = 0 )
				// ^ ^
				// Disc sorts 1st, then Track

				final String[] s1a = o1.split( "\\(" ); // "1/2 "+"3/4)"
				final String[] s2a = o2.split( "\\(" ); // "3/4 "+"1/?)"

				final String o1Ds = s1a[1].split( "/" )[0].trim();

				final String o2Ds = s2a[1].split( "/" )[0].trim();

				final Integer o1D = "?".equals( o1Ds ) ? 0 : Integer.parseInt( o1Ds );

				final Integer o2D = "?".equals( o2Ds ) ? 0 : Integer.parseInt( o2Ds );

				int discComp = o1D.compareTo( o2D );
				if (discComp != 0)
					return discComp;

				final String o1Ts = s1a[0].split( "/" )[0].trim();
				final String o2Ts = s2a[0].split( "/" )[0].trim();

				final Integer o1T = "?".equals( o1Ts ) ? 0 : Integer.parseInt( o1Ts );
				final Integer o2T = "?".equals( o2Ts ) ? 0 : Integer.parseInt( o2Ts );

				return o1T.compareTo( o2T );
			}
		} );

		final List<ColumnConfig<Track, ?>> cols = new ArrayList<ColumnConfig<Track, ?>>();

		cols.add( idColumn );

		cols.add( titleColumn );
		cols.add( albumColumn );
		cols.add( artistColumn );
		cols.add( durationColumn );
		cols.add( sequenceColumn );

		cols.add( genreColumn );
		cols.add( albumArtistColumn );
		cols.add( composerColumn );
		cols.add( dateColumn );
		cols.add( copyrightColumn );
		cols.add( publisherColumn );
		//cols.add( groupingColumn );

		cols.add( channelsColumn );
		cols.add( formatColumn );
		cols.add( sampleRateColumn );
		cols.add( bitRateColumn );
		cols.add( encoderColumn );

		cols.add( filenameColumn );
		cols.add( fileSizeColumn );
		cols.add( lastModColumn );

		cols.add( commentColumn );
		cols.add( compilationColumn );
		cols.add( creationTimeColumn );

		final ListStore<Track> store = new ListStore<Track>( props.key() );

		final ColumnModel<Track> cm = new ColumnModel<Track>( cols );

		cm.addColumnWidthChangeHandler( new ColumnWidthChangeHandler() {

			@Override
			public void onColumnWidthChange ( ColumnWidthChangeEvent event ) {
				if (event.getIndex() == 0 && "1".equals( "2" )) {
					final StringBuilder sb = new StringBuilder();
					for (int i = 0; i < cm.getColumnCount(); i++) {
						final ColumnConfig<Track, ?> cc = cm.getColumn( i );
						sb.append( i ).append( ":" ).append( cc.getWidth() ).append( " " );
					}
				}
			}
		} );

		selectionModel = new GridSelectionModel<Track>();
		selectionModel.addSelectionChangedHandler( new SelectionChangedHandler<Track>() {

			@Override
			public void onSelectionChanged ( SelectionChangedEvent<Track> event ) {
				updateInfoLabel( null );
			}
		} );

		final GroupingView<Track> gv = new TrackGroupingView();
		gv.setShowGroupedColumn( true );
		gv.setForceFit( false );
		gv.setColumnLines( true );

		// gv.groupBy( albumColumn );

		trackData = new Grid<Track>( store, cm, gv );
		trackData.setSelectionModel( selectionModel );

		trackData.getView().setAutoExpandColumn( titleColumn );
		trackData.getView().setAutoExpandMin( 220 );

		filters = new GridFilters<Track>();
		filters.initPlugin( trackData );
		filters.setLocal( true );

		root = new VerticalLayoutContainer();

		root.add( trackData, new VerticalLayoutData( 1.0d, 1.0d, new Margins( 0, 0, 25, 0 ) ) );
		root.add( infoLabel );

	}


	@Override
	public Widget asWidget () {
		if (trackData == null) {
			setup();
		}

		return root;
	}


	@Override
	public void setParentBrowser ( CatTrackBrowser parent ) {
		this.parent = parent;
		if (parent != null) {
			parent.setChildBrowser( this );
		}
	}


	@Override
	public void setChildBrowser ( CatTrackBrowser child ) {
		; // This oughtn't happen.
	}


	/**
	 * Adapted from
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte
	 * -size-into-human-readable-format-in-java.
	 *
	 * @param bytes
	 * @return
	 */
	public static String formatBytesToString ( long bytes ) {
		int unit = 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log( bytes ) / Math.log( unit ));
		String pre = " " + "KMGTPE".charAt( exp - 1 ) + "B";
		return bytesFo.format( (bytes / Math.pow( unit, exp )) ) + pre;
		// return String.format( "%.1f %sB", bytes / Math.pow( unit, exp ), pre
		// );
	}


	private void updateInfoLabel ( List<Track> tracks ) {
		if (tracks == null) {
			tracks = trackData.getStore().getAll();
		}

		long listedMillis = 0;
		long listedBytes = 0;
		int listedCount = tracks.size();

		for (Track track : tracks) {
			listedMillis += track.getMillis();
			listedBytes += track.getSize();
		}

		tracks = selectionModel.getSelectedItems();

		long selectedMillis = 0;
		long selectedBytes = 0;
		int selectedCount = tracks.size();

		for (Track track : tracks) {
			selectedMillis += track.getMillis();
			selectedBytes += track.getSize();
		}

		infoLabel.setText(// @formatter:off

				new StringBuilder()
						.append( "Listed: " )
						.append(listedCount).append(" tracks ")
						.append( Track.formatMillisToDuration( listedMillis ) ).append(" ")
						.append( formatBytesToString( listedBytes ) ).append( " " )

						.append( "Selected: " )
						.append(selectedCount).append(" tracks ")
						.append( Track.formatMillisToDuration( selectedMillis ) ).append(" ")
						.append( formatBytesToString( selectedBytes ) ).append( " " )
						.toString()
				); // @formatter:on
	}


	@Override
	public void onParentSelectionChanged () {
		List<Query> queryList = getQueryList();

		AsyncCallback<List<Track>> callback = new AsyncCallback<List<Track>>() {

			@Override
			public void onSuccess ( List<Track> result ) {
				ListStore<Track> store = trackData.getStore();
				store.clear();

				store.addAll( result );
				updateInfoLabel( result );

				filters.removeAll();

				for ( ListFilter<Track, ?> filter : parent.getFilters() ) {
					filters.addFilter( filter );
					filter.setActive( true, true );

				}

			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "DANG!", caught );
			}
		};

		infoLabel.setText( LOADING_TEXT );
		XinosService.core().queryForTracks( queryList, callback );

	}


	@Override
	public List<Query> getQueryList () {
		List<Query> queryList = new ArrayList<Query>();

		if (parent != null) {
			queryList.addAll( parent.getQueryList() );
		}

		return queryList;
	}


	@Override
	public List<ListFilter<Track, ?>> getFilters () {
		return null;
	}

}
