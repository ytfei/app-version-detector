# Users schema
 
# --- !Ups
 
create table app_info(
	id bigint(20) NOT NULL AUTO_INCREMENT,
	name varchar(255) NOT NULL unique,
	init_version_code bigint(20) NOT NULL,
	init_version_name varchar(255),
	curr_version_code bigint(20),
	curr_version_name varchar(255),
	last_update timestamp,

	PRIMARY KEY (id)
);

CREATE TABLE subscriber(
	id bigint(20) NOT NULL AUTO_INCREMENT,
	name varchar(255),
	email varchar(255) NOT NULL unique,

	PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE User;
DROP TABLE app;
DROP TABLE version;
DROP TABLE subscriber;