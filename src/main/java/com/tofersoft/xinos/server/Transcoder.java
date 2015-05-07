package com.tofersoft.xinos.server;


import java.util.List;


public interface Transcoder extends Comparable<Transcoder> {

	public boolean init ();


	public String getName ();


	public String getDescription ();


	public String getMimeType ();


	public boolean isLossy ();


	public List<String> getBitRates ();


	public int getDefaultBitRate ();


	public List<String> getCommandArguments ( String inputPath, String outputPath, int bitRateIndex );


	public int getBitRateIndex ( String bitRate );


	public String getFileExtension ();
}
