package com.tofersoft.xinos.server.db;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tofersoft.xinos.server.util.PropMaster;
import com.tofersoft.xinos.shared.dm.Categorical;
import com.tofersoft.xinos.shared.dm.Configurable;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;


public class DataManager {

	private static final Logger logger = LoggerFactory.getLogger( DataManager.class );

	private static final String driver = PropMaster.get( PropMaster.KEY_DB_DRIVER, PropMaster.DEF_DB_DRIVER );
	private static final String dbName = PropMaster.get( PropMaster.KEY_DB_NAME, PropMaster.DEF_DB_NAME );
	private static final String url = PropMaster.get( PropMaster.KEY_DB_URL, PropMaster.DEF_DB_URL );

	private static final String[] DDL = { "TBL_library", "TBL_track", "TBL_configuration" };

	private static final String SQL_FETCH_LIBRARIES = "select id, path, lastScanned, tracks from library";
	private static final String SQL_READ_LIBRARY_BY_ID = SQL_FETCH_LIBRARIES + " where id = ?";
	private static final String SQL_READ_LIBRARY_BY_PATH = SQL_FETCH_LIBRARIES + " where path = ?";
	private static final String SQL_CREATE_LIBRARY = "insert into library (path) values (?)";
	private static final String SQL_UPDATE_LIBRARY_BY_ID = "update library set path = ?, lastScanned = ?, tracks = ? where id = ?";
	private static final String SQL_DELETE_LIBRARY_BY_ID = "delete from library where id = ?";

	private static final String SQL_FETCH_TRACKS = "select id, libraryId, filename, size, millis, format, channels, bitRate, sampleRate, modified, cataloged, album, albumArtist, albumSort, artist, artistSort, comment, compilation, composer, copyright, creationTime, date, discSeq, discsCount, encodedBy, encoder, genre, language, performer, publisher, title, titleSort, trackSeq, tracksCount from track";
	private static final String SQL_READ_TRACK_BY_ID = SQL_FETCH_TRACKS + " where id = ?";
	private static final String SQL_READ_TRACK_BY_FILENAME = SQL_FETCH_TRACKS + " where filename = ?";
	private static final String SQL_CREATE_TRACK = "insert into track (libraryId, filename, size, millis, format, channels, bitRate, sampleRate, modified, cataloged, album, albumArtist, albumSort, artist, artistSort, comment, compilation, composer, copyright, creationTime, date, discSeq, discsCount, encodedBy, encoder, genre, language, performer, publisher, title, titleSort, trackSeq, tracksCount) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE_TRACK_BY_ID = "update track set libraryId = ?, filename = ?,  size = ?,  millis = ?,  format = ?,  channels = ?,  bitRate = ?,  sampleRate = ?,  modified = ?,  cataloged = ?,  album = ?,  albumArtist = ?,  albumSort = ?,  artist = ?,  artistSort = ?,  comment = ?,  compilation = ?,  composer = ?,  copyright = ?,  creationTime = ?,  date = ?,  discSeq = ?,  discsCount = ?,  encodedBy = ?,  encoder = ?,  genre = ?,  language = ?,  performer = ?,  publisher = ?,  title = ?,  titleSort = ?,  trackSeq = ?,  tracksCount = ? where id = ?";
	private static final String SQL_DELETE_TRACK_BY_ID = "delete from track where id = ?";

	private static final String SQL_FETCH_CONFIGURATIONS = "select name, val from configuration";
	private static final String SQL_READ_CONFIGURATION_BY_NAME = SQL_FETCH_CONFIGURATIONS + " where name = ?";
	private static final String SQL_CREATE_CONFIGURATION = "insert into configuration (name, val) values (?, ?)";
	private static final String SQL_UPDATE_CONFIGURATION_BY_NAME = "update configuration set val = ? where name = ?";

	private static DataManager instance = null;

	private PreparedStatement FETCH_LIBRARIES = null;
	private PreparedStatement READ_LIBRARY_BY_ID = null;
	private PreparedStatement READ_LIBRARY_BY_PATH = null;
	private PreparedStatement CREATE_LIBRARY = null;
	private PreparedStatement UPDATE_LIBRARY_BY_ID = null;
	private PreparedStatement DELETE_LIBRARY_BY_ID = null;

	private PreparedStatement FETCH_TRACKS = null;
	private PreparedStatement READ_TRACK_BY_ID = null;
	private PreparedStatement READ_TRACK_BY_FILENAME = null;
	private PreparedStatement CREATE_TRACK = null;
	private PreparedStatement UPDATE_TRACK_BY_ID = null;
	private PreparedStatement DELETE_TRACK_BY_ID = null;

	private PreparedStatement FETCH_CONFIGURATIONS = null;
	private PreparedStatement READ_CONFIGURATION_BY_NAME = null;
	private PreparedStatement CREATE_CONFIGURATION = null;
	private PreparedStatement UPDATE_CONFIGURATION_BY_NAME = null;

	private Properties dbProps = null;
	private Connection conn = null;


	/**
	 * Connects-up to the embedded database. Checks that it has all of the
	 * requisite structures and creates those as necessary; preps all
	 * prepared-statements; and loads all relatively static dimensions (such as
	 * Libraries and Formats).
	 */
	private DataManager () {

		logger.info( "Data Manager is initializing..." );

		// Connect to DB
		connectUp();

		if (conn == null) {
			logger.error( "Fatal errors; cannot proceed." );
			return;
		}

		// Check DB
		checkDB();

		// Setup Prepared Statements
		try {
			FETCH_LIBRARIES = conn.prepareStatement( SQL_FETCH_LIBRARIES );
			READ_LIBRARY_BY_ID = conn.prepareStatement( SQL_READ_LIBRARY_BY_ID );
			READ_LIBRARY_BY_PATH = conn.prepareStatement( SQL_READ_LIBRARY_BY_PATH );
			CREATE_LIBRARY = conn.prepareStatement( SQL_CREATE_LIBRARY );
			UPDATE_LIBRARY_BY_ID = conn.prepareStatement( SQL_UPDATE_LIBRARY_BY_ID );
			DELETE_LIBRARY_BY_ID = conn.prepareStatement( SQL_DELETE_LIBRARY_BY_ID );

			FETCH_TRACKS = conn.prepareStatement( SQL_FETCH_TRACKS );
			READ_TRACK_BY_ID = conn.prepareStatement( SQL_READ_TRACK_BY_ID );

			READ_TRACK_BY_FILENAME = conn.prepareStatement( SQL_READ_TRACK_BY_FILENAME );
			CREATE_TRACK = conn.prepareStatement( SQL_CREATE_TRACK );
			UPDATE_TRACK_BY_ID = conn.prepareStatement( SQL_UPDATE_TRACK_BY_ID );
			DELETE_TRACK_BY_ID = conn.prepareStatement( SQL_DELETE_TRACK_BY_ID );

			FETCH_CONFIGURATIONS = conn.prepareStatement( SQL_FETCH_CONFIGURATIONS );
			READ_CONFIGURATION_BY_NAME = conn.prepareStatement( SQL_READ_CONFIGURATION_BY_NAME );
			CREATE_CONFIGURATION = conn.prepareStatement( SQL_CREATE_CONFIGURATION );
			UPDATE_CONFIGURATION_BY_NAME = conn.prepareStatement( SQL_UPDATE_CONFIGURATION_BY_NAME );

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Preload preloadables
		logger.info( "Loaded " + getLibraries().size() + " libraries." );

	}


	/**
	 * Attempts to connect to the embedded Derby database; and if not, then it
	 * tries to "create" it. Most importantly, this method establishes the
	 * Connection object used generously in the class.
	 */
	private void connectUp () {
		try {
			Class.forName( driver ).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error( "Drivers for the DB Server could not be found.", e );
			return;
		} catch (InstantiationException e) {
			logger.error( "The DB could not be instantiated!", e );
			return;
		} catch (IllegalAccessException e) {
			logger.error( "Illegal Access!", e );
			return;
		}

		dbProps = new Properties();
		conn = null;

		try {

			final String fullUrl = url + PropMaster.get( PropMaster.KEY_DATA_FOLDER, PropMaster.DEF_DATA_FOLDER ) + "/" + dbName;

			conn = DriverManager.getConnection( fullUrl + ";create=false", dbProps );

		} catch (SQLException e) {
			logger.warn( "SQL Exception; perhaps the embedded database does not exist; Let's try..." );
			logger.trace( "The SQL Exception was:", e );
			createDB();
			return;
		}

		logger.info( "Connection established to the embedded database" );

	}


	/**
	 * This method actually creates the embedded database-- it uses the
	 * "full URL" which includes the path to the Xinos Folder, etc.
	 */
	private void createDB () {
		try {
			final String fullUrl = url +

			PropMaster.get( PropMaster.KEY_DATA_FOLDER, PropMaster.DEF_DATA_FOLDER ) + "/" + dbName;

			conn = DriverManager.getConnection( fullUrl + ";create=true", dbProps );

		} catch (SQLException e) {
			logger.error( "Failed to create the embedded database!", e );
			conn = null;
			return;
		}

		logger.info( "The embedded database was created successfully." );
	}


	/**
	 * Uses the Connection to query the Derby System-Schema to see if various
	 * structures exist. IF THEY DON'T then it will attempt to create them
	 * vis-a-vie corresponding SQL files within the classpath, sort'a. Each SQL
	 * file needs to be have single-executable DDL statements. This is achieved
	 * by separating the DDL directives with a token ("--@@@"). It will split
	 * the file by that token and attempt to execute each one in this way.
	 */
	private void checkDB () {

		PreparedStatement ps = null;

		try {

			ps = conn.prepareStatement( "VALUES SYSCS_UTIL.SYSCS_CHECK_TABLE('APP', ?)" );

		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		for (String ddl : DDL) {

			final String[] splits = ddl.split( "_" );
			final String name = splits[1];

			try {

				ps.setString( 1, name.toUpperCase() );

				ResultSet rs = ps.executeQuery();

				if (!rs.next() || rs.getInt( 1 ) != 1) {
					execDDL( ddl );
				} else {
					logger.debug( name + " exists." );
				}
			} catch (SQLSyntaxErrorException e) {
				execDDL( ddl );
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}


	/**
	 * This is the actual method that attempts to invoke execution of a given
	 * file-full-a-DDL that is "delimited" via tokens ("--@@@").
	 *
	 * @param ddl
	 */
	private void execDDL ( String ddl ) {

		logger.info( "Executing DDL for '" + ddl + "'..." );
		String[] ddlStrings = null;

		try {

			InputStream ins = getClass().getClassLoader().getResourceAsStream( "sql/" + ddl + ".sql" );

			if (ins == null) {
				logger.error( "Unable to locate that in the classpath!" );
				throw new IOException( "Null input-stream!" );
			}

			ddlStrings = IOUtils.toString( ins ).split( "--@@@" );

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (ddlStrings == null)
			return;

		for (String ddlString : ddlStrings) {
			try {
				logger.trace( ">>>> " + ddl + " >>>>\n" + ddlString + "\n<<<<<->>>>>" );
				Statement stmt = conn.createStatement();
				stmt.execute( ddlString );
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		logger.info( "...completed for '" + ddl + "'" );

	}


	/**
	 * Shuts-down the embedded database. Please note that Derby throws an
	 * exception whenever the database is shutdown. GOOOOOOOOFY!
	 */
	public void shutdown () {

		logger.info( "Data Manager is shutting down..." );

		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				conn = null;
			}

		try {
			// This will shutdown ALL Derby DBs within the JVM-- which I suppose
			// is just fine.
			DriverManager.getConnection( url + ";shutdown=true" );
		} catch (SQLException e) {
			// Would you believe it? Derby ALWAYS throws an exception upon
			// shutdown-- even when it works just swell!
			// TOO GOOFY!
			;
		}

		logger.info( "The embedded database has been shutdown." );

	}


	/**
	 * Based on the supplied ResultSet object, this method produces a legitimate
	 * Library object.
	 *
	 * @param rs
	 *            A ResultSet object that can be interrogated.
	 * @return A Library object (unless an exception is thrown)
	 * @throws SQLException
	 *             For any reason that one would be-- which is abnormal.
	 */
	private Library libraryFromRS ( ResultSet rs ) throws SQLException {
		return new Library( rs.getInt( 1 ), rs.getString( 2 ), rs.getTimestamp( 3 ), rs.getInt( 4 ) );
	}


	/**
	 * Iterates all Library records in the database and adds them to a List.
	 *
	 * @return A List of Library types. Sorry; it's possible this will return
	 *         all-or-nothing.
	 */
	public List<Library> getLibraries () {

		List<Library> libraries = new ArrayList<Library>();

		try {
			ResultSet rs = FETCH_LIBRARIES.executeQuery();

			while (rs.next()) {
				libraries.add( libraryFromRS( rs ) );
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return libraries;
	}


	/**
	 * Fetches a Library record directly from the Database based on the supplied
	 * Library ID.
	 *
	 * @param id
	 *            The numeric ID of the Library within the database.
	 * @return A Library instance, or NULL if that ID doesn't exist in the
	 *         database.
	 */
	public Library getLibraryById ( int id ) {

		Library library = null;

		try {

			READ_LIBRARY_BY_ID.setInt( 1, id );
			ResultSet rs = READ_LIBRARY_BY_ID.executeQuery();
			if (rs.next())
				library = libraryFromRS( rs );

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return library;

	}


	/**
	 * Attempts to return a Library object, from the Database, based on it's
	 * path. And given that it's Path needs to be unique, then this should
	 * always return something or null (in the case that path doesn't exist).
	 *
	 * @param path
	 *            Needs to be forward-slashed. So, "C:\Users" needs to be
	 *            "C:/Users" please.
	 * @return A/The library object from the Database or NULL if not found.
	 */
	public Library getLibraryByPath ( String path ) {
		Library library = null;

		try {
			READ_LIBRARY_BY_PATH.setString( 1, path );
			ResultSet rs = READ_LIBRARY_BY_PATH.executeQuery();
			if (rs.next())
				library = libraryFromRS( rs );
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return library;
	}


	/**
	 * Updates a Library record in the Database.
	 *
	 * @param library
	 *            The Library object to update (just caring about its path,
	 *            actually).
	 * @return True if the update was successful; false if not (the update needs
	 *         to return a "1").
	 */
	public boolean updateLibrary ( Library library ) {
		boolean worked = false;

		try {
			UPDATE_LIBRARY_BY_ID.clearParameters();

			UPDATE_LIBRARY_BY_ID.setString( 1, library.getPath() );
			UPDATE_LIBRARY_BY_ID.setTimestamp( 2, library.getLastScanned() == null ? null : new Timestamp( library.getLastScanned().getTime() ) );
			UPDATE_LIBRARY_BY_ID.setInt( 3, library.getTracks() );

			UPDATE_LIBRARY_BY_ID.setInt( 4, library.getId() );

			worked = UPDATE_LIBRARY_BY_ID.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return worked;
	}


	/**
	 * Attempts to create a brand-spanking-new library; in so doing, it returns
	 * true AND updates the passed-in library's ID.
	 *
	 * @param library
	 *            The Library object to create
	 * @return False if it fails; True if it works-- and the passed-in Library
	 *         object is updated, too (specifically the ID).
	 */
	public boolean createLibrary ( Library library ) {
		boolean worked = false;

		try {
			String tempPath = "" + System.currentTimeMillis();
			Library tempLibrary = null;

			CREATE_LIBRARY.clearParameters();

			CREATE_LIBRARY.setString( 1, tempPath );

			worked = CREATE_LIBRARY.executeUpdate() == 1;

			if (!worked)
				return false;
			tempLibrary = getLibraryByPath( tempPath );

			if (tempLibrary == null)
				return false;

			library.setId( tempLibrary.getId() );

			worked = updateLibrary( library );

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return worked;
	}


	/**
	 * Convenience method that checks whether or not the library-record exists.
	 * If so, then it is updated; if it does not exist then it is created. The
	 * passed-in library object will get an update ID if it didn't exist.
	 *
	 * @param library
	 *            The library to update-or-create
	 * @return False if there was any failure; True otherwise-- and the ID in
	 *         the passed-in Library may be updated as well.
	 */
	public boolean saveLibrary ( Library library ) {
		Library tempLibrary = getLibraryById( library.getId() );

		if (tempLibrary == null) {
			return createLibrary( library );
		} else {
			return updateLibrary( library );
		}
	}


	/**
	 * Attempts to delete the given library (by ID); this cascades into the
	 * Library's static understanding of libraries for that matter.
	 *
	 * @param library
	 *            The library to obliterate from the database, forever, if it
	 *            exists (bare in mind dependencies)
	 * @return True if this works like it does on paper; False for any other
	 *         reason.
	 */
	public boolean deleteLibrary ( Library library ) {

		boolean worked = false;
		try {
			DELETE_LIBRARY_BY_ID.setInt( 1, library.getId() );

			worked = DELETE_LIBRARY_BY_ID.executeUpdate() == 1;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (worked)
			Library.removeLibrary( library );

		return worked;
	}


	/**
	 * Simply interrogates a ResultSet in order to create a Track object.
	 *
	 * @param rs
	 *            A ResultSet ready to be interrogated.
	 * @return A Track object if all went well; or, null, or most likely an
	 *         exception.
	 * @throws SQLException
	 *             Cause that happens (tm).
	 */
	public Track trackFromRS ( ResultSet rs ) throws SQLException {
		Track track = new Track();

		// id:1 libraryId:2 filename:3 size:4 millis:5 format:6 channels:7
		// bitRate:8 sampleRate:9 modified:10 cataloged:11 album:12
		// albumArtist:13 albumSort:14 artist:15 artistSort:16 comment:17
		// compilation:18 composer:19 copyright:20 creationTime:21 date:22
		// discSeq:23 discsCount:24 encodedBy:25 encoder:26 genre:27 language:28
		// performer:29 publisher:30 title:31 titleSort:32 trackSeq:33
		// tracksCount:34

		track.setId( rs.getInt( 1 ) ).setLibrary( Library.getLibraryForId( rs.getInt( 2 ) ) ).setFilename( rs.getString( 3 ) )
				.setSize( rs.getLong( 4 ) ).setMillis( rs.getLong( 5 ) ).setFormat( rs.getString( 6 ) ).setChannels( rs.getInt( 7 ) )
				.setBitRate( rs.getInt( 8 ) ).setSampleRate( rs.getInt( 9 ) ).setModified( rs.getTimestamp( 10 ) )
				.setCataloged( rs.getTimestamp( 11 ) ).setAlbum( rs.getString( 12 ) ).setAlbumArtist( rs.getString( 13 ) )
				.setAlbumSort( rs.getString( 14 ) ).setArtist( rs.getString( 15 ) ).setArtistSort( rs.getString( 16 ) )
				.setComment( rs.getString( 17 ) ).setCompilation( rs.getBoolean( 18 ) ).setComposer( rs.getString( 19 ) )
				.setCopyright( rs.getString( 20 ) ).setCreationTime( rs.getTimestamp( 21 ) ).setDate( rs.getTimestamp( 22 ) )
				.setDiscSeq( rs.getInt( 23 ) ).setDiscsCount( rs.getInt( 24 ) ).setEncodedBy( rs.getString( 25 ) ).setEncoder( rs.getString( 26 ) )
				.setGenre( rs.getString( 27 ) ).setLanguage( rs.getString( 28 ) ).setPerformer( rs.getString( 29 ) )
				.setPublisher( rs.getString( 30 ) ).setTitle( rs.getString( 31 ) ).setTitleSort( rs.getString( 32 ) ).setTrackSeq( rs.getInt( 33 ) )
				.setTracksCount( rs.getInt( 34 ) );

		return track;
	}


	/**
	 * Attempts to instantiate a Track object directly from the Database.
	 *
	 * @param id
	 *            The ID of the Track as it is in the Database.
	 * @return The fully hydrated Track if it was found; or NULL if not (or some
	 *         SQL exception).
	 */
	public Track getTrackById ( int id ) {
		Track track = null;

		try {
			READ_TRACK_BY_ID.setInt( 1, id );
			ResultSet rs = READ_TRACK_BY_ID.executeQuery();
			if (rs.next())
				track = trackFromRS( rs );
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return track;
	}


	/**
	 * Generates a list of ALL TRACKS. DONT DO THIS.
	 *
	 * @return shit.
	 */
	public List<Track> getAllTracks () {
		List<Track> tracks = new ArrayList<Track>();

		try {
			ResultSet rs = FETCH_TRACKS.executeQuery();

			while (rs.next())
				tracks.add( trackFromRS( rs ) );

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tracks;
	}


	/**
	 * Queries the Database for (exactly) matching track-filenames and returns
	 * the first of such it finds (bare in mind that filename needs to be unique
	 * at any rate).
	 *
	 * @param filename
	 *            The corrected filename (such as
	 *            "Nine Inch Nails/Hesitation Marks/Copy Of A.m4a"). Hmm. This
	 *            is LIBRARY NON-SPECIFIC.
	 * @return The track if it finds one; or NULL of not.
	 */
	public Track getTrackByFilename ( String filename ) {
		Track track = null;

		try {
			READ_TRACK_BY_FILENAME.setString( 1, filename );
			ResultSet rs = READ_TRACK_BY_FILENAME.executeQuery();
			if (rs.next())
				track = trackFromRS( rs );
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return track;
	}


	/**
	 *
	 * @param track
	 * @return
	 */
	public boolean updateTrack ( Track track ) {

		boolean worked = false;

		try {

			final Timestamp modified = track.getModified() == null ? null : new Timestamp( track.getModified().getTime() );
			final Timestamp cataloged = track.getCataloged() == null ? null : new Timestamp( track.getCataloged().getTime() );
			final Timestamp creationTime = track.getCreationTime() == null ? null : new Timestamp( track.getCreationTime().getTime() );
			final Timestamp date = track.getDate() == null ? null : new Timestamp( track.getDate().getTime() );

			UPDATE_TRACK_BY_ID.setInt( 1, track.getLibrary().getId() );
			UPDATE_TRACK_BY_ID.setString( 2, track.getFilename() );
			UPDATE_TRACK_BY_ID.setLong( 3, track.getSize() );
			UPDATE_TRACK_BY_ID.setLong( 4, track.getMillis() );
			UPDATE_TRACK_BY_ID.setString( 5, track.getFormat() );
			UPDATE_TRACK_BY_ID.setInt( 6, track.getChannels() );
			UPDATE_TRACK_BY_ID.setInt( 7, track.getBitRate() );
			UPDATE_TRACK_BY_ID.setInt( 8, track.getSampleRate() );
			UPDATE_TRACK_BY_ID.setTimestamp( 9, modified );
			UPDATE_TRACK_BY_ID.setTimestamp( 10, cataloged );
			UPDATE_TRACK_BY_ID.setString( 11, track.getAlbum() );
			UPDATE_TRACK_BY_ID.setString( 12, track.getAlbumArtist() );
			UPDATE_TRACK_BY_ID.setString( 13, track.getAlbumSort() );
			UPDATE_TRACK_BY_ID.setString( 14, track.getArtist() );
			UPDATE_TRACK_BY_ID.setString( 15, track.getArtistSort() );
			UPDATE_TRACK_BY_ID.setString( 16, track.getComment() );
			UPDATE_TRACK_BY_ID.setBoolean( 17, track.getCompilation() != null ? track.getCompilation() : false );
			UPDATE_TRACK_BY_ID.setString( 18, track.getComposer() );
			UPDATE_TRACK_BY_ID.setString( 19, track.getCopyright() );
			UPDATE_TRACK_BY_ID.setTimestamp( 20, creationTime );
			UPDATE_TRACK_BY_ID.setTimestamp( 21, date );
			UPDATE_TRACK_BY_ID.setInt( 22, track.getDiscSeq() );
			UPDATE_TRACK_BY_ID.setInt( 23, track.getDiscsCount() );
			UPDATE_TRACK_BY_ID.setString( 24, track.getEncodedBy() );
			UPDATE_TRACK_BY_ID.setString( 25, track.getEncoder() );
			UPDATE_TRACK_BY_ID.setString( 26, track.getGenre() );
			UPDATE_TRACK_BY_ID.setString( 27, track.getLanguage() );
			UPDATE_TRACK_BY_ID.setString( 28, track.getPerformer() );
			UPDATE_TRACK_BY_ID.setString( 29, track.getPublisher() );
			UPDATE_TRACK_BY_ID.setString( 30, track.getTitle() );
			UPDATE_TRACK_BY_ID.setString( 31, track.getTitleSort() );
			UPDATE_TRACK_BY_ID.setInt( 32, track.getTrackSeq() );
			UPDATE_TRACK_BY_ID.setInt( 33, track.getTracksCount() );

			UPDATE_TRACK_BY_ID.setInt( 34, track.getId() );

			final int rc = UPDATE_TRACK_BY_ID.executeUpdate();

			worked = rc == 1;

			if (!worked)
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return worked;

	}


	public boolean createTrack ( Track track ) {
		boolean worked = false;

		try {
			final String tempFilename = System.currentTimeMillis() + track.getFilename();

			final Timestamp modified = track.getModified() == null ? null : new Timestamp( track.getModified().getTime() );
			final Timestamp cataloged = track.getCataloged() == null ? null : new Timestamp( track.getCataloged().getTime() );
			final Timestamp creationTime = track.getCreationTime() == null ? null : new Timestamp( track.getCreationTime().getTime() );
			final Timestamp date = track.getDate() == null ? null : new Timestamp( track.getDate().getTime() );

			CREATE_TRACK.setInt( 1, track.getLibrary().getId() );
			CREATE_TRACK.setString( 2, tempFilename );
			CREATE_TRACK.setLong( 3, track.getSize() );
			CREATE_TRACK.setLong( 4, track.getMillis() );
			CREATE_TRACK.setString( 5, track.getFormat() );
			CREATE_TRACK.setInt( 6, track.getChannels() );
			CREATE_TRACK.setInt( 7, track.getBitRate() );
			CREATE_TRACK.setInt( 8, track.getSampleRate() );
			CREATE_TRACK.setTimestamp( 9, modified );
			CREATE_TRACK.setTimestamp( 10, cataloged );
			CREATE_TRACK.setString( 11, track.getAlbum() );
			CREATE_TRACK.setString( 12, track.getAlbumArtist() );
			CREATE_TRACK.setString( 13, track.getAlbumSort() );
			CREATE_TRACK.setString( 14, track.getArtist() );
			CREATE_TRACK.setString( 15, track.getArtistSort() );
			CREATE_TRACK.setString( 16, track.getComment() );
			CREATE_TRACK.setBoolean( 17, track.getCompilation() != null ? track.getCompilation() : false );
			CREATE_TRACK.setString( 18, track.getComposer() );
			CREATE_TRACK.setString( 19, track.getCopyright() );
			CREATE_TRACK.setTimestamp( 20, creationTime );
			CREATE_TRACK.setTimestamp( 21, date );
			CREATE_TRACK.setInt( 22, track.getDiscSeq() );
			CREATE_TRACK.setInt( 23, track.getDiscsCount() );
			CREATE_TRACK.setString( 24, track.getEncodedBy() );
			CREATE_TRACK.setString( 25, track.getEncoder() );
			CREATE_TRACK.setString( 26, track.getGenre() );
			CREATE_TRACK.setString( 27, track.getLanguage() );
			CREATE_TRACK.setString( 28, track.getPerformer() );
			CREATE_TRACK.setString( 29, track.getPublisher() );
			CREATE_TRACK.setString( 30, track.getTitle() );
			CREATE_TRACK.setString( 31, track.getTitleSort() );
			CREATE_TRACK.setInt( 32, track.getTrackSeq() );
			CREATE_TRACK.setInt( 33, track.getTracksCount() );

			worked = CREATE_TRACK.executeUpdate() == 1;

			if (!worked)
				return false;

			Track tempTrack = getTrackByFilename( tempFilename );

			if (tempTrack == null)
				return false;

			track.setId( tempTrack.getId() );

			worked = updateTrack( track );

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return worked;
	}


	public boolean saveTrack ( Track track ) {

		Track tempTrack = track.getId() > 0 ? getTrackById( track.getId() ) : getTrackByFilename( track.getFilename() );

		if (tempTrack == null) {
			return createTrack( track );
		} else {
			track.setId( tempTrack.getId() );
			return updateTrack( track );
		}

	}


	/**
	 * It deletes the track, right?
	 *
	 * @param track
	 *            Principally only the Track.ID is used.
	 * @return True if it appeared to succeed and Flase for almost any other
	 *         reason.
	 */
	public boolean deleteTrack ( Track track ) {
		boolean worked = false;

		try {
			DELETE_TRACK_BY_ID.setInt( 1, track.getId() );
			worked = DELETE_TRACK_BY_ID.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return worked;
	}


	/**
	 * Method provides other objects' access to the DataManager's Singleton
	 * instance.
	 *
	 * @return The Singleton DataManager instance which will be instantiated if
	 *         it doesn't exist when first accessed.
	 */
	public static DataManager get () {

		if (instance == null)
			instance = new DataManager();

		return instance;
	}


	public List<String> getCategoricalData ( Categorical categorical ) {
		final List<String> categoricalData = new ArrayList<String>();

		logger.info( "Getting categorical data for: '" + categorical + "'" );

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = null;

			switch (categorical) {
			case ALBUM:
				break;
			case ALBUM_ARTIST:
				break;
			case ALL:
				break;
			case ARTIST:
				rs = stmt.executeQuery( "select distinct artist from track order by artist" );
				break;
			case CHANNELS:
				break;
			case COMPOSER:
				break;
			case DURATION:
				break;
			case GENRE:
				rs = stmt.executeQuery( "select distinct genre from track order by genre" );
				break;
			case KIND:
				break;
			case MODIFIED:
				break;
			case SAMPLE_RATE:
				break;
			case SIZE:
				break;
			case YEAR:
				break;
			default:
				break;

			}

			while (rs.next()) {
				categoricalData.add( rs.getString( 1 ) );
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		categoricalData.add( 0, "All (" + categoricalData.size() + " " + categorical.name + "s)" );
		return categoricalData;
	}


	public List<String> queryForStrings ( List<Query> queryList ) {
		final List<String> strings = new ArrayList<String>();

		// @formatter:off
		// alpha [(GENRE,null)]
		// beta  [(GENRE,('Trance','Soundtrack')),(ARTIST,null)]
		// gamma [(GENRE,('Trance','Soundtrack')),(ARTIST,('Moby'),(ALBUM,null)]
		// @formatter:on

		final Query lastQuery = queryList.get( queryList.size() - 1 );

		StringBuilder sql = new StringBuilder( "select distinct " ).append( lastQuery.getCategorical().tag ).append( " from track" );

		if (queryList.size() > 1) {
			sql.append( " where " );
			for (int i = 0; i < queryList.size() - 1; i++) {
				Query query = queryList.get( i );
				sql.append( i > 0 ? " and " : "" ).append( "(" ).append( query.getCategorical().tag ).append( " in (" );

				boolean first = true;
				for (String s : query.getValues()) {
					sql.append( !first ? "," : "" );
					if (s.startsWith( "All (" )) {
						sql.append( "true" );
					} else {
						sql.append( "'" ).append( s ).append( "'" );
					}
					first = false;
				}
				sql.append( "))" );
			}
		}

		sql.append( " order by " ).append( lastQuery.getCategorical().tag );

		logger.info( sql.toString() );

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql.toString() );

			while (rs.next()) {
				strings.add( rs.getString( 1 ) );
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			rs = null;
			stmt = null;
		}

		return strings;
	}


	public List<Track> queryForTracks ( List<Query> queryList ) {
		List<Track> tracks = new ArrayList<Track>();

		// @formatter:off
		// (GENRE,['Trance','Soundtrack']),(ARTIST,['Moby']),(ALBUM,['All (4...'])
		// select * from taggedTracks where
		// ( genre in ('Trance', 'Soundtrack') )
		// and ( artist in ('Moby') )
		// and ( album in (true) )

		// alpha [(GENRE,null)]
		// beta  [(GENRE,('Trance','Soundtrack')),(ARTIST,null)]
		// gamma [(GENRE,('Trance','Soundtrack')),(ARTIST,('Moby'),(ALBUM,null)]
		// delta [(GENRE,('Trance','Soundtrack')),(ARTIST,('Moby'),(ALBUM,('All '))]
		// @formatter:on

		StringBuilder sql = new StringBuilder( "select * from track where" );

		for (int i = 0; i < queryList.size(); i++) {
			Query query = queryList.get( i );
			sql.append( i > 0 ? " and (" : " (" ).append( query.getCategorical().tag ).append( " in (" );

			boolean first = true;
			if (query.getValues() != null) {
				for (String s : query.getValues()) {
					sql.append( !first ? "," : "" );

					if (s.startsWith( "All (" )) {
						sql.append( "true" );
					} else {
						sql.append( "'" ).append( s ).append( "'" );
					}

					first = false;
				}

				sql.append( ")" );
			}

			sql.append( ")" );
		}

		logger.info( sql.toString() );

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql.toString() );

			while (rs.next()) {
				tracks.add( trackFromRS( rs ) );
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			rs = null;
			stmt = null;
		}

		return tracks;
	}


	private Configurable configurableFromRS ( ResultSet rs ) throws SQLException {
		return new Configurable().setName( rs.getString( 1 ) ).setValue( rs.getString( 2 ) );
	}


	public List<Configurable> getConfigurables () {
		List<Configurable> confs = new ArrayList<Configurable>();
		ResultSet rs = null;
		try {
			rs = FETCH_CONFIGURATIONS.executeQuery();
			while (rs.next()) {
				confs.add( configurableFromRS( rs ) );
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					rs = null;
				}
			}
		}

		return confs;
	}


	public Configurable getConfigurableByName ( String name, String def ) {
		Configurable conf = getConfigurableByName( name );

		if (conf != null) {
			return conf;
		}

		conf = new Configurable().setName( name ).setValue( def );
		addConfigurable( conf );
		return conf;

	}


	public Configurable getConfigurableByName ( String name ) {
		Configurable conf = null;
		ResultSet rs = null;
		try {

			READ_CONFIGURATION_BY_NAME.setString( 1, name );
			rs = READ_CONFIGURATION_BY_NAME.executeQuery();

			if (rs.next()) {
				conf = configurableFromRS( rs );
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					rs = null;
				}
			}
		}

		return conf;
	}


	public int addConfigurable ( Configurable conf ) {

		try {
			CREATE_CONFIGURATION.setString( 1, conf.getName() );
			CREATE_CONFIGURATION.setString( 2, conf.getValue() );

			return CREATE_CONFIGURATION.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}


	public int updateConfigurable ( Configurable conf ) {
		try {
			UPDATE_CONFIGURATION_BY_NAME.setString( 1, conf.getValue() );
			UPDATE_CONFIGURATION_BY_NAME.setString( 2, conf.getName() );

			return UPDATE_CONFIGURATION_BY_NAME.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;

	}


	public int saveConfigurable ( Configurable conf ) {
		if (getConfigurableByName( conf.getName() ) == null) {
			return addConfigurable( conf );
		} else {
			return updateConfigurable( conf );
		}
	}

}
