package com.tofersoft.xinos.client.ui;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tofersoft.xinos.client.XinosEntryPoint;


public class MenuItemUpload extends HorizontalPanel implements MenuComponentItem, MainContentComponent {

	private static final Logger logger = Logger.getLogger( MenuItemUpload.class.getSimpleName() );

	private Label header = new Label( "Upload" );
	private Label sub = new Label( "" );


	@Override
	protected void onLoad () {
		super.onLoad();

		this.getElement().setId( "uploader" );

		logger.finest( "Dumpy message so Eclipse won't warn about the logger being unused." );

		setWidth( "100%" );
		setHeight( "100%" );

		initPlupload();
	}

	// @formatter:off
	private static native void initPlupload () /*-{
		$wnd.$(function() {
			// Setup html5 version
			$wnd.$("#uploader").pluploadQueue({
				// General settings
				runtimes : 'html5,flash,silverlight,html4',
				url : "/examples/upload",
				chunk_size : '1mb',
				rename : true,
				dragdrop: true,

				filters : {

					// Maximum file size
					max_file_size : '1024mb',

					// Specify what files to browse for
					mime_types: [
						{title : "Audio files", extensions : "mp3,m4a,ogg,flac,m4p,mp4,wav,aif,aiff"},
						{title : "Zip files", extensions : "zip"}
					]
				},
	 
				// Resize images on clientside if we can
				resize: {
					width : 200,
					height : 200,
					quality : 90,
					crop: true // crop to exact dimensions
				},

				// Flash settings
				flash_swf_url : '/plupload/js/Moxie.swf',

				// Silverlight settings
				silverlight_xap_url : '/plupload/js/Moxie.xap'
			});
		});

	}-*/;
	// @formatter:on

	@Override
	public Widget getHeaderWidget () {
		return header;
	}


	@Override
	public Widget getSubHeaderWidget () {
		return sub;
	}


	@Override
	public MainContentComponent getContentComponent () {
		return this;
	}


	@Override
	public int getPreferredMenuPosition () {
		return 10;
	}


	@Override
	public Widget getContentWidget () {
		return this;
	}

}
