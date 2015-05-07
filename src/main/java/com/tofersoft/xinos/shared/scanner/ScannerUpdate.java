package com.tofersoft.xinos.shared.scanner;


import java.io.Serializable;

import com.tofersoft.xinos.shared.dm.Library;


public class ScannerUpdate implements Serializable {

	private static final long serialVersionUID = -7590456723769482789L;

	public String id;
	public Library library;
	public String message;
	public long filesScanned;
	public long totalFiles;
	public long totalDirectories;
	public ScannerStatusMessage status;


	public ScannerUpdate () {
		super();
	}


	public ScannerUpdate ( String id, Library library, String message, long filesScanned, long totalFiles, long totalDirectories, ScannerStatusMessage status ) {
		super();
		this.id = id;
		this.library = library;
		this.message = message;
		this.filesScanned = filesScanned;
		this.totalFiles = totalFiles;
		this.totalDirectories = totalDirectories;
		this.status = status;
	}


	@Override
	public String toString () {
		return new StringBuilder( "Scanner Update from '" ).append( id ).append( "' for '" ).append( library.getPath() ).append( "' says '" )
				.append( message ).append( "'; thus far " ).append( filesScanned ).append( " files have been scanned out of " ).append( totalFiles )
				.append( " (" ).append( totalDirectories ).append( " dirs.); status is '" ).append( status ).append( "'." ).toString();
	}

}
