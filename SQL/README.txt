Changes on database
===================

As of today, 11.04.2013, the database has changed quite a lot.
All activity_log entries are now associated with a user_id and a sensor_id.

1. How To migrate.
------------------
	_ You don't have to change anything within the Java code (hopefully that's true).
	_ Go to your PHPMyAdmin (or MySQL console).
	_ Run the 'ahls_v1_backup_of_luis_db.sql' script. This will create a new database
		called 'ahls_v1'. It will also create the new table structure and migrate to
		old data entries (which should be around 40.000 entries).
	_ Last but not least:
		Create a new database user:
			CREATE USER 'ahls'@'localhost' IDENTIFIED BY 'PASSWORD(ahls)';
			GRANT ALL ON ahls_v1.* TO 'ahls'@'localhost';