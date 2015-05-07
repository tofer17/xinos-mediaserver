package com.tofersoft.xinos.shared.dm;


import java.io.Serializable;


public class Path extends Object implements Serializable, Comparable<Path> {

	private static final long serialVersionUID = 4016167189508230127L;

	private String name;
	private String absolutePath;
	private boolean empty = true;


	public Path () {
		this( null, null );
	}


	public Path ( String name ) {
		this( name, name );
	}


	public Path ( String name, String absolutePath ) {
		super();
		this.name = name;
		this.absolutePath = absolutePath;
	}


	public String getName () {
		return name;
	}


	public void setName ( String name ) {
		this.name = name;
	}


	public String getAbsolutePath () {
		return absolutePath;
	}


	public void setAbsolutePath ( String absolutePath ) {
		this.absolutePath = absolutePath;
	}


	public String getAbs () {
		return getAbsolutePath();
	}


	public void setAbs ( String absolutePath ) {
		this.absolutePath = absolutePath;
	}


	public boolean isEmpty () {
		return empty;
	}


	public void setEmpty ( boolean empty ) {
		this.empty = empty;
	}


	@Override
	public int compareTo ( Path o ) {
		if (o == null && absolutePath == null)
			return 0;
		if (absolutePath == null)
			return -1;
		return o == null ? 1 : absolutePath.compareTo( o.absolutePath );
	}


	@Override
	public boolean equals ( Object obj ) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Path)) {
			return false;
		}

		Path p = (Path) obj;
		return absolutePath != null && absolutePath.equals( p.absolutePath );
	};


	@Override
	public String toString () {
		// @formatter:off
		return new StringBuilder( "Path:['" )
			.append( name ).append( "'='" ).append( absolutePath ).append( "'" )
			.append( empty ? "[ ]" : "[X]" )
			.append( "]" )
			.toString();
		// @formatter:on
	}

}
