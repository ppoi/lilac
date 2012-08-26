/**
 * 蔵書情報
 */

-- 書棚
CREATE TABLE bookshelf(
	id serial NOT NULL PRIMARY KEY,
	label varchar(256) NOT NULL,
	owner varchar(256) NOT NULL REFERENCES account ON DELETE CASCADE ON UPDATE CASCADE,
	note varchar(8192)
);
CREATE INDEX idx_bookshelf_owner ON bookshelf(owner);

-- 蔵書情報
CREATE TABLE book(
	id serial NOT NULL PRIMARY KEY,
	bibliography_id integer	NOT NULL REFERENCES bibliography,
	acquisition_date date,
	purchase_shop varchar(256),
	location_id	integer REFERENCES bookshelf ON DELETE SET NULL,
	owner varchar(256) NOT NULL REFERENCES account ON DELETE CASCADE ON UPDATE CASCADE,
	note varchar(8192)
);
CREATE INDEX idx_book_owner ON book(owner);
CREATE INDEX idx_book_bibliography ON book(bibliography_id);
CREATE INDEX idx_book_location ON book(location_id);
CREATE INDEX idx_book_date ON book(acquisition_date);
