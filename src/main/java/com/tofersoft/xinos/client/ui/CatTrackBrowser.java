package com.tofersoft.xinos.client.ui;


import java.util.List;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.tofersoft.xinos.shared.dm.Query;
import com.tofersoft.xinos.shared.dm.Track;


public interface CatTrackBrowser {

	public void setParentBrowser ( CatTrackBrowser parent );


	public void setChildBrowser ( CatTrackBrowser child );


	public void onParentSelectionChanged ();


	public List<Query> getQueryList ();


	public List<ListFilter<Track, ?>> getFilters ();

}
