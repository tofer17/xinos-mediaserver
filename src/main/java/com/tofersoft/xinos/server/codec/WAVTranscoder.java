package com.tofersoft.xinos.server.codec;


import java.util.ArrayList;
import java.util.List;

import com.tofersoft.xinos.server.AbstractTranscoder;


public class WAVTranscoder extends AbstractTranscoder {

	@Override
	public String getName () {
		return "WAV";
	}


	@Override
	public String getDescription () {
		return "WAV (WAV)";
	}


	@Override
	public String getMimeType () {
		return "audio/x-wav";
	}


	@Override
	public boolean isLossy () {
		return false;
	}


	@Override
	public List<String> getBitRates () {
		return new ArrayList<String>( DEFAULT_LOSSLESS_BITRATES );
	}


	@Override
	public int getDefaultBitRate () {
		return 0;
	}


	@Override
	public List<String> getCommandArguments ( String inputPath, String outputPath, int bitRateIndex ) {
		final List<String> args = new ArrayList<String>();

		args.add( "-i" );
		// args.add( "\"" + inputPath + "\"" );
		args.add( inputPath );

		args.add( "-codec:a" );
		args.add( "pcm_s16le" );

		args.add( "-f" );
		args.add( "wav" );

		// Ignore bitrate
		// args.add( "-b:a" );
		// args.add( DEFAULT_BITRATES.get( bitRateIndex ) + "k" );

		// args.add( "-strict" );
		// args.add( "experimental" );

		args.add( outputPath );

		return args;
	}


	@Override
	public String getFileExtension () {
		return "wav";
	}

}
