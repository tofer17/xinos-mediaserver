CREATE TABLE configuration (
	name VARCHAR (32) NOT NULL,
		CONSTRAINT configuration_pk PRIMARY KEY (name),
	val VARCHAR (128)
)

-- drop table configuration