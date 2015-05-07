package com.tofersoft.xinos.server;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tofersoft.xinos.server.db.DataManager;
import com.tofersoft.xinos.shared.dm.Track;


public class PlayerLister extends HttpServlet {

	private static final long serialVersionUID = 1978943747665364666L;

	private static final Logger logger = LoggerFactory.getLogger( PlayerLister.class );


	@Override
	protected void doGet ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		this.doPost( req, resp );
	}


	@Override
	protected void doPost ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		String tracksString = req.getParameter( "ids" );
		logger.info( "Handling request for '{}'...", tracksString );

		String[] idStrings = tracksString.split( "," );
		List<Integer> trackIds = new ArrayList<Integer>( idStrings.length );
		for (String id : idStrings) {
			trackIds.add( new Integer( id ) );
		}

		List<Track> tracks = XinosCore.getInstance().getTracks( trackIds );

		resp.setContentType( "audio/x-scpls" );

		String codec = DataManager.get().getConfigurableByName( "transcoder.default.codec", "OGG" ).getValue();
		String bitrate = DataManager.get().getConfigurableByName( "transcoder.default.bitrate", "128" ).getValue();
		String fileExt = Streamer.getExtensionForCodec( codec );

		String url = req.getRequestURL().toString();
		url = url.substring( 0, url.length() - 10 ) + "Player/stream/";

		logger.info( "Base URL is: '{}'", url );

		// TODO: consider to encode "sessionId" into PLS entry. :)

		PrintWriter out = resp.getWriter();
		out.print( "[playlist]" );
		out.println();

		for (int i = 0; i < tracks.size(); i++) {
			Track track = tracks.get( i );

			out.print( "Title" );
			out.print( i + 1 );
			out.print( "=" );
			out.print( track.getTitle() );
			out.println();

			out.print( "File" );
			out.print( i + 1 );
			out.print( "=" );
			out.print( url );
			out.print( URLEncoder.encode( track.getTitle(), "UTF-8" ) );
			out.print( "_" );
			out.print( track.getId() );
			out.print( "-" );
			out.print( codec );
			out.print( "-" );
			out.print( bitrate );
			out.print( "-" );
			out.print( i == tracks.size() - 1 ? tracks.get( 0 ).getId() : tracks.get( i + 1 ).getId() );
			out.print( "." );
			out.print( fileExt );
			out.println();

			out.print( "Length" );
			out.print( i + 1 );
			out.print( "=" );
			out.print( track.getMillis() / 1000l );
			out.println();

		}

		out.print( "NumberOfEntries=" );
		out.print( tracks.size() );
		out.println();

		out.print( "Version=2" );
		out.println();

	}

}
