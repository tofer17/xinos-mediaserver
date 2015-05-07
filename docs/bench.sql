/*
SELECT DISTINCT
	valString
FROM
	(
	SELECT  
		*
	FROM
		tag
	WHERE
		( name = 'genre' AND valString IN ('Soundtrack') ) OR ( name = 'artist' AND valString = 'Moby' )
	) AS a
WHERE
	name = 'artist' AND valString = 'Moby'


SELECT
	a.*, b.*
FROM
	track AS a, tag AS b
WHERE
	a.id = b.trackId
	AND ( name = 'genre' AND valString IN ('Soundtrack') ) 
	OR ( name = 'artist' AND valString = 'Moby' )


select 
	id, title 
from 
	track 
where 
	id in ( 
		select 
			trackId 
		from 
			tag 
		where 
			(name = 'artist' and valstring in ( 'Moby' ) ) 
		)

-- THIS WORKS
select * from (
select
	tr.*,
	ge.valString as "GENRE",
	ar.valString as "ARTIST",
	al.valString as "ALBUM"
from
	track as tr,
	tag as ge,
	tag as ar,
	tag as al
where 1 = 1
	and ( tr.id = ge.trackId and ge.name = 'genre' )
	and ( tr.id = ar.trackid and ar.name = 'artist' )
	and ( tr.id = al.trackId and al.name = 'album' )
) as a 
where 1 = 1
	and ( a.GENRE in ( 'Soundtrack', 'Trance', 'Electronic' ) )
	and ( a.ARTIST in ( 'Moby', 'Aphex Twin' ) )
	and ( a.ALBUM in ( 'Syro', 'Bourne Supremacy' ) )
*/

select c.* from (
select
	a.id as "ID",
	max( a.libraryId ) as "LIBRARYID",
	max( a.fileName ) as "FILENAME",
	max( a.title ) as "TITLE",
	max( a.size ) as "SIZE",
	max( a.millis ) as "MILLIS",
	max( a.formatId ) as "FORMATID",
	max( a.channels ) as "CHANNELS",
	max( a.bps ) as "BPS",
	max( a.sampleRate ) as "SAMPLERATE",
	max( a.created ) as "CREATED",
	max( a.modified ) as "MODIFIED",
	max( case when a.discSeq is null then 0 else a.discSeq end ) as "DISCSEQ",
	max( case when a.trackSeq is null then 0 else a.trackSeq end ) as "TRACKSEQ",
	
	max( case when b.name = 'album' then b.valString else null end ) as "ALBUM",
	max( case when b.name = 'album_artist' then b.valString else null end ) as "ALBUM_ARTIST",
	max( case when b.name = 'artist' then b.valString else null end ) as "ARTIST",
	max( case when b.name = 'compilation' then b.valString else null end ) as "COMPILATION",
	max( case when b.name = 'copmoser' then b.valString else null end ) as "COPMOSER",
	max( case when b.name = 'copyright' then b.valString else null end ) as "COPYRIGHT",
	max( case when b.name = 'creation_time' then b.valString else null end ) as "CREATION_TIME",
	max( case when b.name = 'date' then b.valString else null end ) as "DATE",
	max( case when b.name = 'encoder' then b.valString else null end ) as "ENCODER",
	max( case when b.name = 'genre' then b.valString else null end ) as "GENRE",
	max( case when b.name = 'publisher' then b.valString else null end ) as "PUBLISHER"

from
	track as a
	left outer join
		tag as b
		on a.id = b.trackId
--where true
--	and ( a.id = b.trackId )
group by
	a.id
) as c
where
	c.id > 10

-- select distinct name from tag order by name
-- select * from track where id > 330

/*
select * from taggedTracks where
	( genre in ('Trance', 'Soundtrack') )
and ( artist in ( true, 'Moby') )
and ( album in (true) )
*/

