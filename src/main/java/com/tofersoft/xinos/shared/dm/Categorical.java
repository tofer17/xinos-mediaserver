package com.tofersoft.xinos.shared.dm;


public enum Categorical {

	// @formatter:off
	GENRE         ( "Genre", TagDef.GENRE.tagKey ),
	ARTIST        ( "Artist", TagDef.ARTIST.tagKey ),
	ALBUM         ( "Album", TagDef.ALBUM.tagKey ),
	YEAR          ( "Year", TagDef.DATE.tagKey ),
	COMPOSER      ( "Composer", TagDef.COMPOSER.tagKey ),
	//BPM           ( "BPM", TagDef.BPM ),
	//RATING        ( "Rating", TagDef.RATING ),
	ALBUM_ARTIST  ( "Album Artist", TagDef.ALBUM_ARTIST.tagKey ),
	//PLAY_COUNT    ( "Play Count", TagDef.PLAY_COUNT ),
	KIND          ( "Kind", "formatId" ),
	SIZE          ( "Size", "size" ),
	DURATION      ( "Duration", "millis" ),
	SAMPLE_RATE   ( "Sample Rate", "sampleRate" ),
	CHANNELS      ( "Channels", "channels" ),
	MODIFIED      ( "Modified", "modified" ),
	ALL           ( null, null );
	// @formatter:on

	public String name;
	public String tag;


	private Categorical ( String name, String tag ) {
		this.name = name;
		this.tag = tag;
	}

}
