CREATE DATABASE myblog;
GRANT ALL ON myblog.* TO user@localhost IDENTIFIED BY 'pass';

USE myblog;

CREATE TABLE post (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(512) DEFAULT NULL,
  body text,
  PRIMARY KEY (id)
)

CREATE TABLE comment (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(512) DEFAULT NULL,
  body text,
  post_id int(11) DEFAULT NULL,
  PRIMARY KEY (id)
)

CREATE TABLE blog_user (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(32) DEFAULT NULL,
  password varchar(32) DEFAULT NULL,
  PRIMARY KEY (id)
);
