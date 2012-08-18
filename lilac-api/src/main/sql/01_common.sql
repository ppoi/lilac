/**
 * 共通
 */

-- ユーザー
CREATE TABLE "user"(
	"id"	varchar(256)	NOT NULL PRIMARY KEY,
	realname	varchar(256),
	email_address	varchar(256),
	library_name	varchar(256),
	note	varchar(8192)
);

-- ロール
CREATE TABLE user_role(
	"user"	varchar(256) NOT NULL REFERENCES "user" ON DELETE CASCADE,
	role	varchar(256) NOT NULL,
	PRIMARY KEY ("user", role)
);

-- 認証情報
CREATE TABLE user_auth(
	"user"	varchar(256) NOT NULL PRIMARY KEY REFERENCES "user" ON DELETE CASCADE,
	password varchar(256) NOT NULL
);

-- 認証済みセッション情報
CREATE TABLE user_session(
	"id"	varchar(256) NOT NULL PRIMARY KEY,
	"user" varchar(256) NOT NULL REFERENCES "user" ON DELETE CASCADE,
	created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- インポートファイル
CREATE TABLE import_file(
	"id"	serial NOT NULL PRIMARY KEY,
	"user"	varchar(256) NOT NULL REFERENCES "user" ON DELETE CASCADE,
	file_name	varchar(256) NOT NULL,
	created_timestamp	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_import_file_user ON import_file("user");
CREATE INDEX idx_import_file_timestamp ON import_file("created_timestamp");

-- 変更ログ
CREATE TABLE change_log(
	"id"	bigserial	NOT NULL PRIMARY KEY,
	target_type	varchar(256)	NOT NULL,
	target_key	integer	NOT NULL,
	"user"	varchar(256),
	contents	varchar(8192),
	"timestamp"	timestamp NOT NULL DEFAULT current_timestamp
);
CREATE INDEX idx_change_log_user ON change_log("user");
CREATE INDEX idx_change_log_timestamp ON change_log("timestamp");

