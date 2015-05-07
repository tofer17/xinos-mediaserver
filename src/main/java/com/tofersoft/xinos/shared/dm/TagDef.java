package com.tofersoft.xinos.shared.dm;


/**
 * Supported tags are:
 * <ul>
 * <li>creation_time: 2005-12-29 10:06:33</li>
 * <li>title: Atonement</li>
 * <li>artist: John Powell</li>
 * <li>composer: John Powell</li>
 * <li>album: Bourne Supremacy</li>
 * <li>genre: Soundtrack</li>
 * <li>track: 12</li>
 * <li>disc: 1</li>
 * <li>date: 2004</li>
 * <li>compilation: 1</li>
 * <li>encoder: iTunes v6.0.1.3</li>
 * <li>album_artist: Album Artist</li>
 * <li>comment: Some comments, eh?</li>
 * <li>publisher: Pub Lish Er</li>
 * <li>grouping: categ</li>
 * <li>lyrics: lyrics!</li>
 * <li>gapless_playback: 1</li>
 * </ul>
 *
 * @author cmetyko
 *
 */
public enum TagDef {

	// @formatter:off
	// ENUM            KEY      TITLE    Type
	GENRE            ( "genre", "Genre", java.lang.String.class ),
	ARTIST           ( "artist", "Artist", java.lang.String.class ),
	ALBUM_ARTIST     ( "albumArtist", "Album Artist", java.lang.String.class ),
	ALBUM            ( "album", "Album", java.lang.String.class ),
	TRACK            ( "track", "Track #", java.lang.Integer.class ),
	DISC             ( "disc", "Disc #", java.lang.String.class ),

	TITLE            ( "title", "Title", java.lang.String.class ),

	COMMENT          ( "comment", "Comment", java.lang.String.class ),
	LYRICS           ( "lyrics", "Lyrics", java.lang.String.class ),

	COMPOSER         ( "composer", "Composer", java.lang.String.class ),
	PUBLISHER        ( "publisher", "Publisher", java.lang.String.class ),
	COPYRIGHT        ( "copyright", "Copyright", java.lang.String.class ),

	DATE             ( "date", "Year", java.lang.Integer.class ),
	//GROUPING         ( "grouping", "Category Grouping", java.lang.String.class ),
	COMPILATION      ( "compilation", "Part of Compilation", java.lang.Boolean.class ),
	//GAPLESS_PLAYBACK ( "gapless_playback", "Gapless Playback", java.lang.Boolean.class ),

	CREATION_TIME    ( "creationTime", "Creation", java.util.Date.class ),
	ENCODER          ( "encoder", "Encoder", java.lang.String.class );
	// @formatter:on

	public final String tagKey;

	public final String tagTitle;

	public final Class<?> tagType;


	private TagDef ( String tagKey, String tagTitle, Class<?> tagType ) {
		this.tagKey = tagKey;
		this.tagTitle = tagTitle;
		this.tagType = tagType;
	}

}
