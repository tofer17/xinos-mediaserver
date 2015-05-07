package com.tofersoft.xinos.shared.dm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Configurable implements Serializable {

	public enum Hint {
		NORMAL, TEXTAREA, SLIDER, SPINNER
	}

	public enum Type {
		STRING, INTEGER, LONG, DOUBLE, DATE, BOOLEAN
	}

	private static final long serialVersionUID = 3294096628591691613L;

	private String name = "";
	private String label = "";
	private String value = null;
	private List<String> options = new ArrayList<String>();
	private Type type = Type.STRING;
	private Hint hint = Hint.NORMAL;
	private boolean required = false;
	private boolean enabled = true;
	private int area = -1;


	public Configurable () {
		super();
	}


	public String getName () {
		return name;
	}


	public Configurable setName ( String name ) {
		this.name = name;
		return this;
	}


	public Configurable ( int area, String name, String value, List<String> options ) {
		super();
		setArea( area );
		setName( name );
		setLabel( name );
		setValue( value );
		if (options != null)
			setOptions( options );
	}


	public String getLabel () {
		return label;
	}


	public Configurable setLabel ( String label ) {
		this.label = label;
		return this;
	}


	public Type getType () {
		return type;
	}


	public Configurable setType ( Type type ) {
		this.type = type;
		return this;
	}


	public String getValue () {
		return value;
	}


	public Configurable setValue ( String value ) {
		this.value = value;
		return this;
	}


	public List<String> getOptions () {
		return new ArrayList<String>( options );
	}


	public Configurable setOptions ( List<String> options ) {
		this.options.clear();
		this.options.addAll( options );
		return this;
	}


	public Configurable addOptions ( List<String> options ) {
		this.options.addAll( options );
		return this;
	}


	public Configurable addOption ( String option ) {
		options.add( option );
		return this;
	}


	public Hint getHint () {
		return hint;
	}


	public Configurable setHint ( Hint hint ) {
		this.hint = hint;
		return this;
	}


	public boolean isRequired () {
		return required;
	}


	public Configurable setRequired ( boolean required ) {
		this.required = required;
		return this;
	}


	public boolean isEnabled () {
		return enabled;
	}


	public Configurable setEnabled ( boolean enabled ) {
		this.enabled = enabled;
		return this;
	}


	public int getArea () {
		return area;
	}


	public Configurable setArea ( int area ) {
		this.area = area;
		return this;
	}

}
