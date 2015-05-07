package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.info.DefaultInfoConfig;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.info.InfoConfig.InfoPosition;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.client.ui.PathDialog.PathDialogHideEvent;
import com.tofersoft.xinos.client.ui.PathDialog.PathDialogHideHandler;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.scanner.ScannerControlMessage;
import com.tofersoft.xinos.shared.scanner.ScannerStatusMessage;
import com.tofersoft.xinos.shared.scanner.ScannerUpdate;


public class MenuItemMedia extends LayoutPanel implements MenuComponentItem, MainContentComponent {

	private static final Logger logger = Logger.getLogger( MenuItemMedia.class.getSimpleName() );

	private static final LibraryProperties props = GWT.create( LibraryProperties.class );

	private Label header = new Label( "Media" );
	private com.google.gwt.user.client.ui.Grid sub = null;

	private ListStore<Library> store;

	private TextButton delLibraryButton;
	private TextButton scanLibraryButton;

	private Grid<Library> grid;
	CheckBoxSelectionModel<Library> selectionModel;


	public MenuItemMedia () {
		setupSub();
	}


	@Override
	protected void onLoad () {
		super.onLoad();

		setupGrid();

		add( grid );

		setWidgetTopBottom( grid, 0.0d, Unit.EM, 0.0d, Unit.PX );

	}


	protected void handleScanLibraries ( SelectEvent event ) {
		for (Library library : selectionModel.getSelectedItems()) {
			handleScanButton( library );
		}
	}


	protected void handleDelLibrary ( SelectEvent event ) {
		for (final Library library : selectionModel.getSelectedItems()) {
			ConfirmMessageBox confirm = new ConfirmMessageBox(// @formatter:off
					"Remove Library", "Are you sure you want to remove the library \"" +
					library.getPath() + "\"? Click \"Yes\" if so or click \"No\" " +
					"to keep it." // @formatter:on
			);
			confirm.show();
			confirm.addDialogHideHandler( new DialogHideHandler() {

				@Override
				public void onDialogHide ( DialogHideEvent event ) {
					if (event.getHideButton() == PredefinedButton.YES) {
						removeLibrary( library );
					}
				}
			} );
		}
	}


	private void removeLibrary ( final Library library ) {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			@Override
			public void onSuccess ( Integer result ) {
				if (result.intValue() == 1) {
					store.remove( library );
				} else if (result.intValue() == -1) {
					Window.alert( "The library was not removed because there would be none left." );
				}
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Crud!", caught );
			}
		};

		XinosService.core().removeLibrary( library, callback );
	}


	private void handleAddLibrary ( SelectEvent event ) {

		PathDialog dialog = new PathDialog( "Select Library", null );
		dialog.show();
		dialog.center();

		dialog.addPathDialogHideHandler( new PathDialogHideHandler() {

			@Override
			public void onDialogHide ( PathDialogHideEvent event ) {
				if (event.getHideButton() == PredefinedButton.OK)
					addLibrary( event.getPath() );
			}
		} );

	}


	private void addLibrary ( String path ) {
		AsyncCallback<Library> callback = new AsyncCallback<Library>() {

			@Override
			public void onSuccess ( Library result ) {
				if (result.getId() > 0) {
					store.add( result );
				} else {
					Window.alert( "The library was not added." );
				}
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Crapola!", caught );
			}
		};

		XinosService.core().addLibrary( path, callback );
	}


	private void setupGrid () {

		IdentityValueProvider<Library> identity = new IdentityValueProvider<Library>();
		selectionModel = new CheckBoxSelectionModel<Library>( identity );

		selectionModel.addSelectionChangedHandler( new SelectionChangedHandler<Library>() {

			@Override
			public void onSelectionChanged ( SelectionChangedEvent<Library> event ) {
				boolean yes = selectionModel.getSelectedItems().size() > 0;
				delLibraryButton.setEnabled( yes );
				scanLibraryButton.setEnabled( yes );
			}
		} );

		ColumnConfig<Library, String> pathColumn = new ColumnConfig<Library, String>( props.path(), 450,
				SafeHtmlUtils.fromTrustedString( "<b>Path</b>" ) );

		final DateTimeFormat dtf = DateTimeFormat.getFormat( PredefinedFormat.DATE_TIME_MEDIUM );
		ColumnConfig<Library, String> lastScannedColumn = new ColumnConfig<Library, String>( new ValueProvider<Library, String>() {

			@Override
			public String getValue ( Library object ) {
				Date lastScanned = props.last().getValue( object );
				if (lastScanned == null) {
					return "Never";
				}
				return dtf.format( lastScanned );
			}


			@Override
			public void setValue ( Library object, String value ) {
				; // we don't do anything.
			}


			@Override
			public String getPath () {
				return props.last().getPath();
			}
		}, 150, SafeHtmlUtils.fromTrustedString( "<b>Last Scanned</b>" ) );

		ColumnConfig<Library, String> browseColumn = new ColumnConfig<Library, String>( props.path(), 50, "" );

		TextButtonCell browseButton = new TextButtonCell() {

			@Override
			public void render ( Context context, String value, SafeHtmlBuilder sb ) {
				value = "...";
				super.render( context, value, sb );
			}

		};
		browseButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				int index = event.getContext().getIndex();
				Library lib = store.get( index );

				handleBrowseButton( lib != null ? lib.getPath() : null );
			}
		} );

		browseColumn.setCell( browseButton );
		browseColumn.setResizable( false );
		browseColumn.setHideable( false );
		browseColumn.setSortable( false );
		browseColumn.setMenuDisabled( true );

		ColumnConfig<Library, String> scanColumn = new ColumnConfig<Library, String>( props.path(), 100, "" );

		TextButtonCell scanButton = new TextButtonCell() {

			@Override
			public void render ( Context context, String value, SafeHtmlBuilder sb ) {
				value = "Scan Now";
				super.render( context, value, sb );
			}

		};
		scanButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				int index = event.getContext().getIndex();
				handleScanButton( store.get( index ) );
			}
		} );

		scanColumn.setCell( scanButton );
		scanColumn.setResizable( false );
		scanColumn.setHideable( false );
		scanColumn.setSortable( false );
		scanColumn.setMenuDisabled( true );

		ColumnConfig<Library, Double> progColumn = new ColumnConfig<Library, Double>( props.progress(), 100, "" );
		progColumn.setColumnTextClassName( "XinosPBC" );

		ProgressBarCell progCell = new ProgressBarCell() {

			@Override
			public void render ( Context context, Double value, SafeHtmlBuilder sb ) {
				if (value >= 0.0d) {
					super.render( context, value, sb );
				}
			}

		};
		progCell.setProgressText( "{0}% complete..." );
		progCell.setWidth( 100 );
		progCell.setIncrement( 100 );

		progColumn.setCell( progCell );
		progColumn.setResizable( false );
		progColumn.setHideable( false );
		progColumn.setSortable( false );
		progColumn.setMenuDisabled( true );

		List<ColumnConfig<Library, ?>> columns = new ArrayList<ColumnConfig<Library, ?>>();

		columns.add( selectionModel.getColumn() );
		columns.add( pathColumn );
		columns.add( browseColumn );
		columns.add( lastScannedColumn );
		columns.add( scanColumn );
		columns.add( progColumn );

		ColumnModel<Library> cm = new ColumnModel<Library>( columns );

		store = new ListStore<Library>( props.key() );

		grid = new Grid<Library>( store, cm );
		grid.setAllowTextSelection( true );
		grid.getView().setAutoExpandColumn( pathColumn );
		grid.getView().setStripeRows( true );
		grid.getView().setStripeRows( true );
		grid.getView().setColumnLines( true );
		grid.setBorders( false );
		grid.setColumnReordering( true );

		grid.setSelectionModel( selectionModel );

		final PathField pathField = new PathField();

		pathField.addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange ( ValueChangeEvent<String> event ) {

				logger.info( "OVC:'" + event.getValue() + "' '" + selectionModel.getSelectedItem() + "'" );

				Library library = selectionModel.getSelectedItem();

				confirmLibraryChange( library.getPath(), event.getValue() );

			}
		} );

		final GridEditing<Library> editing = new GridInlineEditing<Library>( grid );
		editing.addEditor( pathColumn, pathField );

		AsyncCallback<List<Library>> callback = new AsyncCallback<List<Library>>() {

			@Override
			public void onSuccess ( List<Library> result ) {
				// Set the IDs to astronomical values lest conflict shall ensue!
				// Library l = new Library( 2,
				// "C:\\Users\\cmetyko\\Music\\iTunes\\iTunes Media\\Music" );
				// l.setLastScanned();
				// l.setProgress( -1.0d );
				// GWT.log( "Injected fake library: '" + l + "'" );
				// result.add( l );
				// l = new Library( 3,
				// "\\\\aspen\\data1\\music\\iTunes Media\\Music\\Beck" );
				// l.setLastScanned( new Date( System.currentTimeMillis() -
				// 60003000l ) );
				// l.setLastScanned();
				// l.setProgress( -1.0d );
				// GWT.log( "Injected fake library: '" + l + "'" );
				// result.add( l );
				//
				// l = new Library( 4, "C:\\bits" );
				// l.setLastScanned();
				// l.setProgress( -1.0d );
				// result.add( l );

				store.addAll( result );
				logger.info( "added " + result.size() + " rows." );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Whoops; that wasn't supposed to happen!", caught );
			}
		};

		XinosService.core().getLibraries( callback );

	}


	protected void handleBrowseButton ( final String path ) {

		PathDialog dialog = new PathDialog( "Select Library", path );
		dialog.show();
		dialog.center();

		dialog.addPathDialogHideHandler( new PathDialogHideHandler() {

			@Override
			public void onDialogHide ( PathDialogHideEvent event ) {
				if (event.getHideButton() == PredefinedButton.OK)
					confirmLibraryChange( path, event.getPath() );
			}
		} );

	}


	private void confirmLibraryChange ( final String curPath, final String newPath ) {
		// Bail out if there is no change.
		if (curPath.equals( newPath ))
			return;

		ConfirmMessageBox confirm = new ConfirmMessageBox( "Change Library Path",// @formatter:off
				"Are you certain you want to change the path of the library? " +
				"Click \"Yes\" if so, or \"No\" to cancel changes."
				); // @formatter:on

		confirm.addDialogHideHandler( new DialogHideHandler() {

			@Override
			public void onDialogHide ( DialogHideEvent event ) {
				if (event.getHideButton() == PredefinedButton.YES) {
					handleChangeLibrary( curPath, newPath );
				} else {
					store.rejectChanges();
				}
			}
		} );
		confirm.show();

	}


	private void handleChangeLibrary ( String curPath, String newPath ) {
		Library library = null;
		for (Library lib : store.getAll()) {
			if (lib.getPath().equals( curPath )) {
				library = lib;
			}
		}

		if (library == null)
			return;

		library.setPath( newPath );

		final Library finalLibrary = library;

		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			@Override
			public void onSuccess ( Integer result ) {
				if (result.intValue() == 1) {
					store.update( finalLibrary );
					logger.info( "Library updated: '" + finalLibrary + "'" );
				} else {
					Window.alert( "That didn't work." );
				}
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Oh, poop!", caught );
			}
		};

		XinosService.core().updateLibrary( library, callback );
	}


	protected void handleScanButton ( final Library library ) {

		AsyncCallback<ScannerUpdate> callback = new AsyncCallback<ScannerUpdate>() {

			@Override
			public void onSuccess ( ScannerUpdate result ) {
				logger.info( result.toString() );

				rescan( library );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "YUCK!", caught );
			}
		};

		XinosService.core().scanLibrary( library, ScannerControlMessage.START, callback );

	}


	private void rescan ( final Library library ) {
		AsyncCallback<ScannerUpdate> callback = new AsyncCallback<ScannerUpdate>() {

			@Override
			public void onSuccess ( ScannerUpdate result ) {
				logger.info( result.toString() );
				if (result.status != ScannerStatusMessage.FINISHED) {

					library.setScanning( true );
					library.setProgress( (double) result.filesScanned / (double) result.totalFiles );
					store.update( library );

					rescan( library );

				} else {

					library.setScanning( false );
					library.setProgress( -1.0d );
					library.setLastScanned();
					updateLibrary( library );

					DefaultInfoConfig config = new DefaultInfoConfig( "Scan Complete", "The library, " + library.getPath() + "', "
							+ "has been scanned." );
					config.setPosition( InfoPosition.TOP_RIGHT );
					config.setDisplay( 5000 );
					Info.display( config );
				}
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "YUCK and again more YUCK!", caught );
			}
		};

		XinosService.core().scanLibrary( library, ScannerControlMessage.UPDATE, callback );

	}


	private void updateLibrary ( final Library library ) {

		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			@Override
			public void onSuccess ( Integer result ) {
				if (result.intValue() == 1) {
					store.update( library );
				} else {
					Window.alert( "That did not work." );
				}
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Hmph!", caught );
			}
		};

		XinosService.core().updateLibrary( library, callback );

	}


	@Override
	public Widget getHeaderWidget () {
		return header;
	}


	private void setupSub () {

		TextButton addLibraryButton = new TextButton( "New LIbrary" );
		addLibraryButton.setTitle( "Add a new library" );
		addLibraryButton.setWidth( "100%" );
		addLibraryButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleAddLibrary( event );
			}
		} );

		delLibraryButton = new TextButton( "Remove Library" );
		delLibraryButton.setTitle( "Remove selected libraries." );
		delLibraryButton.setEnabled( false );
		delLibraryButton.setWidth( "100%" );
		delLibraryButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleDelLibrary( event );
			}
		} );

		scanLibraryButton = new TextButton( "Scan" );
		scanLibraryButton.setTitle( "Scan selected libraries." );
		scanLibraryButton.setEnabled( false );
		scanLibraryButton.setWidth( "100%" );
		scanLibraryButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleScanLibraries( event );
			}
		} );

		sub = new com.google.gwt.user.client.ui.Grid( 3, 1 );
		sub.setWidth( "100%" );

		sub.setWidget( 0, 0, addLibraryButton );
		sub.setWidget( 1, 0, delLibraryButton );

		sub.setWidget( 2, 0, scanLibraryButton );

	}


	@Override
	public Widget getSubHeaderWidget () {
		if (sub == null) {
			setupSub();
		}

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

	public interface LibraryProperties extends PropertyAccess<Library> {

		@com.google.gwt.editor.client.Editor.Path ( "id" )
		ModelKeyProvider<Library> key ();


		@com.google.gwt.editor.client.Editor.Path ( "path" )
		LabelProvider<Library> pathLabel ();


		ValueProvider<Library, Integer> id ();


		ValueProvider<Library, String> path ();


		ValueProvider<Library, Date> last ();


		ValueProvider<Library, Double> progress ();

	}

	public class PathField extends TextField {

		private Widget sw = null;
		private Widget b;
		private com.google.gwt.user.client.ui.Grid lp = null;
		private TextField tf = null;


		@Override
		public Widget asWidget () {

			if (this.getParent() != null) {
				int ow = this.getParent().getOffsetWidth();
				if (sw != null)
					sw.setWidth( (ow - 31) + "px" );
			}

			if (sw == null) {
				tf = this;

				sw = super.asWidget();
				lp = new com.google.gwt.user.client.ui.Grid( 1, 2 );

				b = new TextButton( "..." ) {

					@Override
					public void onBrowserEvent ( Event event ) {
						super.onBrowserEvent( event );
						if (event.getTypeInt() == Event.ONMOUSEDOWN) {
							handleBrowseButton( tf.getValue() );
						}
					}

				};
				b.setWidth( "25px" );
				b.setTitle( "Browse..." );

				lp.setWidth( "100%" );
				lp.setWidget( 0, 0, sw );
				lp.setWidget( 0, 1, b );

			}

			super.focus();

			return lp.asWidget();
		}

	}

}
