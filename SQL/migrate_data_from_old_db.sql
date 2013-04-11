-- copy logged data from ahls to ahls_v1 database.
-- all values will be logged for 'sensor id = 1'
-- all values will be associated with 'user 1 = 031796799e76cf794757b4cd59bd4eb7d0970abb'

INSERT INTO ahls_v1.activity_log (sensor, user_id, data, time)
	SELECT 1, 1, data, time FROM ahls.activity_log;