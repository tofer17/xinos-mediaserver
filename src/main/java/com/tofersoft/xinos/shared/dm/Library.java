package com.tofersoft.xinos.shared.dm;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Library implements Serializable, Comparable<Library> {

	private static final long serialVersionUID = 3214078580713412206L;

	private static Map<Integer, Library> libraries = new HashMap<Integer, Library>();

	private Integer id;

	private String path;

	private Date lastScanned;

	private int tracks;

	private boolean scanning;

	private double progress;


	public Library () {
		super();

		id = null;
		path = null;
		lastScanned = null;
		tracks = 0;

		scanning = false;
		progress = -1.0d;
	}


	public Library ( Integer id, String path ) {
		this();

		this.id = id;
		this.path = path;
	}


	public Library ( Integer id, String path, Date lastScanned, int tracks ) {
		this();

		this.id = id;
		this.path = path;
		this.lastScanned = lastScanned;
		this.tracks = tracks;

		if (!libraries.containsKey( id )) {
			libraries.put( id, this );
		}

		lastScanned = null;
	}


	public Integer getId () {
		return id != null ? id : new Integer( -1 );
	}


	public void setId ( Integer id ) {

		libraries.remove( id );

		this.id = new Integer( id );

		libraries.put( this.id, this );
	}


	public String getPath () {
		return path;
	}


	public void setPath ( String path ) {
		this.path = path;
	}


	public Date getLastScanned () {
		return lastScanned;
	}


	public void setLastScanned ( Date lastScanned ) {
		this.lastScanned = lastScanned;
	}


	public void setLastScanned () {
		this.setLastScanned( new Date() );
	}


	public Date getLast () {
		return lastScanned;
	}


	public void setLast ( Date lastScanned ) {
		this.lastScanned = lastScanned;
	}


	public int getTracks () {
		return tracks;
	}


	public void setTracks ( int tracks ) {
		this.tracks = tracks;
	}


	public boolean isScanning () {
		return scanning;
	}


	public void setScanning ( boolean scanning ) {
		this.scanning = scanning;
	}


	public double getProgress () {
		return progress;
	}


	public void setProgress ( double progress ) {
		this.progress = progress;
	}


	public static Library getLibraryForId ( int id ) {
		return libraries.get( id );
	}


	public static Library removeLibrary ( Library library ) {
		return libraries.remove( library.getId() );
	}


	public static int size () {
		return libraries.size();
	}


	@Override
	public String toString () {
		return new StringBuilder( "Library " ).append( " id=" ).append( id ).append( "" ).append( " path='" ).append( path ).append( "'" )
				.append( " lastScanned='" ).append( lastScanned ).append( "'" ).toString();
	}


	@Override
	public int compareTo ( Library o ) {
		return path.compareTo( o.path );
	}

}
