package com.tofersoft.xinos.client.ui;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tofersoft.xinos.client.rpc.XinosService;
import com.tofersoft.xinos.shared.dm.Configurable;


public class MenuItemAbout extends TabLayoutPanel implements MenuComponentItem, MainContentComponent {

	private static final Logger logger = Logger.getLogger( MenuItemAbout.class.getSimpleName() );

	private Label header = new Label( "About" );
	private Label sub = new Label( "" );

	private VerticalLayoutContainer me;
	private VerticalLayoutContainer you;
	private VerticalLayoutContainer others;


	public MenuItemAbout () {
		super( 1.5d, Unit.EM );
	}


	@Override
	protected void onLoad () {
		super.onLoad();

		me = new VerticalLayoutContainer();
		you = new VerticalLayoutContainer();
		others = new VerticalLayoutContainer();

		loadMe();

		this.add( me, "Me" );
		this.add( you, "You" );
		this.add( others, "Others" );

		this.onResize();
	}


	private void loadMe () {
		AsyncCallback<List<Configurable>> callback = new AsyncCallback<List<Configurable>>() {

			@Override
			public void onSuccess ( List<Configurable> results ) {
				setupMe( results );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "HOLLY MOLLY ME", caught );
			}
		};

		XinosService.core().getConfiguration( 0, callback );

	}


	private void updateConfiguration ( final Configurable conf ) {
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			@Override
			public void onSuccess ( Integer result ) {
				logger.log( Level.SEVERE, "The configuration was updated: '" + conf.getLabel() + "'" );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "Gnarly!", caught );
			}
		};

		XinosService.core().setConfiguration( conf, callback );
	}


	private void addConfsTo ( List<Configurable> confs, VerticalLayoutContainer vlc ) {
		if (confs.isEmpty())
			return;

		for (Configurable conf : confs) {

			Configurable.Type c = conf.getType();

			if (c == Configurable.Type.STRING) {
				vlc.add( new FieldLabel( createStringField( conf ), conf.getLabel() ), new VerticalLayoutData( 1, -1 ) );
			} else {
				logger.info( "Unknown configurable type...'" + c + "'" );
			}

		}
	}


	private Widget createStringField ( final Configurable conf ) {
		TextField tf = new TextField();
		tf.setValue( "" + conf.getValue() );
		tf.setEnabled( conf.isEnabled() );
		tf.setAllowBlank( !conf.isRequired() );
		tf.addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange ( ValueChangeEvent<String> event ) {
				updateConfiguration( conf.setValue( event.getValue() ) );
			}
		} );

		return tf;
	}


	private void setupMe ( List<Configurable> confs ) {
		loadYou();

		addConfsTo( confs, me );
	}


	private void loadYou () {
		AsyncCallback<List<Configurable>> callback = new AsyncCallback<List<Configurable>>() {

			@Override
			public void onSuccess ( List<Configurable> results ) {
				setupYou( results );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "HOLLY MOLLY YOU", caught );
			}
		};

		XinosService.core().getConfiguration( 1, callback );

	}


	private void setupYou ( List<Configurable> confs ) {
		loadOthers();

		addConfsTo( confs, you );
		you.add( new Label( "...you..." ) );
	}


	private void loadOthers () {
		AsyncCallback<List<Configurable>> callback = new AsyncCallback<List<Configurable>>() {

			@Override
			public void onSuccess ( List<Configurable> results ) {
				setupOthers( results );
			}


			@Override
			public void onFailure ( Throwable caught ) {
				logger.log( Level.SEVERE, "HOLLY MOLLY OTHERS", caught );
			}
		};

		XinosService.core().getConfiguration( 2, callback );

	}


	private void setupOthers ( List<Configurable> confs ) {
		addConfsTo( confs, others );
		others.add( new Label( "...you..." ) );
	}


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
		return 0;
	}


	@Override
	public Widget getContentWidget () {
		return this;
	}

}
