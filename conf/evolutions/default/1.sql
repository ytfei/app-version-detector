# Users schema
 
# --- !Ups
 
CREATE TABLE User (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    fullname varchar(255) NOT NULL,
    isAdmin boolean NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE app (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	package varchar(255) NOT NULL,

	PRIMARY KEY (id)
);

CREATE TABLE version(
	id bigint(20) NOT NULL AUTO_INCREMENT,
	app_id bigint(20) NOT NULL,
	version_code bigint(20) NOT NULL,
	version_name varchar(255),
	last_update timestamp,

	PRIMARY KEY (id)
);

CREATE TABLE subscriber(
	id bigint(20) NOT NULL AUTO_INCREMENT,
	name varchar(255),
	email varchar(255) NOT NULL,

	PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE User;
DROP TABLE app;
DROP TABLE version;
DROP TABLE subscriber;