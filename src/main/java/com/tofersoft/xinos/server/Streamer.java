package com.tofersoft.xinos.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.shared.GWT;
import com.tofersoft.xinos.server.db.DataManager;
import com.tofersoft.xinos.server.util.PropMaster;
import com.tofersoft.xinos.shared.dm.Track;


/**
 * Adaptations from
 * https://github.com/artclarke/xuggle-xuggler/blob/master/src/com
 * /xuggle/xuggler/Converter.java
 *
 * @author cmetyko
 *
 */
public class Streamer extends HttpServlet {

	private static final long serialVersionUID = 5614885277254601969L;

	private static final Logger logger = LoggerFactory.getLogger( Streamer.class );

	private static final Map<String, Transcoder> transcoders = new Hashtable<String, Transcoder>();

	private static final Vector<String> locks = new Vector<String>();

	@Override
	public void init () throws ServletException {
		transcoders.clear();

		File xf = new File( PropMaster.get( PropMaster.KEY_DATA_FOLDER, PropMaster.DEF_DATA_FOLDER ), "Codecs" );

		Reflections r = null;
		if (xf.exists()) {
			try {
				r = new Reflections( new ConfigurationBuilder().addUrls( xf.toURI().toURL() ) );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			Set<Class<? extends Transcoder>> subTypes = r.getSubTypesOf( Transcoder.class );

			for (Class<? extends Transcoder> cd : subTypes) {
				try {
					logger.info( "...detected '{}'", cd );
					final Transcoder t = cd.newInstance();
					if (t.init()) {
						transcoders.put( t.getName(), t );
					} else {
						logger.warn( "Initialization of transcoder '{}' was negative; skipping.", t.getName() );
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info( "User-codecs folder, '{}', does not exist and was not scanned.", xf.getAbsolutePath() );

		logger.info( "Scanning in CP: 'com.tofersoft.xinos.server.codec'" );
		r = new Reflections( "com.tofersoft.xinos.server.codec" );

		for (Class<? extends Transcoder> cd : r.getSubTypesOf( AbstractTranscoder.class )) {
			try {
				final Transcoder t = cd.newInstance();
				if (t.init()) {
					transcoders.put( t.getName(), t );
				} else {
					logger.warn( "Initialization of transcoder '{}' was negative; skipping.", t.getName() );
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		logger.info( "{} transcoders available: {}", transcoders.size(), getTranscoders().toString() );

	}


	@Override
	protected void doGet ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		String sessionId = req.getSession().getId();
		sessionId = "17";

		String trackId = req.getParameter( "t" );
		String transcoderId = req.getParameter( "c" );
		String bitRate = req.getParameter( "b" );
		String nextTrackId = req.getParameter( "n" );

		String trackPath = req.getPathInfo();

		// Policy+Of+Truth_4447-OGG-48.ogg
		// Policy+Of+Truth_4450-OGG-48.ogg
		// >> Policy+Of+Truth_4447-OGG-48-4450.ogg
		if (trackPath != null) {
			String[] splits = trackPath.split( "_" );
			String[] args = splits[splits.length - 1].split( "-" );
			args[args.length - 1] = args[args.length - 1].split( "\\." )[0];

			if (trackId == null || "".equals( trackId ))
				trackId = args[0];

			if (transcoderId == null || "".equals( transcoderId ))
				transcoderId = args[1];

			if (bitRate == null || "".equals( bitRate ))
				bitRate = args[2];

			if (!"".equals( nextTrackId ) && args.length > 3) {
				nextTrackId = args[3];
			}

		}

		if (transcoderId == null || "".equals( transcoderId )) {
			transcoderId = DataManager.get().getConfigurableByName( "transcoder.default.codec", "OGG" ).getValue();
		}

		if (bitRate == null || "".equals( bitRate )) {
			bitRate = DataManager.get().getConfigurableByName( "transcoder.default.bitrate", "128" ).getValue();
		}

		logger.info( "Processing request for track '{}' from '{}'", trackId, sessionId );

		String xinosHome = PropMaster.get( PropMaster.KEY_DATA_FOLDER, PropMaster.DEF_DATA_FOLDER );
		File tempFolder = new File( xinosHome, "temp" );
		tempFolder = new File( DataManager.get().getConfigurableByName( "transcoder.temp.folder", tempFolder.getAbsolutePath() ).getValue() );
		if (!tempFolder.exists()) {
			tempFolder.mkdirs();
		}

		Track track = DataManager.get().getTrackById( Integer.parseInt( trackId ) );
		logger.info( "Found '{}", track );

		Transcoder transcoder = transcoders.get( transcoderId );

		int bitRateIndex = transcoder.getBitRateIndex( bitRate );

		List<String> command = new ArrayList<String>();

		command.add( DataManager.get().getConfigurableByName( "ffmpeg.bin", "ffmpeg" ).getValue() );

		command.add( "-y" );

		command.add( "-loglevel" );
		command.add( "quiet" );
		// command.add( "verbose" );

		File outputFile = new File( tempFolder, sessionId + "_" + track.getId() + "_" + transcoderId + "-" + bitRate + "."
				+ transcoder.getFileExtension() );

		String outputPath = outputFile.getAbsolutePath();

		if ( locks.contains( outputPath ) ) {
			logger.warn( "The file is locked; waiting up to 2 minutes: '{}'...", outputPath );
			long waitUntil = System.currentTimeMillis() + ( 2l * 60l * 1000l );
			while ( locks.contains( outputPath ) && System.currentTimeMillis() < waitUntil ) {
				try {
					Thread.sleep( 500 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info( "Done waiting for '{}'.... we'll see what happens", outputPath );
		}

		if (!outputFile.exists()) {

			try {
				locks.add( outputPath );
				command.addAll( transcoder.getCommandArguments( track.getFilename(), outputPath, bitRateIndex ) );

				logger.info( "Transcoding :\n" + command.toString() );

				ProcessBuilder pb = new ProcessBuilder( command );
				pb.redirectErrorStream( true );

				Process process = pb.start();

				InputStream stderr = process.getInputStream();
				InputStreamReader isr = new InputStreamReader( stderr );
				BufferedReader br = new BufferedReader( isr );
				String line = null;

				while ((line = br.readLine()) != null) {
					logger.info( line );
				}

				int exit = process.waitFor();
				logger.info( "Operation completed: {} => {}", exit, outputPath );
			} catch (Throwable e) {
				e.printStackTrace();
				return;
			} finally {
				locks.remove( outputPath );
			}
		} else {
			logger.info( "The file has been pre-transcoded! Serving it, {}, up.", outputPath );
		}

		resp.setContentType( transcoder.getMimeType() );

		resp.setContentLength( new Long( outputFile.length() ).intValue() );
		String dur = ((double) (track.getMillis() / 1000.0d)) + "";

		logger.info( "Duration is '{}' & mime is '{}'", dur, transcoder.getMimeType() );
		resp.addHeader( "X-Content-Duration", dur );
		resp.addHeader( "StreamTitle", track.getTitle() );
		resp.addHeader( "x-audiocast-name", track.getTitle() );
		resp.addHeader( "x-audiocast-bitrate", track.getBitRate()+"" );
		resp.addHeader( "icy-br", track.getBitRate()+"" );
		resp.addHeader( "Accept-Ranges", "bytes" );
		resp.addHeader( "connection", "close" );

		// TODO: Consider this approach:
		//	http://stackoverflow.com/questions/2537306/java-opening-and-reading-from-a-file-without-locking-it

		try {
			logger.info( "Streaming '{}'", outputPath );
			FileInputStream in = new FileInputStream( outputFile );

			byte[] buf = new byte[8 * 1024];
			int bytesRead;
			while ((bytesRead = in.read( buf )) > 0) {
				resp.getOutputStream().write( buf, 0, bytesRead );
				if (!GWT.isProdMode()) {
					Thread.sleep( 5l );
				}
				resp.getOutputStream().flush();
			}

			in.close();

			logger.info( "Done streaming." );
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if ("1".equals( DataManager.get().getConfigurableByName( "transcoder.delete.temp", "1" ).getValue() )) {
			outputFile.delete();
		}

		if (nextTrackId != null && !"".equals( nextTrackId )) {
			track = DataManager.get().getTrackById( Integer.parseInt( nextTrackId ) );

			command.clear();

			command.add( DataManager.get().getConfigurableByName( "ffmpeg.bin", "ffmpeg" ).getValue() );

			command.add( "-y" );

			command.add( "-loglevel" );
			command.add( "quiet" );
			// command.add( "verbose" );

			outputFile = new File( tempFolder, sessionId + "_" + track.getId() + "_" + transcoderId + "-" + bitRate + "."
					+ transcoder.getFileExtension() );

			outputPath = outputFile.getAbsolutePath();

			try {

				locks.add( outputPath );

				command.addAll( transcoder.getCommandArguments( track.getFilename(), outputPath, bitRateIndex ) );

				logger.info( "Pre-transcoding :\n" + command.toString() );

				ProcessBuilder pb = new ProcessBuilder( command );
				pb.redirectErrorStream( true );

				Process process = pb.start();

				InputStream stderr = process.getInputStream();
				InputStreamReader isr = new InputStreamReader( stderr );
				BufferedReader br = new BufferedReader( isr );
				String line = null;

				while ((line = br.readLine()) != null) {
					logger.info( line );
				}

				int exit = process.waitFor();
				logger.info( "Pre-transcoding operation completed: {} => {}", exit, outputPath );
			} catch (Throwable e) {
				e.printStackTrace();
				return;
			} finally {
				locks.remove( outputPath );
			}

			final long tooOld = System.currentTimeMillis()
					- new Long( DataManager.get().getConfigurableByName( "streamer.stale.age", "3600000" ).getValue() ).longValue();
			for (File f : outputFile.getParentFile().listFiles()) {
				if (f.lastModified() <= tooOld) {
					f.delete();
				}
			}

		}

	}


	public static List<Transcoder> getTranscoders () {
		List<Transcoder> sortedTranscoders = new ArrayList<Transcoder>( transcoders.values() );

		Collections.sort( sortedTranscoders );

		return sortedTranscoders;
	}


	public static String getExtensionForCodec ( String codecId ) {
		final Transcoder transcoder = transcoders.get( codecId );
		return transcoder != null ? transcoder.getFileExtension() : "";
	}

}
