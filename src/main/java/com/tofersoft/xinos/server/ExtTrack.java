package com.tofersoft.xinos.server;


import com.tofersoft.xinos.shared.dm.Track;


public class ExtTrack extends Track {

	private static final long serialVersionUID = -6632879738586101364L;

	protected double confidence = 0.0d;

	protected Throwable thrown = null;

	protected long millisToDecode = -1;


	public ExtTrack () {
		super();
	}


	public ExtTrack ( double confidence, Throwable thrown, long millisToDecode ) {
		super();
		this.confidence = confidence;
		this.thrown = thrown;
		this.millisToDecode = millisToDecode;
	}


	public double getConfidence () {
		return confidence;
	}


	public void setConfidence ( double confidence ) {
		this.confidence = confidence;
	}


	public Throwable getThrown () {
		return thrown;
	}


	public void setThrown ( Throwable thrown ) {
		this.thrown = thrown;
	}


	public long getMillisToDecode () {
		return millisToDecode;
	}


	public void setMillisToDecode ( long millisToDecode ) {
		this.millisToDecode = millisToDecode;
	}


	@Override
	public String debug () {
		return super.debug() + " in " + millisToDecode + "ms @ " + confidence + " & with" + (thrown == null ? "out" : " an") + " exception.";
	}
}
