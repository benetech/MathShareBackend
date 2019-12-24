CREATE TABLE problem_step (
    id SERIAL PRIMARY KEY,
    explanation TEXT,
    cleanup TEXT,
    step_value TEXT NOT NULL,
    replaced_by INTEGER,
    problem_id INTEGER NOT NULL,
    scratchpad_id INTEGER,
    deleted BOOLEAN NOT NULL DEFAULT false,
    date_modified TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problem(id),
    FOREIGN KEY (replaced_by) REFERENCES problem_step(id),
    FOREIGN KEY (scratchpad_id) REFERENCES scratchpad(id)
);
