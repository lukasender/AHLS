/*
 * README:
 * -------
 * Be aware of the commented commands! (Line 9)
 * If you haven't created the database 'ahls_v1' by your own, you may want to uncomment line 9.
 * Line 34: If you already have a user 'ahls' for your MySQL database, you can ignore line 34.
 */

-- CREATE DATABASE ahls_v1;
USE ahls_v1;

CREATE TABLE IF NOT EXISTS user (
	id INT(10) NOT NULL AUTO_INCREMENT,
	username VARCHAR(255),
	registered TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activity_log (
	id INT(10) NOT NULL AUTO_INCREMENT,
	sensor INT(10),
	user_id INT(10) NOT NULL,
	data INT(4),
	time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
) ENGINE=InnoDB;

ALTER TABLE activity_log
ADD CONSTRAINT FK_user_activity
FOREIGN KEY (user_id) REFERENCES user(id)
ON UPDATE CASCADE
ON DELETE CASCADE;

CREATE USER 'ahls'@'localhost' IDENTIFIED BY 'PASSWORD(ahls)';
GRANT ALL ON ahls_v1.* TO 'ahls'@'localhost';