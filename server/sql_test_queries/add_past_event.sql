SELECT * FROM conglomerate_test.events;

INSERT INTO conglomerate_test.events (name, group_id, date_time, duration, recurring)
values ('', 1, '2020-02-23 20:52:00', 1, 0);

INSERT INTO conglomerate_test.events (name, group_id, date_time, duration, recurring)
values ('', 1, '2020-04-22 00:00:00', 1, 1);