/**
 * 書誌情報管理
 */

-- レーベル情報
CREATE TABLE label(
	"name"	varchar(256) NOT NULL PRIMARY KEY,
	website	varchar(8192),
	note	varchar(8192),
	version	integer NOT NULL DEFAULT 0
);

-- 作者
CREATE TABLE author(
	"id"	serial	NOT NULL PRIMARY KEY,
	"name"	varchar(256)	NOT NULL,
	website	varchar(8192),
	twitter varchar(256),
	synonym_key	integer NOT NULL DEFAULT 0,
	note	varchar(8192),
	version	integer NOT NULL DEFAULT 0
);
CREATE INDEX idx_author_name ON author USING senna("name");

-- 書誌情報
CREATE TABLE bibliography (
	"id"	serial	NOT NULL PRIMARY KEY,
	isbn	varchar(16) NOT NULL,
	label	varchar(256)	NOT NULL REFERENCES label ON DELETE RESTRICT ON UPDATE CASCADE,
	"index"	char(1),
	title	varchar(8192) NOT NULL,
	subtitle	varchar(8192),
	description	varchar(8192),
	price	integer,
	publication_date date,
	note	varchar(8192),
	version	integer NOT NULL DEFAULT 0
);
CREATE INDEX idx_bibliography_isbn ON bibliography(isbn);
CREATE INDEX idx_bibliography_index ON bibliography("index");
CREATE INDEX idx_bibliography_title ON bibliography USING senna(title);
CREATE INDEX idx_bibliography_subtitle ON bibliography USING senna(subtitle);
CREATE INDEX idx_bibliography_description ON bibliography USING senna(description);
CREATE INDEX idx_publication_date ON bibliography(publication_date);

-- 書籍 - 著者
CREATE TABLE bib_author (
	"id"	bigserial	NOT NULL PRIMARY KEY,
	bibliography_id	integer	NOT NULL REFERENCES bibliography ON DELETE CASCADE,
	author_id	integer	NOT NULL REFERENCES author,
	role	varchar	NOT NULL
);

