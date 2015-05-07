package com.tofersoft.xinos.shared.dm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Query implements Serializable {

	private static final long serialVersionUID = 473265454473729666L;

	private Categorical categorical;

	private List<String> values;


	public Query () {
		this( null, null );
	}


	public Query ( Categorical categorical, List<String> values ) {
		this.categorical = categorical;
		this.values = values != null ? new ArrayList<String>( values ) : null;
	}


	public Categorical getCategorical () {
		return categorical;
	}


	public void setCategorical ( Categorical categorical ) {
		this.categorical = categorical;
	}


	public List<String> getValues () {
		return values == null ? null : new ArrayList<String>( values );
	}


	public void setValues ( List<String> values ) {
		if (this.values != null) {
			this.values.clear();
		}
		if (values != null) {
			if (this.values == null) {
				this.values = new ArrayList<String>( values );
			} else {
				this.values.addAll( values );
			}
		}
	}

}
