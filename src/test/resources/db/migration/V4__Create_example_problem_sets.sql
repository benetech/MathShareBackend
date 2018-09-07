-- TODO: For testing purposes create initial problems like in the example below:
INSERT INTO problem_set DEFAULT VALUES;

INSERT INTO problem_set_revision(problem_set_id) VALUES (1);

INSERT INTO problem(problem_set_revision_id, problem_text, title)
    VALUES (1, '3(-\frac{1}{6})(-\frac{2}{5})', 'Find the product');
