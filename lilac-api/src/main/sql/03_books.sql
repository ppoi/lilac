/**
 * 蔵書管理
 */

-- 書棚
CREATE TABLE bookshelf(
	id	serial	NOT NULL PRIMARY KEY,
	label	varchar(256)	NOT NULL,
	owner	varchar(256)	NOT NULL REFERENCES "user" ON DELETE CASCADE ON UPDATE CASCADE
);

-- 蔵書
CREATE TABLE book(
	id	serial	NOT NULL PRIMARY KEY,
	bibliography_id	integer	NOT NULL REFERENCES bibliography,
	acquisition_date	date,
	purchase_shop	varchar(256),
	location_id	integer	REFERENCES bookshelf,
	owner	varchar(256)	NOT NULL REFERENCES "user" ON DELETE CASCADE ON UPDATE CASCADE,
	note varchar(8192)
);
