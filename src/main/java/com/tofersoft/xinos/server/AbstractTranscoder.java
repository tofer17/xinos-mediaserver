package com.tofersoft.xinos.server;


import java.util.ArrayList;
import java.util.List;


public abstract class AbstractTranscoder implements Transcoder {

	protected static final List<String> DEFAULT_LOSSY_BITRATES = new ArrayList<String>( 17 );
	protected static final List<String> DEFAULT_LOSSLESS_BITRATES = new ArrayList<String>( 1 );

	protected static final int DEFAULT_BITRATE_INDEX = 10;

	static {
		DEFAULT_LOSSY_BITRATES.add( "16" );
		DEFAULT_LOSSY_BITRATES.add( "24" );
		DEFAULT_LOSSY_BITRATES.add( "32" );
		DEFAULT_LOSSY_BITRATES.add( "40" );
		DEFAULT_LOSSY_BITRATES.add( "48" );
		DEFAULT_LOSSY_BITRATES.add( "56" );
		DEFAULT_LOSSY_BITRATES.add( "64" );
		DEFAULT_LOSSY_BITRATES.add( "80" );
		DEFAULT_LOSSY_BITRATES.add( "96" );
		DEFAULT_LOSSY_BITRATES.add( "112" );
		DEFAULT_LOSSY_BITRATES.add( "128" );
		DEFAULT_LOSSY_BITRATES.add( "160" );
		DEFAULT_LOSSY_BITRATES.add( "192" );
		DEFAULT_LOSSY_BITRATES.add( "224" );
		DEFAULT_LOSSY_BITRATES.add( "256" );
		DEFAULT_LOSSY_BITRATES.add( "288" );
		DEFAULT_LOSSY_BITRATES.add( "320" );

		DEFAULT_LOSSLESS_BITRATES.add( "1441" );
	}


	@Override
	public int compareTo ( Transcoder o ) {

		// I always sort ahead of nulls.
		if (o == null) {
			return 1;
		}

		boolean me = this.isLossy();
		boolean it = o.isLossy();

		// I sort ahead of not-lossy (lossless)
		if (me && !it) {
			return -1;
		}

		// It's lossy and I am not-lossy (lossless) so I sort lower.
		if (!me && it) {
			return 1;
		}

		// We're the same so sort by name.
		return this.getName().compareTo( o.getName() );
	}


	@Override
	public boolean init () {
		return true;
	}


	@Override
	public List<String> getBitRates () {
		return new ArrayList<String>( DEFAULT_LOSSY_BITRATES );
	}


	@Override
	public int getDefaultBitRate () {
		return DEFAULT_BITRATE_INDEX;
	}


	@Override
	public int getBitRateIndex ( String bitRate ) {

		for (int i = 0; i < DEFAULT_LOSSY_BITRATES.size(); i++) {
			if (DEFAULT_LOSSY_BITRATES.get( i ).equals( bitRate )) {
				return i;
			}
		}

		return DEFAULT_BITRATE_INDEX;
	}


	@Override
	public String toString () {
		return getName();
	}

}
