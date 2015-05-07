package com.tofersoft.xinos.client.rpc;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tofersoft.xinos.shared.dm.Categorical;
import com.tofersoft.xinos.shared.dm.Configurable;
import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.dm.Path;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;
import com.tofersoft.xinos.shared.scanner.ScannerControlMessage;
import com.tofersoft.xinos.shared.scanner.ScannerUpdate;


public interface CoreServiceAsync {

	public void getPathList ( String child, AsyncCallback<List<Path>> callback );


	public void getLibraries ( AsyncCallback<List<Library>> callback );


	public void getParents ( String childPath, AsyncCallback<List<String>> callback );


	public void mkdir ( String parent, String name, AsyncCallback<String> callback );


	public void scanLibrary ( Library library, ScannerControlMessage scm, AsyncCallback<ScannerUpdate> callback );


	public void fetchUserHome ( AsyncCallback<String> callback );


	public void updateLibrary ( Library library, AsyncCallback<Integer> callback );


	public void removeLibrary ( Library library, AsyncCallback<Integer> callback );


	public void addLibrary ( String path, AsyncCallback<Library> callback );


	public void getCategoricalData ( Categorical categorical, AsyncCallback<List<String>> callback );


	public void queryForTracks ( List<Query> queryList, AsyncCallback<List<Track>> callback );


	public void queryForStrings ( List<Query> queryList, AsyncCallback<List<String>> callback );


	public void getTracks ( List<Integer> trackIds, AsyncCallback<List<Track>> callback );


	public void getConfiguration ( int area, AsyncCallback<List<Configurable>> callback );


	public void setConfiguration ( Configurable conf, AsyncCallback<Integer> callback);
}
