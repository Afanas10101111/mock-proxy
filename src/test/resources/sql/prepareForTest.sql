DELETE
FROM patterns;
DELETE
FROM rules;

INSERT INTO rules (id, stub, repeat_limit, repeat_counter)
VALUES (1, 'SUBBED RESPONSE #1', 4, 0);
INSERT INTO rules (id, stub, repeat_limit, repeat_counter)
VALUES (2, 'SUBBED RESPONSE #2', 4, 0);

INSERT INTO patterns (id, pattern, mock_rule_id)
VALUES (1, 'PATTERN#1', 1);
INSERT INTO patterns (id, pattern, mock_rule_id)
VALUES (2, 'PATTERN#2', 2);
