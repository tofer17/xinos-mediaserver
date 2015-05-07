package com.tofersoft.xinos.server;


import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.KeyValueBag;
import io.humble.video.MediaDescriptor.Type;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DecoderMaster {

	private static final Logger logger = LoggerFactory.getLogger( DecoderMaster.class );

	public static final String FILE_EXTS = "mp3|m4a|mp4|m4p|wav|aac|aif|aiff|ogg|flac";

	public static final String FILE_EXTS_REGEX = "^.*\\.(" + FILE_EXTS + ")$";

	public static final Pattern fileExtsPattern = Pattern.compile( FILE_EXTS_REGEX );

	private static final SimpleDateFormat dfA = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

	private static final SimpleDateFormat dfB = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );

	private static final SimpleDateFormat dfC = new SimpleDateFormat( "yyyy" );


	public static synchronized ExtTrack catalogFile ( File file ) {

		final long startMillis = System.currentTimeMillis();

		ExtTrack track = new ExtTrack();
		// Just in case someone overrides or alters ExtTrack and changes its
		// default confidence of 0.0d to something else...
		track.setConfidence( 0.0d );

		if (file == null || file.getName().length() < 4) {
			return track;
		}

		final String filename = file.getName().toLowerCase();

		if (!fileExtsPattern.matcher( filename ).matches()) {
			return track;
		}

		logger.trace( "Cataloging '{}'...", file.getAbsolutePath() );

		track.setFilename( file.getAbsolutePath() );
		track.setSize( file.length() );
		track.setModified( new Date( file.lastModified() ) );
		track.setCataloged( new Date() );

		Demuxer demuxer = null;
		try {
			demuxer = Demuxer.make();
		} catch (Throwable t) {
			; // well, ya'know...
		}

		try {
			demuxer.open( file.getAbsolutePath(), null, false, true, null, null );
		} catch (InterruptedException e) {
			track.setThrown( e );
			return track;
		} catch (IOException e) {
			track.setThrown( e );
			return track;
		} catch (Throwable t) {
			track.setThrown( t );
			return track;
		}

		track.setBitRate( demuxer.getBitRate() );

		KeyValueBag metadata = demuxer.getMetaData();

		Boolean compilation = null;
		String s = metadata.getValue( "compilation" );
		if ("1".equals( s )) {
			compilation = true;
		}

		s = metadata.getValue( "creation_time" );
		final Date creationTime = parseDate( s );

		s = metadata.getValue( "date" );
		final Date date = parseDate( s );

		track.setAlbum( metadata.getValue( "album" ) ).setAlbumArtist( metadata.getValue( "album_artist" ) )
				.setAlbumSort( metadata.getValue( "album-sort" ) ).setArtist( metadata.getValue( "artist" ) )
				.setArtistSort( metadata.getValue( "artist-sort" ) ).setComment( metadata.getValue( "comment" ) ).setCompilation( compilation )
				.setComposer( metadata.getValue( "composer" ) ).setCopyright( metadata.getValue( "copyright" ) ).setCreationTime( creationTime )
				.setDate( date ).setDiscInDiscs( metadata.getValue( "disc" ) ).setEncodedBy( metadata.getValue( "encoded_by" ) )
				.setEncoder( metadata.getValue( "encoder" ) ).setGenre( metadata.getValue( "genre" ) ).setLanguage( metadata.getValue( "language" ) )
				.setPerformer( metadata.getValue( "performer" ) ).setPublisher( metadata.getValue( "publisher" ) )
				.setTitle( metadata.getValue( "title" ) ).setTitleSort( metadata.getValue( "title-sort" ) )
				.setTrackInTracks( metadata.getValue( "track" ) );

		int streamCount = 0;
		try {
			streamCount = demuxer.getNumStreams();
		} catch (InterruptedException e) {
			track.setThrown( e );
			return track;
		} catch (IOException e) {
			track.setThrown( e );
			return track;
		}

		for (int streamIndex = 0; streamIndex < streamCount; streamIndex++) {
			DemuxerStream stream = null;

			try {
				stream = demuxer.getStream( streamIndex );
			} catch (InterruptedException e) {
				track.setThrown( e );
				return track;
			} catch (IOException e) {
				track.setThrown( e );
				return track;
			}

			Decoder decoder = stream.getDecoder();

			if (decoder.getCodecType() == Type.MEDIA_AUDIO) {

				final double millisD = (double) stream.getTimeBase().getDouble() * stream.getDuration() * 1000.0d * 100.0d;
				final long millisL = Math.round( millisD ) / 100l;

				track.setMillis( millisL );

				track.setBitRate( decoder.getPropertyAsInt( "b" ) );
				track.setSampleRate( decoder.getSampleRate() );
				track.setChannels( decoder.getChannels() );

				track.setFormat( decoder.getCodec().getName() );

				// We're done here.
				streamIndex = streamCount;
			}
		}

		try {
			demuxer.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		track.setConfidence( 1.0d );
		track.setMillisToDecode( System.currentTimeMillis() - startMillis );

		return track;

	}


	private static final Date parseDate ( String s ) {
		if (s != null && s.length() == 4) {
			try {
				return dfC.parse( s );
			} catch (Throwable t) {
				logger.trace( "Oopps", t );
			}
		} else if (s != null) {
			try {
				return dfA.parse( s );
			} catch (Throwable t1) {
				try {
					return dfB.parse( s );
				} catch (Throwable t2) {
					logger.trace( "Nope:'" + s + "'", t2 );
				}
			}
		}

		return null;
	}

}
