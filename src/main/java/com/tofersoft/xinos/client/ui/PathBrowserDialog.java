package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridSelectionModel;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.shared.dm.Path;


public class PathBrowserDialog extends Dialog implements DialogHideHandler {

	private static final Logger logger = Logger.getLogger( PathBrowserDialog.class.getSimpleName() );

	private TreeGrid<Path> tree;
	private TextBox newFolderTextBox;
	private TreeLoader<Path> loader;
	private GridSelectionModel<Path> selectionModel;
	private TreeStore<Path> store;

	private String childPath = null;
	private List<String> parentPaths = null;
	private boolean loaded = false;

	private final List<BrowserDialogHideHandler> handlers;


	public PathBrowserDialog () {
		this( null );
	}


	public PathBrowserDialog ( String childPath ) {
		super();

		handlers = new ArrayList<PathBrowserDialog.BrowserDialogHideHandler>();
		addDialogHideHandler( this );

		this.childPath = childPath;
		setHideOnButtonClick( true );
		getHeader().setIcon( Images.get().folderOpen() );

		TextButton newFolderButton = new TextButton( "Make New Folder" );
		newFolderButton.addSelectHandler( new SelectHandler() {

			@Override
			public void onSelect ( SelectEvent event ) {
				handleNewFolder();
			}
		} );

		Label helpText = new Label( "Select the library directory to use." );

		setupTree();

		Label nfl = new Label( "Folder:" );
		newFolderTextBox = new TextBox();
		newFolderTextBox.setValue( "tofersoft" );
		newFolderTextBox.setWidth( "90%" );

		LayoutPanel lp = new LayoutPanel();
		lp.add( nfl );
		lp.add( newFolderTextBox );

		lp.setWidgetLeftWidth( nfl, 0.0d, Unit.PX, 4.0d, Unit.EM );
		lp.setWidgetLeftRight( newFolderTextBox, 4.0d, Unit.EM, 0.0d, Unit.PX );

		ScrollPanel sp = new ScrollPanel();

		LayoutPanel pan = new LayoutPanel();
		pan.add( helpText );
		pan.add( sp );
		pan.add( lp );

		pan.setWidgetTopHeight( helpText, 0.0d, Unit.PX, 2.2d, Unit.EM );
		pan.setWidgetTopBottom( sp, 2.2d, Unit.EM, 3.0d, Unit.EM );
		pan.setWidgetBottomHeight( lp, 0.0d, Unit.PX, 3.0d, Unit.EM );

		setPixelSize( 386, 510 );
		setModal( true );
		setBlinkModal( true );
		setHeadingText( "Select Library Directory" );

		setWidget( pan );

		setPredefinedButtons( PredefinedButton.OK, PredefinedButton.CANCEL );

		// addButton( newFolderButton );

		sp.setWidget( tree );

	}


	public String getSelectedPath () {
		return newFolderTextBox.getValue();
	}


	private void setupTree () {

		RpcProxy<Path, List<Path>> proxy = new RpcProxy<Path, List<Path>>() {

			@Override
			public void load ( Path childPath, AsyncCallback<List<Path>> callback ) {
				XinosService.core().getPathList( childPath != null ? childPath.getAbsolutePath() : null, callback );
			}

		};

		loader = new TreeLoader<Path>( proxy ) {

			@Override
			public boolean hasChildren ( Path parent ) {
				return !parent.isEmpty();
			}
		};

		PathProperties props = GWT.create( PathProperties.class );

		store = new TreeStore<Path>( props.key() );

		ChildTreeStoreBinding<Path> ctsb = new ChildTreeStoreBinding<Path>( store ) {

			@Override
			public void onLoad ( LoadEvent<Path, List<Path>> event ) {
				super.onLoad( event );
				handleTreeLoad( event.getLoadConfig(), event.getLoadResult() );
			}

		};

		loader.addLoadHandler( ctsb );

		ColumnConfig<Path, String> col = new ColumnConfig<Path, String>( props.name(), 100, "Name" );

		List<ColumnConfig<Path, ?>> cols = new ArrayList<ColumnConfig<Path, ?>>();
		cols.add( col );

		ColumnModel<Path> cm = new ColumnModel<Path>( cols );

		selectionModel = new TreeGridSelectionModel<Path>() {

			@Override
			protected void onSelectChange ( Path model, boolean select ) {
				super.onSelectChange( model, select );
				if (select) {
					newFolderTextBox.setValue( model.getAbsolutePath() );
				}
			}

		};
		selectionModel.setSelectionMode( SelectionMode.SINGLE );

		tree = new TreeGrid<Path>( store, cm, col );
		tree.setBorders( true );
		tree.setTreeLoader( loader );
		tree.setCaching( true );
		tree.getView().setTrackMouseOver( false );
		tree.getView().setAutoExpandColumn( col );

		tree.getStyle().setJointCloseIcon( Images.get().plus() );
		tree.getStyle().setJointOpenIcon( Images.get().minus() );
		tree.getStyle().setNodeCloseIcon( Images.get().folderClosed() );
		tree.getStyle().setNodeOpenIcon( Images.get().folderOpen() );
		tree.getStyle().setLeafIcon( Images.get().folderClosed() );

		tree.setSelectionModel( selectionModel );

	}


	private void handleTreeLoad ( Path loadConfig, List<Path> result ) {
		if (!loaded) {
			loaded = true;
			logger.info( "Tree loaded (" + loadConfig + ") first time; fetching parentPaths for '" + childPath + "'" );
			fetchParentPaths( childPath );
		} else if (parentPaths != null && !parentPaths.isEmpty()) {
			Path path = store.findModelWithKey( parentPaths.remove( 0 ) );

			logger.info( "Loaded '" + loadConfig + "'; parentPaths is not empty; loading next:'" + path + "'" );
			tree.getTreeView().ensureVisible( path );

			loader.load( path );
		} else {
			Path path = store.findModel( loadConfig );
			tree.getTreeView().ensureVisible( path );
			tree.setExpanded( path, true );
			selectionModel.select( path, true );
		}
	}


	private void fetchParentPaths ( String childPath ) {

		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess ( List<String> result ) {
				logger.info( "Parent Paths are: '" + result.toString() + "'" );
				parentPaths = result;
				String parent = parentPaths.remove( 0 );
				Path path = store.findModelWithKey( parent );

				logger.info( "Loading: '" + path + "'" );
				loader.load( path );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Oh, crap!", caught );
			}
		};

		XinosService.core().getParents( childPath, callback );

	}


	public void addBrowserDialogHideHandler ( BrowserDialogHideHandler handler ) {
		handlers.add( handler );
	}


	@Override
	public void onDialogHide ( DialogHideEvent event ) {
		final BrowserDialogHideEvent newEvent = new BrowserDialogHideEvent( event, newFolderTextBox.getValue() );
		for (BrowserDialogHideHandler handler : handlers)
			handler.onDialogHide( newEvent );
	}


	private void handleNewFolder () {

		if ("1".equals( "1" )) {
			return;
		}

		final Path parent = selectionModel.getSelectedItem();
		final String name = newFolderTextBox.getValue();

		AsyncCallback<String> callback = new AsyncCallback<String>() {

			@Override
			public void onSuccess ( String result ) {
				logger.info( "Refreshing '" + parent + "' to include '" + result + "'" );
				loader.load( parent );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Boom!", caught );
			}
		};

		logger.info( "mkdir('" + parent + "', '" + name + "'" );
		XinosService.core().mkdir( parent.getAbsolutePath(), name, callback );

	}

	public interface PathProperties extends PropertyAccess<Path> {

		@com.google.gwt.editor.client.Editor.Path ( "abs" )
		ModelKeyProvider<Path> key ();


		ValueProvider<Path, String> name ();

	}

	public interface BrowserDialogHideHandler {

		public void onDialogHide ( BrowserDialogHideEvent event );
	}

	public static class BrowserDialogHideEvent extends DialogHideEvent {

		private final DialogHideEvent parentEvent;
		private final String path;


		protected BrowserDialogHideEvent ( DialogHideEvent parentEvent, String path ) {
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
