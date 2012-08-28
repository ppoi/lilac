/**
 * 共通定義
 */

-- アカウント情報
CREATE TABLE account(
	username varchar(256) NOT NULL PRIMARY KEY,
	realname varchar(256),
	email_address varchar(256),
	library_name varchar(256),
	version integer NOT NULL DEFAULT 0,
	note varchar(8192)
);

-- 認証情報
CREATE TABLE user_auth(
	username varchar(256) NOT NULL PRIMARY KEY REFERENCES account ON DELETE CASCADE,
	password varchar(256)
);
CREATE INDEX idx_user_auth_password ON user_auth(password);

-- 認証済みセッション情報
CREATE TABLE user_session(
	id varchar(256) NOT NULL PRIMARY KEY,
	username varchar(256) NOT NULL REFERENCES account ON DELETE CASCADE,
	created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インポートファイル
CREATE TABLE import_file(
	id	serial NOT NULL PRIMARY KEY,
	username varchar(256) NOT NULL REFERENCES account ON DELETE CASCADE,
	file_name varchar(256) NOT NULL,
	created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	note varchar(8192)
);
CREATE INDEX idx_import_file_user ON import_file(username);
CREATE INDEX idx_import_file_timestamp ON import_file(created_timestamp);

