package com.tofersoft.xinos.shared.dm;


import java.io.Serializable;
import java.util.Date;

/**
 * The selection of ID3v2 tags is adapted from:
 * 	https://www.ffmpeg.org/doxygen/trunk/id3v2_8c_source.html#l00995
 * @author cmetyko
 *
 */
public class Track implements Serializable {

	private static final long serialVersionUID = 7843066987258647153L;

	private int id = -1;

	private Library library = null;

	private String filename = null;

	/**
	 * The file size in bytes.
	 */
	private long size = 0;

	/**
	 * The Date when the file was last modified.
	 */
	private Date modified = null;

	/**
	 * The Date when the file was cataloged.
	 */
	private Date cataloged = null;

	/**
	 * The duration of the track in milliseconds
	 */
	private long millis = 0;

	/**
	 * The codec used to encode the audio such as "ALAC".
	 */
	private String format = null;

	/**
	 * Stereo = 2, Mono = 1, Unknown = -1;
	 */
	private int channels = -1;

	/**
	 * In Bits-per-Second (not kBps!).
	 */
	private int bitRate = 0;

	/**
	 * Most often always 41100.
	 */
	private int sampleRate = 0;

	/**
	 * "TALB" id3v2 tag.
	 */
	private String album = null;

	/**
	 * "TPE2" id3v2 tag.
	 */
	private String albumArtist = null;

	/**
	 * "TSOA" id3v2 tag.
	 */
	private String albumSort = null;

	/**
	 * "TPE1" id3v2 tag.
	 */
	private String artist = null;

	/**
	 * "TSOP" id3v2 tag.
	 */
	private String artistSort = null;

	/**
	 * "COMM" id3v2 tag.
	 */
	private String comment = null;

	/**
	 * "TCMP" id3v2 tag.
	 */
	private Boolean compilation = null;

	/**
	 * "TCOM" id3v2 tag.
	 */
	private String composer = null;

	/**
	 * "TCOP" id3v2 tag.
	 */
	private String copyright = null;

	/**
	 * "TDEN" id3v2 tag.
	 */
	private Date creationTime = null;

	/**
	 * "TDRC" or "TDRL" id3v2 tag.
	 */
	private Date date = null;

	/**
	 * "TPOS" first part of the decomposed id3v2 tag; Integer.MIN_VALUE is used
	 * if n/a.
	 */
	private int discSeq = Integer.MIN_VALUE;

	/**
	 * "TPOS" second part of the decomposed id3v2 tag; Integer.MIN_VALUE is used
	 * if n/a.
	 */
	private int discsCount = Integer.MIN_VALUE;

	/**
	 * "TENC" id3v2 tag.
	 */
	private String encodedBy = null;

	/**
	 * "TSSE" id3v2 tag.
	 */
	private String encoder = null;

	/**
	 * "TCON" id3v2 tag.
	 */
	private String genre = null;

	/**
	 * "TLAN" id3v2 tag.
	 */
	private String language = null;

	/**
	 * "TPE3" id3v2 tag.
	 */
	private String performer = null;

	/**
	 * "TPUB" id3v2 tag.
	 */
	private String publisher = null;

	/**
	 * "TIT2" id3v2 tag.
	 */
	private String title = null;

	/**
	 * "TSOT" id3v2 tag.
	 */
	private String titleSort = null;

	/**
	 * "TRCK" first part of the decomposed id3v2 tag; Integer.MIN_VALUE is used
	 * if n/a.
	 */
	private int trackSeq = Integer.MIN_VALUE;

	/**
	 * "TRCK" second part of the decomposed id3v2 tag; Integer.MIN_VALUE is used
	 * if n/a.
	 */
	private int tracksCount = Integer.MIN_VALUE;


	public Track () {
		super();
	}


	public int getId () {
		return id;
	}


	public Track setId ( int id ) {
		this.id = id;
		return this;
	}


	public Library getLibrary () {
		return library;
	}


	public Track setLibrary ( Library library ) {
		this.library = library;
		return this;
	}


	public String getFilename () {
		return filename;
	}


	public Track setFilename ( String filename ) {
		this.filename = filename;
		return this;
	}


	public long getSize () {
		return size;
	}


	public Track setSize ( long size ) {
		this.size = size;
		return this;
	}


	public Date getModified () {
		return modified;
	}


	public Track setModified ( Date modified ) {
		this.modified = modified;
		return this;
	}


	public Date getCataloged () {
		return cataloged;
	}


	public Track setCataloged ( Date cataloged ) {
		this.cataloged = cataloged;
		return this;
	}


	public long getMillis () {
		return millis;
	}


	public Track setMillis ( long millis ) {
		this.millis = millis;
		return this;
	}


	public String getDuration () {
		return formatMillisToDuration( millis );
	}


	public String getFormat () {
		return format;
	}


	public Track setFormat ( String format ) {
		this.format = format;
		return this;
	}


	public int getChannels () {
		return channels;
	}


	public boolean isStereo () {
		return channels == 2;
	}


	public boolean isMono () {
		return channels == 1;
	}


	public Track setChannels ( int channels ) {
		this.channels = channels;
		return this;
	}


	public int getBitRate () {
		return bitRate;
	}


	public Track setBitRate ( int bitRate ) {
		this.bitRate = bitRate;
		return this;
	}


	public int getSampleRate () {
		return sampleRate;
	}


	public Track setSampleRate ( int sampleRate ) {
		this.sampleRate = sampleRate;
		return this;
	}


	public String getAlbum () {
		return album;
	}


	public Track setAlbum ( String album ) {
		this.album = album;
		return this;
	}


	public String getAlbumArtist () {
		return albumArtist;
	}


	public Track setAlbumArtist ( String albumArtist ) {
		this.albumArtist = albumArtist;
		return this;
	}


	public String getAlbumSort () {
		return albumSort;
	}


	public Track setAlbumSort ( String albumSort ) {
		this.albumSort = albumSort;
		return this;
	}


	public String getArtist () {
		return artist;
	}


	public Track setArtist ( String artist ) {
		this.artist = artist;
		return this;
	}


	public String getArtistSort () {
		return artistSort;
	}


	public Track setArtistSort ( String artistSort ) {
		this.artistSort = artistSort;
		return this;
	}


	public String getComment () {
		return comment;
	}


	public Track setComment ( String comment ) {
		this.comment = comment;
		return this;
	}


	public Boolean getCompilation () {
		return compilation;
	}


	public boolean isCompilation () {
		return compilation != null && compilation.booleanValue();
	}


	public Track setCompilation ( Boolean compilation ) {
		this.compilation = compilation;
		return this;
	}


	public String getComposer () {
		return composer;
	}


	public Track setComposer ( String composer ) {
		this.composer = composer;
		return this;
	}


	public String getCopyright () {
		return copyright;
	}


	public Track setCopyright ( String copyright ) {
		this.copyright = copyright;
		return this;
	}


	public Date getCreationTime () {
		return creationTime;
	}


	public Track setCreationTime ( Date creationTime ) {
		this.creationTime = creationTime;
		return this;
	}


	public Date getDate () {
		return date;
	}


	public Track setDate ( Date date ) {
		this.date = date;
		return this;
	}


	public int getDiscSeq () {
		return discSeq;
	}


	public Track setDiscSeq ( int discSeq ) {
		this.discSeq = discSeq;
		return this;
	}


	public int getDiscsCount () {
		return discsCount;
	}


	public Track setDiscsCount ( int discsCount ) {
		this.discsCount = discsCount;
		return this;
	}


	/**
	 * Returns something like "1/2" or "1/?" or "?/?" (or possibly "?/2").
	 *
	 * @return
	 */
	public String getDiscInDiscs () {
		return new StringBuilder() // @formatter:off
			.append( discSeq != Integer.MIN_VALUE ? ""+discSeq : "?")
			.append( "/" )
			.append( discsCount != Integer.MIN_VALUE ? ""+discsCount : "?")
			.toString(); // @formatter:on
	}


	/**
	 * Sets discSeq and discsCount from the string such as "1/2" or "1"; accepts
	 * and properly handles a null value as well; doesn't complain about
	 * improperly formated discInDiscs string, such as "1/a".
	 *
	 * @param discInDiscs
	 * @return
	 */
	public Track setDiscInDiscs ( String discInDiscs ) {

		discSeq = Integer.MIN_VALUE;
		discsCount = Integer.MIN_VALUE;

		if (discInDiscs == null || discInDiscs.length() < 1) {
			return this;
		}

		String[] splits = discInDiscs.split( "/" );

		if (splits == null || splits.length < 1) {
			return this;
		}

		try {
			discSeq = Integer.parseInt( splits[0].trim() );
		} catch (NumberFormatException nfe) {
			; //
		}

		if (splits.length > 1) {
			try {
				discsCount = Integer.parseInt( splits[1].trim() );
			} catch (NumberFormatException nfe) {
				; //
			}
		}

		return this;
	}


	public String getEncodedBy () {
		return encodedBy;
	}


	public Track setEncodedBy ( String encodedBy ) {
		this.encodedBy = encodedBy;
		return this;
	}


	public String getEncoder () {
		return encoder;
	}


	public Track setEncoder ( String encoder ) {
		this.encoder = encoder;
		return this;
	}


	public String getGenre () {
		return genre;
	}


	public Track setGenre ( String genre ) {
		this.genre = genre;
		return this;
	}


	public String getLanguage () {
		return language;
	}


	public Track setLanguage ( String language ) {
		this.language = language;
		return this;
	}


	public String getPerformer () {
		return performer;
	}


	public Track setPerformer ( String performer ) {
		this.performer = performer;
		return this;
	}


	public String getPublisher () {
		return publisher;
	}


	public Track setPublisher ( String publisher ) {
		this.publisher = publisher;
		return this;
	}


	public String getTitle () {
		return title;
	}


	public Track setTitle ( String title ) {
		this.title = title;
		return this;
	}


	public String getTitleSort () {
		return titleSort;
	}


	public Track setTitleSort ( String titleSort ) {
		this.titleSort = titleSort;
		return this;
	}


	public int getTrackSeq () {
		return trackSeq;
	}


	public Track setTrackSeq ( int trackSeq ) {
		this.trackSeq = trackSeq;
		return this;
	}


	public int getTracksCount () {
		return tracksCount;
	}


	public Track setTracksCount ( int tracksCount ) {
		this.tracksCount = tracksCount;
		return this;
	}


	/**
	 * Returns something like "1/2" or "1/?" or "?/?" (or possibly "?/2").
	 *
	 * @return
	 */
	public String getTrackInTracks () {
		return new StringBuilder() // @formatter:off
			.append( trackSeq != Integer.MIN_VALUE ? ""+trackSeq : "?")
			.append( "/" )
			.append( tracksCount != Integer.MIN_VALUE ? ""+tracksCount : "?")
			.toString(); // @formatter:on
	}


	/**
	 * Sets trackSeq and tracksCount from the string such as "1/2" or "1";
	 * accepts and properly handles a null value as well; doesn't complain about
	 * improperly formated trackInTracks string, such as "1/a".
	 *
	 * @param trackInTracks
	 * @return
	 */
	public Track setTrackInTracks ( String trackInTracks ) {

		trackSeq = Integer.MIN_VALUE;
		tracksCount = Integer.MIN_VALUE;

		if (trackInTracks == null || trackInTracks.length() < 1) {
			return this;
		}

		String[] splits = trackInTracks.split( "/" );
		if (splits == null || splits.length < 1) {
			return this;
		}

		try {
			trackSeq = Integer.parseInt( splits[0].trim() );
		} catch (NumberFormatException nfe) {
			; //
		}

		if (splits.length > 1) {
			try {
				tracksCount = Integer.parseInt( splits[1].trim() );
			} catch (NumberFormatException nfe) {
				; //
			}
		}

		return this;
	}


	/**
	 * Returns something like "1/10 (1/2)"; see getTrackInTracks and
	 * getDiscInDiscs.
	 *
	 * @return
	 */
	public String getSequence () {
		return new StringBuilder( getTrackInTracks() )// @formatter:off
			.append( " (" )
			.append( getDiscInDiscs() )
			.append( ")" )
			.toString(); // @formatter:on
	}


	public String debug () {
		return new StringBuilder( getClass().getName() ).append( "@" ).append( hashCode() ) // @formatter:off
			.append( ":[" )
			.append( "id=" ).append( id )
			.append( " library='" ).append( library ).append( "'" )
			.append( " filename='" ).append( filename ).append( "'" )
			.append( " size=" ).append( size )
			.append( " millis=" ).append( millis )
			.append( " format='" ).append( format ).append( "'" )
			.append( " channels=" ).append( channels )
			.append( " bitRate=" ).append( bitRate )
			.append( " sampleRate=" ).append( sampleRate )
			.append( " modified='" ).append( modified ).append( "'" )
			.append( " cataloged='" ).append( cataloged ).append( "'" )

			.append( " album='" ).append( album ).append( "'" )
			.append( " albumArtist='" ).append( albumArtist ).append( "'" )
			.append( " albumSort='" ).append( albumSort ).append( "'" )
			.append( " artist='" ).append( artist ).append( "'" )
			.append( " artistSort='" ).append( artistSort ).append( "'" )
			.append( " comment='" ).append( comment ).append( "'" )
			.append( " compilation='" ).append( compilation != null ? compilation.booleanValue() : null ).append( "'" )
			.append( " composer='" ).append( composer ).append( "'" )
			.append( " copyright='" ).append( copyright ).append( "'" )
			.append( " creationTime='" ).append( creationTime ).append( "'" )
			.append( " date='" ).append( date ).append( "'" )
			.append( " encodedBy='" ).append( encodedBy ).append( "'" )
			.append( " encoder='" ).append( encoder ).append( "'" )
			.append( " genre='" ).append( genre ).append( "'" )
			.append( " language='" ).append( language ).append( "'" )
			.append( " performer='" ).append( performer ).append( "'" )
			.append( " publisher='" ).append( publisher ).append( "'" )
			.append( " title='" ).append( title ).append( "'" )
			.append( " titleSort='" ).append( titleSort ).append( "'" )
			.append( " sequence='" ).append( getSequence() ).append( "'" )
			.append( "]" ).toString(); // @formatter:on
	}


	@Override
	public String toString () {
		return new StringBuilder() // @formatter:off
			.append( super.toString() ).append( "[" )
			.append( "id=" ).append( id )
			.append( "filename='" ).append( filename ).append( "'" )
			.append( "format='" ).append( format ).append( "'" )
			.append( "]" ).toString(); // @formatter:on
	}


	/**
	 * Formats given number of milliseconds into H:M:S.ms string.
	 *
	 * @param millis
	 *            Number of milliseconds
	 * @return Those milliseconds formatted into H:M:S.ms
	 */
	public static String formatMillisToDuration ( long millis ) {

		final int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
		final int minutes = (int) ((millis / (1000 * 60)) % 60);
		final int seconds = (int) (millis / 1000) % 60;

		final StringBuilder sb = new StringBuilder();

		if (hours > 0) {
			sb.append( hours ).append( ":" );
		}

		if (hours > 0 || minutes > 0) {
			if (hours > 0 && minutes < 10)
				sb.append( "0" );
			sb.append( minutes ).append( ":" );
		}

		if ((hours > 0 || minutes > 0) && seconds < 10)
			sb.append( "0" );
		sb.append( seconds );

		return sb.toString();
	}

}
