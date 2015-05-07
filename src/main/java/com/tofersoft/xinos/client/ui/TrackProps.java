package com.tofersoft.xinos.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.dm.Track;


public class TrackProps {

	public static final TrackPropertyAccess get = GWT.create( TrackPropertyAccess.class );

	public interface TrackPropertyAccess extends PropertyAccess<Track> {

		@Path( "id" )
		public ModelKeyProvider<Track> key ();


		@Path( "id" )
		public ValueProvider<Track, Integer> id ();


		@Path( "library" )
		public ValueProvider<Track, Library> library ();


		@Path( "filename" )
		public ValueProvider<Track, String> filename ();


		@Path( "title" )
		public ValueProvider<Track, String> title ();


		@Path( "size" )
		public ValueProvider<Track, Long> size ();


		@Path( "millis" )
		public ValueProvider<Track, Long> millis ();


		@Path( "duration" )
		public ValueProvider<Track, String> duration ();


		@Path( "format" )
		public ValueProvider<Track, String> format ();


		@Path( "channels" )
		public ValueProvider<Track, Integer> channels ();


		@Path ( "bitRate" )
		public ValueProvider<Track, Integer> bitRate ();


		@Path ( "sampleRate" )
		public ValueProvider<Track, Integer> sampleRate ();


		@Path( "cataloged" )
		public ValueProvider<Track, Date> cataloged ();


		@Path( "modified" )
		public ValueProvider<Track, Date> modified ();


		@Path ( "discSeq" )
		public ValueProvider<Track, Integer> discSeq ();


		@Path ( "trackSeq" )
		public ValueProvider<Track, Integer> trackSeq ();


		@Path ( "sequence" )
		public ValueProvider<Track, String> sequence ();


		@Path ( "album" )
		public ValueProvider<Track, String> album ();


		@Path ( "artist" )
		public ValueProvider<Track, String> artist ();


		@Path ( "albumArtist" )
		public ValueProvider<Track, String> albumArtist ();


		@Path ( "comment" )
		public ValueProvider<Track, String> comment ();


		@Path ( "compilation" )
		public ValueProvider<Track, Boolean> compilation ();


		@Path ( "composer" )
		public ValueProvider<Track, String> composer ();


		@Path ( "copyright" )
		public ValueProvider<Track, String> copyright ();


		@Path ( "creationTime" )
		public ValueProvider<Track, Date> creationTime ();


		@Path ( "date" )
		public ValueProvider<Track, Date> date ();


		@Path ( "encoder" )
		public ValueProvider<Track, String> encoder ();


		@Path ( "genre" )
		public ValueProvider<Track, String> genre ();


		@Path ( "publisher" )
		public ValueProvider<Track, String> publisher ();

	}


}
