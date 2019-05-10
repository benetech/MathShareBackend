ALTER TABLE problem_solution DROP COLUMN review_code;

CREATE TABLE review_solution_revision (
    id SERIAL PRIMARY KEY,
    solution_revision_id INTEGER NOT NULL,
    review_code BIGINT,
    deleted BOOLEAN NOT NULL DEFAULT false,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (solution_revision_id) REFERENCES solution_revision(id)
);
