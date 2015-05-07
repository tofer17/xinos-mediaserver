CREATE TABLE library (
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
		CONSTRAINT library_pk PRIMARY KEY (id),
	path VARCHAR (4096) NOT NULL UNIQUE,
	lastScanned TIMESTAMP,
	tracks INTEGER NOT NULL DEFAULT 0
)

--@@@
CREATE INDEX idx_library_path ON library (path)

--@@@
INSERT INTO library (path) VALUES ('/')

-- select * from library
-- drop table library
-- delete from library where id > 1