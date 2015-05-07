package com.tofersoft.xinos.client.ui;


import com.google.gwt.user.client.ui.Widget;


public interface MenuComponentItem {

	/**
	 * The "header" that is displayed for the user to click on to activate.
	 *
	 * @return
	 */
	public Widget getHeaderWidget ();


	/**
	 * The widget beneath the header (mostly stupid); not really important.
	 *
	 * @return
	 */
	public Widget getSubHeaderWidget ();


	/**
	 * The Component for the main-content area.
	 *
	 * @return
	 */
	public MainContentComponent getContentComponent ();


	/**
	 * An "ordinal" that dictates the ordering of the menu component in the main
	 * menu; lower values first and higher values later.
	 *
	 * @return
	 */
	public int getPreferredMenuPosition ();

}
