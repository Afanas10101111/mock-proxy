DELETE
FROM patterns;
DELETE
FROM rules;

INSERT INTO rules (id, status, content_type, body, repeat_limit, repeat_counter)
VALUES (1, 200, 'text/xml;charset=UTF-8', 'DEFAULT', 0, 0);
INSERT INTO rules (id, status, content_type, body, repeat_limit, repeat_counter)
VALUES (2, 200, 'text/xml;charset=UTF-8', 'SUBBED RESPONSE #1', 4, 0);

INSERT INTO patterns (id, pattern, mock_rule_id)
VALUES (1, 'DEFAULT', 1);
INSERT INTO patterns (id, pattern, mock_rule_id)
VALUES (2, 'PATTERN#1', 2);
