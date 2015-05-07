package com.tofersoft.xinos.server.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropMaster {

	private static final Logger logger = LoggerFactory.getLogger( PropMaster.class );

	public static final String KEY_DATA_FOLDER = "xinos.data.folder";
	public static final String DEF_DATA_FOLDER = System.getProperty( "user.home" ) + File.separator + "Xinos";

	public static final String KEY_DB_DRIVER = "xinos.db.driver";
	public static final String DEF_DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	public static final String KEY_DB_NAME = "xinos.db.name";
	public static final String DEF_DB_NAME = "XinosData";

	public static final String KEY_DB_URL = "xinos.db.url";
	public static final String DEF_DB_URL = "jdbc:derby:";

	public static final String KEY_DB_USER = "xinos.db.user";
	public static final String DEF_DB_USER = "";

	public static final String KEY_DB_PASS = "xinos.db.pass";
	public static final String DEF_DB_PASS = "";

	private static final Properties props = new Properties();

	static {
		put( KEY_DATA_FOLDER, DEF_DATA_FOLDER );

		put( KEY_DB_DRIVER, DEF_DB_DRIVER );
		put( KEY_DB_NAME, DEF_DB_NAME );
		put( KEY_DB_URL, DEF_DB_URL );
	}


	public static final void reload () {
		reload( get( KEY_DATA_FOLDER, DEF_DATA_FOLDER ) );
	}


	/**
	 * Attempts to reload properties; essentially, this will look for the
	 * data-store.properties file located in the xinos.data.folder property. If
	 * the value is passed in as an argument to Java, then that value will be
	 * used; if there is an environmental variable then that would be used; and
	 * finally, if neither, then the default is used: ${user.home}/Xinos. If the
	 * data-store.properties file is not found, then it will be created using
	 * defaults; which may be supplied via argument or environment. If the
	 * data-store.properties exists, it is read and then it's properties are
	 * interrogated against any that are supplied as arguments or environment.
	 * This file is then saved.
	 */
	public static synchronized final void reload ( String dataFolder ) {
		logger.info( "Reloading from dataFolder: '{}'...", dataFolder );
		synchronized (props) {

			boolean dirty = true;
			File propsFile = new File( dataFolder, "data-store.properties" );
			Properties p = new Properties();

			if (propsFile.canRead()) {
				InputStream in = null;
				try {
					in = new FileInputStream( propsFile );
					p.load( in );
					logger.info( "Properties were loaded from '{}'", propsFile.getAbsolutePath() );
					dirty = false;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					dirty = true;
				} catch (IOException e) {
					e.printStackTrace();
					dirty = true;
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							in = null;
						}
					}
				}
			} else {
				dirty = false;
				logger.info( "Cannot read data-store.properties: '{}' (this is ok )", propsFile.getAbsolutePath() );
			}

			if (!dirty) {
				props.clear();

				props.put( KEY_DATA_FOLDER, dataFolder );

				props.put( KEY_DB_DRIVER, getAorE( KEY_DB_DRIVER, p.getProperty( KEY_DB_DRIVER, DEF_DB_DRIVER ) ) );
				props.put( KEY_DB_NAME, getAorE( KEY_DB_NAME, p.getProperty( KEY_DB_NAME, DEF_DB_NAME ) ) );
				props.put( KEY_DB_URL, getAorE( KEY_DB_URL, p.getProperty( KEY_DB_URL, DEF_DB_URL ) ) );
				props.put( KEY_DB_USER, getAorE( KEY_DB_USER, p.getProperty( KEY_DB_USER, DEF_DB_USER ) ) );
				props.put( KEY_DB_PASS, getAorE( KEY_DB_PASS, p.getProperty( KEY_DB_PASS, DEF_DB_PASS ) ) );
			}

			OutputStream out = null;
			try {
				logger.info( "Saving to '{}'", propsFile.getAbsolutePath() );
				out = new FileOutputStream( propsFile );
				props.store( out, null );
				logger.info( "Properties were saved to '{}'", propsFile.getAbsolutePath() );
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						out = null;
					}
				}
			}
		}
	}


	public static final String put ( String key, String val ) {
		String pre = props.getProperty( key );
		props.setProperty( key, val );
		return pre;
	}


	/**
	 * Get (A)rgument or (E)nvironment...
	 *
	 * @param key
	 * @param def
	 * @return
	 */
	private static final String getAorE ( String key, String def ) {
		if (System.getProperty( key, null ) != null) {
			return System.getProperty( key );
		} else if (System.getenv( key ) != null) {
			return System.getenv( key );
		} else {
			return def;
		}
	}


	public static final String get ( String key, String def ) {
		String val = getAorE( key, props.getProperty( key ) );
		return val != null ? val : def;
	}


	public static final String get ( String key ) {
		return get( key, null );
	}
}
