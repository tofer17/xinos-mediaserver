package com.tofersoft.xinos.server.codec;


import java.util.ArrayList;
import java.util.List;

import com.tofersoft.xinos.server.AbstractTranscoder;


public class OGGTranscoder extends AbstractTranscoder {

	@Override
	public String getName () {
		return "OGG";
	}


	@Override
	public String getDescription () {
		return "Vorbis/OGG (OGG)";
	}


	@Override
	public String getMimeType () {
		return "application/ogg";
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
		args.add( "libvorbis" );

		args.add( "-f" );
		args.add( "ogg" );

		args.add( "-b:a" );
		args.add( DEFAULT_LOSSY_BITRATES.get( bitRateIndex ) + "k" );

		args.add( "-strict" );
		args.add( "experimental" );

		args.add( outputPath );

		return args;
	}


	@Override
	public String getFileExtension () {
		return "ogg";
	}

}
