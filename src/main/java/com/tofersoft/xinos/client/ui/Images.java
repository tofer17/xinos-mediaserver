package com.tofersoft.xinos.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public class Images {

	public static interface ImageClientBundle extends ClientBundle {

		public ImageResource logo ();


		public ImageResource loading ();


		public ImageResource folderClosed ();


		public ImageResource folderOpen ();


		public ImageResource previous ();


		public ImageResource next ();


		public ImageResource play ();


		public ImageResource pause ();


		public ImageResource playing ();


		public ImageResource paused ();


		public ImageResource volumeOn ();


		public ImageResource volumeOff ();


		public ImageResource repeatOne ();


		public ImageResource repeatAll ();


		public ImageResource repeatNone ();


		public ImageResource shuffleOn ();


		public ImageResource shuffleOff ();


		public ImageResource refresh ();


		public ImageResource zip ();


		public ImageResource playlist ();


		public ImageResource enqueue ();


		public ImageResource minus ();


		public ImageResource plus ();

	}

	private static ImageClientBundle _image = null;


	public static ImageClientBundle get () {

		if (_image == null)
			_image = GWT.create( ImageClientBundle.class );

		return _image;
	}

}
