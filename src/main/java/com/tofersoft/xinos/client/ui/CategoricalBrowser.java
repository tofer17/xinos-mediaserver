package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.shared.dm.Categorical;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;


public class CategoricalBrowser implements IsWidget, CatTrackBrowser {

	private static final Logger logger = Logger.getLogger( CategoricalBrowser.class.getSimpleName() );

	private VerticalLayoutContainer root = null;

	private Categorical browsing;
	private ComboBox<Category> catCombo;
	private Grid<Data> catData;

	private GridSelectionModel<Data> selectionModel;

	private CatTrackBrowser parent;
	private CatTrackBrowser child;


	public CategoricalBrowser ( Categorical browsing ) {
		super();

		this.browsing = browsing;

		parent = null;
		child = null;
	}


	private void injectStore ( ListStore<Category> store ) {

		for (Categorical categorical : Categorical.values()) {
			if (categorical.name != null) {
				store.add( new Category( categorical ) );
			}
		}
	}


	private void setup () {
		root = new VerticalLayoutContainer();

		CategoryProperties catProps = GWT.create( CategoryProperties.class );
		ListStore<Category> store = new ListStore<CategoricalBrowser.Category>( catProps.value() );
		injectStore( store );

		catCombo = new ComboBox<CategoricalBrowser.Category>( store, catProps.name() );
		catCombo.setValue( store.get( 0 ) );
		catCombo.setAllowBlank( false );
		catCombo.setForceSelection( true );
		catCombo.setTriggerAction( TriggerAction.ALL );

		DataProperties dataProps = GWT.create( DataProperties.class );

		ColumnConfig<Data, String> it = new ColumnConfig<CategoricalBrowser.Data, String>( dataProps.value() );

		List<ColumnConfig<Data, ?>> cols = new ArrayList<ColumnConfig<Data, ?>>();
		cols.add( it );

		ListStore<Data> dataStore = new ListStore<Data>( dataProps.key() );

		ColumnModel<Data> cm = new ColumnModel<CategoricalBrowser.Data>( cols );

		selectionModel = new GridSelectionModel<Data>();
		selectionModel.setSelectionMode( SelectionMode.MULTI );

		selectionModel.addSelectionChangedHandler( new SelectionChangedHandler<Data>() {

			@Override
			public void onSelectionChanged ( SelectionChangedEvent<Data> event ) {
				logger.info( browsing.name + " => onSelectionChanged: " + event.getSelection() );

				handleSelectionChange( event );
			}
		} );

		catData = new Grid<CategoricalBrowser.Data>( dataStore, cm );

		catData.setSelectionModel( selectionModel );
		catData.getView().setAutoExpandColumn( it );
		catData.setHideHeaders( true );

		root.add( catCombo, new VerticalLayoutData( 1, -1 ) );
		root.add( catData, new VerticalLayoutData( 1, 1 ) );
	}


	private void handleSelectionChange ( SelectionChangedEvent<Data> event ) {

		if (event.getSelection() == null || event.getSelection().size() == 0) {
			return;
		}

		if (selectionModel.getSelectedItems().size() < 1) {
			selectionModel.select( 0, false );
		}

		if (child != null) {
			child.onParentSelectionChanged();
		}
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
		this.child = child;
	}


	@Override
	public void onParentSelectionChanged () {

		List<Query> queryList;

		queryList = getQueryList();

		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess ( List<String> result ) {
				ListStore<Data> store = catData.getStore();
				store.clear();

				store.add( new Data( "All (" + result.size() + " " + browsing.name + "s)" ) );
				for (String s : result) {
					store.add( new Data( s != null ? s : "[NULL]" ) );
				}

				selectionModel.select( 0, false );

			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Foobar!", caught );
			}
		};

		XinosService.core().queryForStrings( queryList, callback );
	}


	@Override
	public List<Query> getQueryList () {
		List<Query> queryList = new ArrayList<Query>();
		if (parent != null) {
			queryList.addAll( parent.getQueryList() );
		}

		List<String> values = new ArrayList<String>();
		if (selectionModel != null) {
			for (Data d : selectionModel.getSelectedItems()) {
				values.add( d.value );
			}
		}

		queryList.add( new Query( browsing, values ) );

		return queryList;
	}


	@Override
	public Widget asWidget () {

		if (root == null) {
			setup();
		}

		return root;
	}


	@Override
	public List<ListFilter<Track, ?>> getFilters () {

		List<ListFilter<Track, ?>> filters = new ArrayList<ListFilter<Track, ?>>();

		if (parent != null) {
			filters.addAll( parent.getFilters() );
		}

		final ListStore<String> stringStore = new ListStore<String>( new ModelKeyProvider<String>() {

			@Override
			public String getKey ( String item ) {
				return item;
			}
		});

		ListStore<?> store = null;

		ValueProvider<Track, ?> provider = null;
		ValueProvider<Track, String> stringProvider = null;

		switch (browsing) {
		case ALL:
			break;

		case ALBUM:
			store = stringStore;
			stringProvider = TrackProps.get.album();
			break;
		case ALBUM_ARTIST:
			store = stringStore;
			stringProvider = TrackProps.get.albumArtist();
			break;
		case ARTIST:
			store = stringStore;
			stringProvider = TrackProps.get.artist();
			break;
		case COMPOSER:
			store = stringStore;
			stringProvider = TrackProps.get.composer();
			break;
		case GENRE:
			store = stringStore;
			stringProvider = TrackProps.get.genre();
			break;
		case KIND:
			store = stringStore;
			stringProvider = TrackProps.get.format();
			break;

		case CHANNELS:
			break;
		case DURATION:
			break;
		case YEAR:
			break;
		case MODIFIED:
			break;
		case SAMPLE_RATE:
			break;
		case SIZE:
			break;
		default:
			break;

		}

		ListFilter<Track, ?> filter = null;

		if ( store == stringStore ) {
			for ( Data d : catData.getStore().getAll() ) {
				stringStore.add( d.getValue() );
			}

			filter = new ListFilter<Track, String>( stringProvider, stringStore );
		}

		// TODO nope.
		if (store == null)
			return filters;

		filters.add( filter );

		return filters;
	}

	public class Category {

		private Categorical category;
		private String value;


		public Category ( Categorical category ) {
			this.category = category;
			this.value = category.name;
		}


		public Categorical getCategory () {
			return category;
		}


		public void setCategory ( Categorical category ) {
			this.category = category;
		}


		public String getValue () {
			return value;
		}


		public void setValue ( String value ) {
			this.value = value;
		}

	}

	public interface CategoryProperties extends PropertyAccess<Category> {

		public ModelKeyProvider<Category> value ();


		@Path ( "value" )
		public LabelProvider<Category> name ();

	}

	public class Data {

		private String value;


		public Data ( String value ) {
			this.value = value;
		}


		public String getValue () {
			return value;
		}


		public void setValue ( String value ) {
			this.value = value;
		}

	}

	public interface DataProperties extends PropertyAccess<Data> {

		@Path ( "value" )
		public ModelKeyProvider<Data> key ();


		public ValueProvider<Data, String> value ();

	}

}
