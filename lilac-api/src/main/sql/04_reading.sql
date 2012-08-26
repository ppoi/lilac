/**
 * 読書記録管理
 */

-- 読書記録
CREATE TABLE reading_record (
	id serial NOT NULL PRIMARY KEY,
	bibliography_id integer NOT NULL REFERENCES bibliography,
	reader varchar(256) NOT NULL REFERENCES account ON DELETE CASCADE ON UPDATE CASCADE,
	start_date date,
	completion_date date,
	note varchar(8192)
);
CREATE INDEX idx_reading_record_reader ON reading_record(reader);
CREATE INDEX idx_reading_record_date ON reading_record(start_date, completion_date);
CREATE INDEX idx_reading_record_bibliography ON reading_record(bibliography_id);
