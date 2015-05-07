CREATE TABLE track (
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
			CONSTRAINT track_pk PRIMARY KEY (id),
	libraryId INTEGER NOT NULL DEFAULT 1,
			CONSTRAINT track_library_fk FOREIGN KEY (libraryId) REFERENCES library (id),
	filename VARCHAR (4096) NOT NULL UNIQUE,
	size BIGINT,
	millis BIGINT,
	format VARCHAR(32),
	channels INTEGER,
	bitRate INTEGER,
	sampleRate INTEGER,
	modified TIMESTAMP,
	cataloged TIMESTAMP,

	album VARCHAR(1024),
	albumArtist VARCHAR(1024),
	albumSort VARCHAR(1024),
	artist VARCHAR(1024),
	artistSort VARCHAR(1024),
	comment VARCHAR(1024),
	compilation BOOLEAN,
	composer VARCHAR(1024),
	copyright VARCHAR(1024),
	creationTime TIMESTAMP,
	date TIMESTAMP,
	discSeq INTEGER,
	discsCount INTEGER,
	encodedBy VARCHAR(1024),
	encoder VARCHAR(1024),
	genre VARCHAR(1024),
	language VARCHAR(1024),
	performer VARCHAR(1024),
	publisher VARCHAR(1024),
	title VARCHAR(1024),
	titleSort VARCHAR(1024),
	trackSeq INTEGER,
	tracksCount INTEGER
)
--@@@
CREATE INDEX idx_track_filename ON track (filename)

--@@@
CREATE INDEX idx_track_title ON track (title)
	

-- select * from track	
-- select * from track where filename = 'F2.mp4'
-- select * from SYS.SYSTABLES where SCHEMAID = '80000000-00d2-b38f-4cda-000a0a412c00'
-- select * from SYS.SYSSCHEMAS
-- select * from SYS.SYSFILES
-- insert into track (filename,title) values ('F2.mp4','File 2 Title')
-- drop table track
-- update track set libraryId = 1, trackSeq = 17 where id = 331