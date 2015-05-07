package com.tofersoft.xinos.server;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tofersoft.xinos.shared.dm.Library;
import com.tofersoft.xinos.shared.scanner.ScannerControlMessage;
import com.tofersoft.xinos.shared.scanner.ScannerStatusMessage;
import com.tofersoft.xinos.shared.scanner.ScannerUpdate;


public class PathScanner {

	private static final Logger logger = LoggerFactory.getLogger( PathScanner.class );

	private static PathScanner instance = null;

	private List<ScannerWorker> workers;


	private PathScanner () {
		workers = new ArrayList<ScannerWorker>();
		logger.info( "Path Scanner has intialized." );
	}


	private ScannerWorker getWorker ( Library library ) {
		ScannerWorker worker = null;

		for (ScannerWorker sw : workers) {
			if (sw.getLibrary().getPath().equals( library.getPath() ))
				return sw;
		}

		worker = new ScannerWorker( library );
		workers.add( worker );

		logger.info( "Created a new ScannerWorker for '{}'", library );
		return worker;
	}


	public static ScannerUpdate scan ( Library library, ScannerControlMessage scm ) {
		if (instance == null) {
			instance = new PathScanner();
		}

		ScannerWorker worker = instance.getWorker( library );

		ScannerUpdate sup;
		if (worker.presentStatus == ScannerStatusMessage.SCANNING && scm == ScannerControlMessage.START) {
			sup = worker.update( ScannerControlMessage.PAUSE );
		} else {
			sup = worker.update( scm );
		}

		if (sup.status == ScannerStatusMessage.FINISHED) {
			instance.workers.remove( worker );
		}

		return sup;
	}


	public static void shutdown () {
		if (instance == null) {
			logger.info( "PathScanner is not intialized so there is nothing to shutdown (which is just fine)." );
		} else {
			if (instance.workers.size() == 0) {
				logger.info( "Path Scanner has no workers to shutdown; shutdown complete." );
				return;
			}
			logger.info( "Path Scanner will shutdown {} workers.", instance.workers.size() );
			for (ScannerWorker worker : instance.workers) {
				logger.info( "Shutting down scanner for '{}'...", worker.getLibrary() );
				worker.shutdown();
				logger.info( "...Scanner for '{}' has been shutdown.", worker.getLibrary() );
			}
		}

		instance.workers.clear();
		logger.info( "Path Scanner has completed shutdown of all workers." );
	}

	private class ScannerWorker extends Thread {

		private String id;
		private final Library library;
		private long filesScanned = 0;
		private long totalFiles = -1;
		private long totalDirectories = -1;
		private boolean running = false;
		private ScannerStatusMessage presentStatus = ScannerStatusMessage.STARTING;


		public ScannerWorker ( Library library ) {
			this.library = library;
			id = "SW:" + Integer.toString( hashCode(), 16 );
		}


		public Library getLibrary () {
			return library;
		}


		public void shutdown () {
			running = false;
			interrupt();
		}


		public ScannerUpdate update ( ScannerControlMessage scm ) {
			ScannerUpdate sup = null;

			switch (scm) {
			case START:
				logger.info( "Starting for {}", library );
				sup = new ScannerUpdate( id, library, "Starting...", filesScanned, totalFiles, totalDirectories, presentStatus );
				start();
				break;
			case STOP:
				logger.info( "stopping for {}", library );
				running = false;
				sup = new ScannerUpdate( id, library, "Stopping...", filesScanned, totalFiles, totalDirectories, presentStatus );
				break;
			case PAUSE:
				logger.info( "pasuing for {}", id );
				running = false;
				presentStatus = ScannerStatusMessage.PAUSED;
				break;
			case UPDATE:
				logger.trace( "Updating for {}", library );
				sup = new ScannerUpdate( id, library, "Scanning...", filesScanned, totalFiles, totalDirectories, presentStatus );
				break;

			}

			logger.trace( sup.message );
			return sup;
		}


		@Override
		public void run () {

			running = true;

			try {

				// TODO: Ought to break this into 2 threads: one that flufs, and
				// the other that scans.
				// Fluffing is only interesting to determine percent complete
				// and there is no reason not
				// to begin actually scanning prior to knowing percent complete.
				if (presentStatus == ScannerStatusMessage.STARTING) {
					presentStatus = ScannerStatusMessage.FLUFFING;
					totalFiles = 0;
					totalDirectories = 0;
					getFileCounts( new File( library.getPath() ) );
				}
				logger.info( "TF is " + totalFiles );

				presentStatus = ScannerStatusMessage.SCANNING;

				scanPath( new File( library.getPath() ) );

			} catch (Exception e) {
				e.printStackTrace();
			}

			running = false;
			if (presentStatus != ScannerStatusMessage.PAUSED) {
				presentStatus = ScannerStatusMessage.FINISHED;

				logger.info( "All done." );

			}

		}


		private void getFileCounts ( File path ) {
			logger.info( "gfc " + path.getAbsolutePath() );

			File a = path;

			try {
				for (File f : path.listFiles()) {
					a = f;
					if (!running)
						break;
					if (f.isDirectory()) {
						totalDirectories++;
						getFileCounts( f );
					} else {
						totalFiles++;
					}
				}
			} catch (Throwable t) {
				logger.error( "There was a problem delving into '" + a + "'; it will not be counted.", t );
			}

		}


		private void scanPath ( File path ) {

			for (File f : path.listFiles()) {

				if (f.isDirectory()) {
					scanPath( f );
				} else {
					try {

						ExtTrack track = DecoderMaster.catalogFile( f );

						if (track.getThrown() == null && track.getConfidence() > 0.0d) {

							track.setLibrary( library );

							int result = XinosCore.saveTrack( track );

							logger.debug( track.toString() + "=>" + (result == 1 ? "SAVED" : "ERRED") );

						} else if (track.getThrown() == null) {
							logger.debug( "No decoder for '{}'.", f.getAbsolutePath() );
						} else {
							logger.warn( track.debug(), track.getThrown() );
						}
					} catch (Throwable t) {
						logger.warn( "There was an issue with '" + f.getAbsolutePath() + "'; skipping.", t );
					}

					filesScanned++;
				}
			}

		}

	}

}
