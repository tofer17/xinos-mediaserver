package com.tofersoft.xinos.server;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tofersoft.xinos.client.rpc.CoreService;
import com.tofersoft.xinos.server.db.DataManager;
import com.tofersoft.xinos.server.util.PropMaster;
import com.tofersoft.xinos.shared.dm.Categorical;
import com.tofersoft.xinos.shared.dm.Configurable;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.dm.Path;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;
import com.tofersoft.xinos.shared.scanner.ScannerControlMessage;
import com.tofersoft.xinos.shared.scanner.ScannerUpdate;


public class XinosCore extends RemoteServiceServlet implements CoreService {

	private static final long serialVersionUID = 9159974073950040113L;

	private static final Logger logger = LoggerFactory.getLogger( XinosCore.class );

	private static FileFilter folderFilter = new FileFilter() {

		@Override
		public boolean accept ( File pathname ) {
			return pathname.isDirectory() && !pathname.getName().startsWith( "." );
		}
	};

	public static final FileSystemView fsv = FileSystemView.getFileSystemView();

	private static XinosCore instance = null;

	private DataManager dm = null;


	@Override
	public void init () throws ServletException {
		super.init();

		logger.info( "Xinos Core Server is initializing" );

		PropMaster.reload();

		if (!checkFolder()) {
			logger.error( "Aborting!" );
			throw new ServletException( "Unable to access the Xinos Data Folder!" );
		}

		dm = DataManager.get();

		if (dm == null) {
			throw new ServletException( "Unable to instantiate the Xinos Data Manager!" );
		}

		// Check configs
		dm.getConfigurableByName( "ffmpeg.bin", "ffmpeg" );

		logger.info( "Xinos Core Server has initialized." );

		instance = this;

		// logger.info( "Property xinos.test='{}'",
		// getProperty("xinos.test") );
		// logger.info( "Environment xinos.test='{}'", System.getenv(
		// "xinos.test" ) );

		logger.info( "{}='{}'", PropMaster.KEY_DATA_FOLDER, PropMaster.get( PropMaster.KEY_DATA_FOLDER, null ) );

	}


	private boolean checkFolder () {

		File dataFolder = new File( PropMaster.get( PropMaster.KEY_DATA_FOLDER, PropMaster.DEF_DATA_FOLDER ) );

		if (!dataFolder.exists()) {
			logger.info( "The Xinos Data Folder, '" + dataFolder.getAbsolutePath() + "', does not exist; creating..." );
			if (!dataFolder.mkdir()) {
				logger.error( "...unable to create the Xinos Data Folder!" );
				return false;
			} else {
				logger.info( "Ok; the Xinos Data Folder was created." );
			}
		}

		return true;
	}


	public static final synchronized int saveTrack ( Track track ) {

		if (instance.dm.saveTrack( track )) {
			return 1;
		} else {
			return -1;
		}

	}


	public static final XinosCore getInstance () {
		return instance;
	}


	@Override
	public void destroy () {
		super.destroy();

		logger.info( "Xinos Core Server is shutting down..." );

		dm.shutdown();

		PathScanner.shutdown();

		logger.info( "Xinos Core Server has shutdown." );
	}


	@Override
	public List<Library> getLibraries () {
		try {

			logger.debug( "Fetching libraries." );

			List<Library> libraries = dm.getLibraries();

			logger.debug( "Returning {} libraries.", libraries.size() );

			return libraries;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}


	@Override
	public List<Path> getPathList ( String child ) {

		File[] folders = null;

		if (child == null) {

			folders = File.listRoots();

			logger.debug( "Was null, used /, got " + folders.length );

		} else {

			File f = new File( child );

			try {
				folders = f.listFiles( folderFilter );
			} catch (Throwable t) {
				t.printStackTrace();
			}

			if (folders == null)
				folders = new File[0];
			logger.debug( "Wasn't null: '" + child + "'; returning " + folders.length );
		}

		List<Path> paths = new ArrayList<Path>( folders.length );

		for (File f : folders) {

			String name = f.getName();
			String abs = f.getAbsolutePath();

			if (name.length() < 1)
				name = abs;

			Path p = new Path( name, abs );

			try {
				final String[] contents = f.list();
				p.setEmpty( contents == null || contents.length == 0 );
			} catch (Throwable t) {
				p.setEmpty( true );
			}

			paths.add( p );
		}

		return paths;
	}


	@Override
	public List<String> getParents ( String childPath ) {
		// Given something like /a/b/c/d
		// return a list of "/, /a, /a/b, /a/b/c, /a/b/c/d"

		List<String> parents = new ArrayList<String>();

		if (childPath == null)
			childPath = System.getProperty( "user.home" );

		File f = new File( childPath );
		parents.add( f.getAbsolutePath() );

		while (f.getParentFile() != null) {
			parents.add( 0, f.getParentFile().getAbsolutePath() );
			f = f.getParentFile();
		}

		logger.info( "getParents of '" + childPath + "' -> " + parents );

		return parents;
	}


	/**
	 * TODO: Needs to return better detail to the uses as to why it didn't work.
	 * This should be a wrapper and, for that matter, it may as well be a nested
	 * class of Path.
	 */
	@Override
	public String mkdir ( String parent, String name ) {

		if (parent == null || parent.length() < 1 || name == null || name.length() < 1) {
			logger.error( "Either the parent-path, new path, or both were invalid." );
			return null;
		}

		File parentFolder = new File( parent );
		if (!parentFolder.isDirectory()) {
			logger.error( "The parent-folder is not a folder." );
			return null;
		}

		logger.info( parentFolder.getAbsolutePath() + " " + name );

		File newFolder = new File( parentFolder, name );
		if (newFolder.exists()) {
			logger.error( "The new-folder (weather it is a folder or not) already exists." );
			return null;
		}

		final boolean worked = newFolder.mkdir();

		// Path newPath = new Path( newFolder.getName(),
		// newFolder.getAbsolutePath() );

		logger.info( "Worked: " + (worked ? "yes" : "NO") + " '" + newFolder.getAbsolutePath() + "'" );

		return newFolder.getAbsolutePath();
	}


	@Override
	public ScannerUpdate scanLibrary ( Library library, ScannerControlMessage scm ) {

		logger.trace( "prx path:" + library + " scm: " + scm );

		return PathScanner.scan( library, scm );
	}


	@Override
	public String fetchUserHome () {
		return System.getProperty( "user.home" );
	}


	@Override
	public int updateLibrary ( Library library ) {
		logger.info( "Request to update library: '" + library + "'" );
		return dm.updateLibrary( library ) ? 1 : -1;
	}


	@Override
	public int removeLibrary ( Library library ) {
		logger.info( "Request to remove library: '" + library + "'" );

		List<Library> libraryList = getLibraries();

		if (libraryList.size() < 2) {
			return -1;
		}

		return dm.deleteLibrary( library ) ? 1 : -1;
	}


	@Override
	public Library addLibrary ( String path ) {
		logger.info( "Request to add new library: '" + path + "'" );

		Library library = new Library( -1, path );

		dm.createLibrary( library );

		return library;
	}


	@Override
	public List<String> getCategoricalData ( Categorical categorical ) {
		return dm.getCategoricalData( categorical );
	}


	@Override
	public List<Track> queryForTracks ( List<Query> queryList ) {
		List<Track> tracks = dm.queryForTracks( queryList );

		logger.info( "queryForTracks returning " + tracks.size() + " results." );

		return tracks;
	}


	@Override
	public List<String> queryForStrings ( List<Query> queryList ) {
		List<String> queryForStrings = dm.queryForStrings( queryList );

		logger.info( "queryForStrings returning " + queryForStrings.size() + " results." );

		return queryForStrings;
	}


	@Override
	public List<Track> getTracks ( List<Integer> trackIds ) {
		List<Track> tracks = new ArrayList<Track>( trackIds.size() );

		for (Integer trackId : trackIds) {
			Track track = dm.getTrackById( trackId );
			if (track != null) {
				tracks.add( track );
			}
		}

		return tracks;
	}


	@Override
	public List<Configurable> getConfiguration ( int area ) {
		List<Configurable> confs = new ArrayList<Configurable>();

		logger.info( "Confs for area {} requestd.", area );
		switch (area) {
		case 0: // Me
			confs.add( dm.getConfigurableByName( "ffmpeg.bin", "ffmpeg" ).setArea( 0 ).setLabel( "Path to FFMPEG" ) );
			confs.add( dm.getConfigurableByName( "streamer.stale.age", "3600000" ).setArea( 0 ).setLabel( "Temp file stale age (ms)" ) );
			break;
		case 1: // You
			confs.add( dm.getConfigurableByName( "transcoder.default.codec", "OGG" ).setArea( 1 ).setLabel( "Transcoder Codec" ) );
			confs.add( dm.getConfigurableByName( "transcoder.default.bitrate", "128" ).setArea( 1 ).setLabel( "Transcoder Bitrate" ) );
		}

		logger.info( "Returning {} confs...", confs.size() );
		return confs;
	}


	@Override
	public int setConfiguration ( Configurable conf ) {
		logger.info( "Setting configurable '{}' to '{} ({})'", conf.getName(), conf.getValue(), conf.getType() );
		int r = dm.saveConfigurable( conf );
		logger.info( "r={}", r );

		return r;
	}

}
