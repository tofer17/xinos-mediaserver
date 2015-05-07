package com.tofersoft.xinos.client.ui;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.tofersoft.xinos.shared.dm.Track;


public class TrackGroupingView extends GroupingView<Track> {

	private static final Logger logger = Logger.getLogger( TrackGroupingView.class.getSimpleName() );

	private int k = -1;

	public static final long[] MILLI_INTS = { 0, 60000, 120000, 180000, 240000, 300000, 360000, 420000, 480000, 540000, 600000, 720000, 900000,
			1200000, 1800000, 3600000, 7200000, 10800000, 14400000, 18000000 };
	public static final String[] MILLI_LABLS = { "<0:01:00", "0:01:00 - 0:01:59", "0:02:00 - 0:02:59", "0:03:00 - 0:03:59", "0:04:00 - 0:04:59",
			"0:05:00 - 0:05:59", "0:06:00 - 0:06:59", "0:07:00 - 0:07:59", "0:08:00 - 0:08:59", "0:09:00 - 0:09:59", "0:10:00 - 0:11:59",
			"0:12:00 - 0:19:59", "0:20:00 - 0:29:59", "0:30:00 - 0:44:59", "0:45:00 - 0:59:59", "1:00:00 - 1:59:59", "2:00:00 - 2:59:59",
			"3:00:00 - 3:59:59", "4:00:00 - 4:59:59", "5:00:00 +" };

	public static final long[] BITRATE_INTS = { 0, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000,
			224000, 256000, 320000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000 };
	public static final String[] BITRATE_LABLS = { "<16 kbps", "16 kbps - 23 kbps", "24 kbps - 31 kbps", "32 kbps - 39 kbps", "40 kbps - 47 kbps",
			"48 kbps - 55 kbps", "56 kbps - 63 kbps", "64 kbps - 79 kbps", "80 kbps - 95 kbps", "96 kbps - 111 kbps", "112 kbps - 127 kbps",
			"128 kbps - 159 kbps", "160 kbps - 191 kbps", "192 kbps - 223 kbps", "224 kbps - 255 kbps", "256 kbps - 319 kbps", "320 kbps - 399 kbps",
			"400 kbps - 499 kbps", "500 kbps - 599 kbps", "600 kbps - 699 kbps", "700 kbps - 799 kbps", "800 kbps - 899 kbps", "900 kbps - 999 kbps",
			"1000+ kbps" };

	public static final long[] SIZE_INTS = { 0, 1048576, 2097152, 3145728, 4194304, 5242880, 6291456, 7340032, 8388608, 9437184, 10485760, 12582912,
			15728640, 20971520, 26214400, 31457280, 36700160, 41943040, 47185920, 52428800, 57671680, 62914560 };
	public static final String[] SIZE_LABLS = { "<1 mb", "1 mb - 1.9 mb", "2 mb - 2.9 mb", "3 mb - 3.9 mb", "4 mb - 4.9 mb", "5 mb - 5.9 mb",
			"6 mb - 6.9 mb", "7 mb - 7.9 mb", "8 mb - 8.9 mb", "9 mb - 9.9 mb", "10 mb - 11.9 mb", "12 mb - 14.9 mb", "15 mb - 19.9 mb",
			"20 mb - 24.9 mb", "25 mb - 29.9 mb", "30 mb - 34.9 mb", "35 mb - 39.9 mb", "40 mb - 44.9 mb", "45 mb - 49.9 mb", "50 mb - 54.9 mb",
			"55 mb - 59.9 mb", "60+ mb" };

	public static final long[] TIME_INTS = { 0l, 3600000l, 21600000l, 43200000l, 86400000l, 172800000l, 604800000l, 1209600000l, 2592000000l,
			5184000000l, 15552000000l, 31536000000l, 63072000000l, 157680000000l, 315360000000l };
	public static final String[] TIME_LABLS = { "Within the hour", "Within 6 hours", "Within 12 hours", "Within 1 day", "Within 2 days",
			"Within the week", "Within 2 weeks", "Within 1 month", "Within 2 months", "Within 6 months", "Within 1 year", "Within 2 years",
			"Within 5 years", "Within 10 years", "10+ years" };

	private static final String[] PATHS = {

	"bitRate", "millis", "size", "created", "modified"

	// "tag.album", "tag.albumArtist", "tag.artist", "bitRate",
	// "channels", "tag.comment", "tag.compilation", "tag.composer",
	// "tag.copyright", "created", "tag.creationTime", "tag.date",
	// "discSeq", "millis", "tag.encoder", "filename", "format",
	// "tag.genre", "tag.grouping", "modified", "tag.publisher",
	// "sampleRate", "sequence", "size", "title", "trackSeq"
	};


	@Override
	public void groupBy ( ColumnConfig<Track, ?> column ) {
		if (column == null) {
			k = -1;
		} else {
			figureOutK( column.getPath() );
		}
		super.groupBy( column );
	}


	private int getInterval ( long v, long[] ints ) {

		int res = -1;

		for (int i = 0; i < ints.length - 1 && res < 0; i++) {
			if (v >= ints[i] && v < ints[i + 1])
				res = i;
		}

		return res < 0 ? (ints.length - 1) : res;
	}


	private void figureOutK ( String path ) {
		k = -1;
		for (int i = 0; i < PATHS.length && k < 0; i++) {
			if (PATHS[i].equals( path ))
				k = i;
		}
	}


	@Override
	protected List<GroupingData<Track>> createGroupingData ( List<Track> rows, int startGroupIndex ) {

		List<GroupingData<Track>> groups = new ArrayList<GroupingData<Track>>();

		final String path = groupingColumn.getPath();
		logger.info( "Creating grouping data for '" + path + "' (" + k + ")" );

		// iterate through each item, creating a new group as needed. Assumes
		// the
		// list is sorted
		GroupingData<Track> curGroup = null;
		for (int j = 0; j < rows.size(); j++) {
			Track model = rows.get( j );

			int rowIndex = (j + startGroupIndex);

			// the value for the group field
			final Object gvalue;
			if (ds.hasRecord( model )) {
				gvalue = ds.getRecord( model ).getValue( groupingColumn.getValueProvider() );
			} else {
				gvalue = groupingColumn.getValueProvider().getValue( model );
			}

			if (curGroup == null || !valueBelongsInGroup( curGroup, gvalue )) {
				curGroup = makeGroupForRow( rowIndex, model, gvalue );
				verifyNewGroup( groups, curGroup );
				groups.add( curGroup );

			} else {
				curGroup.getItems().add( model );
			}
		}
		return groups;
	}


	@Override
	protected SafeHtml renderGroupHeader ( GroupingData<Track> groupInfo ) {

		long v = 0;
		String s = "?";
		switch (k) {
		case 0: // bitRate Integer
			v = new Long( (Integer) groupInfo.getValue() ).longValue();
			s = BITRATE_LABLS[getInterval( v, BITRATE_INTS )];
			return new SafeHtmlBuilder().appendEscaped( s ).toSafeHtml();
		case 1: // millis Long
			s = MILLI_LABLS[getInterval( (Long) groupInfo.getValue(), MILLI_INTS )];
			return new SafeHtmlBuilder().appendEscaped( s ).toSafeHtml();
		case 2: // size Long
			s = SIZE_LABLS[getInterval( (Long) groupInfo.getValue(), SIZE_INTS )];
			return new SafeHtmlBuilder().appendEscaped( s ).toSafeHtml();
		case 3: // created Date
		case 4: // modified Date
			final Date d = (Date) groupInfo.getValue();
			v = d.getTime();
			s = TIME_LABLS[getInterval( System.currentTimeMillis() - v, TIME_INTS )];
			return new SafeHtmlBuilder().appendEscaped( s ).toSafeHtml();

		}

		return super.renderGroupHeader( groupInfo );

	}


	@Override
	protected boolean valueBelongsInGroup ( GroupingData<Track> group, Object value ) {

		long g = 0;
		long v = 0;

		switch (k) {
		case 0: // bitRate Integer
			g = new Long( (Integer) group.getValue() ).longValue();
			v = new Long( (Integer) value ).longValue();
			return getInterval( g, BITRATE_INTS ) == getInterval( v, BITRATE_INTS );
		case 1: // millis Long
			g = (Long) group.getValue();
			v = (Long) value;
			return getInterval( g, MILLI_INTS ) == getInterval( v, MILLI_INTS );
		case 2: // size Long
			g = (Long) group.getValue();
			v = (Long) value;
			return getInterval( g, SIZE_INTS ) == getInterval( v, SIZE_INTS );
		case 3: // created Date
		case 4: // modified Date
			final Date d1 = (Date) group.getValue();
			final Date d2 = (Date) value;
			g = System.currentTimeMillis() - d1.getTime();
			v = System.currentTimeMillis() - d2.getTime();
			return getInterval( g, TIME_INTS ) == getInterval( v, TIME_INTS );

		default:
			return super.valueBelongsInGroup( group, value );
		}
	}

}
