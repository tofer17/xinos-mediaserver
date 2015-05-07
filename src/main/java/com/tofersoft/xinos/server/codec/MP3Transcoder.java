package com.tofersoft.xinos.server.codec;


import java.util.ArrayList;
import java.util.List;

import com.tofersoft.xinos.server.AbstractTranscoder;


public class MP3Transcoder extends AbstractTranscoder {

	@Override
	public String getName () {
		return "MP3";
	}


	@Override
	public String getDescription () {
		return "Moving Pictures Expert Group (MP3)";
	}


	@Override
	public String getMimeType () {
		return "audio/mpeg";
	}


	@Override
	public boolean isLossy () {
		return true;
	}


	@Override
	public List<String> getCommandArguments ( String inputPath, String outputPath, int bitRateIndex ) {
		final List<String> args = new ArrayList<String>();

		args.add( "-i" );
		args.add( inputPath );

		args.add( "-codec:a" );
		args.add( "libmp3lame" );

		args.add( "-f" );
		args.add( "mp3" );

		args.add( "-b:a" );
		args.add( DEFAULT_LOSSY_BITRATES.get( bitRateIndex ) + "k" );

		args.add( outputPath );

		return args;
	}


	@Override
	public String getFileExtension () {
		return "mp3";
	}

}
