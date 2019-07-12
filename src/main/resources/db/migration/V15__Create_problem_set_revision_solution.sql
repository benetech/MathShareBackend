CREATE TABLE problem_set_revision_solution(
    id SERIAL PRIMARY KEY,
    problem_set_revision_id INTEGER NOT NULL,
    edit_code BIGINT,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (problem_set_revision_id) REFERENCES problem_set_revision(id)
);

ALTER TABLE review_solution_revision RENAME COLUMN deleted TO inactive;
ALTER TABLE review_solution_revision ADD problem_set_revision_solution_id int NULL;
ALTER TABLE review_solution_revision
ADD CONSTRAINT review_solution_revision_problem_set_revision_solution_fk
FOREIGN KEY (problem_set_revision_solution_id) REFERENCES problem_set_revision_solution(id);
