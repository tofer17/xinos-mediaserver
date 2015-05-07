package com.tofersoft.xinos.client.rpc;


import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tofersoft.xinos.shared.dm.Categorical;
import com.tofersoft.xinos.shared.dm.Configurable;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.dm.Path;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;
import com.tofersoft.xinos.shared.scanner.ScannerControlMessage;
import com.tofersoft.xinos.shared.scanner.ScannerUpdate;


@RemoteServiceRelativePath ( "core" )
public interface CoreService extends RemoteService {

	public List<Path> getPathList ( String child );


	public List<Library> getLibraries ();


	public List<String> getParents ( String childPath );


	public String mkdir ( String parent, String name );


	public ScannerUpdate scanLibrary ( Library library, ScannerControlMessage scm );


	public String fetchUserHome ();


	public int updateLibrary ( Library library );


	public int removeLibrary ( Library library );


	public Library addLibrary ( String path );


	public List<String> getCategoricalData ( Categorical categorical );


	public List<Track> queryForTracks ( List<Query> queryList );


	public List<String> queryForStrings ( List<Query> queryList );


	public List<Track> getTracks ( List<Integer> trackIds );


	public List<Configurable> getConfiguration ( int area );


	public int setConfiguration ( Configurable conf );
}
