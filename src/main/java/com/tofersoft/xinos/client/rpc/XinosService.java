package com.tofersoft.xinos.client.rpc;


import com.google.gwt.core.client.GWT;


public class XinosService {

	private static CoreServiceAsync coreService = null;


	public static CoreServiceAsync core () {
		if (coreService == null) {
			coreService = (CoreServiceAsync) GWT.create( CoreService.class );
		}

		return coreService;
	}

}
