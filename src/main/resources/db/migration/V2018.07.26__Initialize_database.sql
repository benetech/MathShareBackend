CREATE TABLE problem_set(
    id SERIAL PRIMARY KEY,
    edit_code VARCHAR (255) UNIQUE NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    date_updated TIMESTAMP
);

CREATE TABLE problem_set_revision(
    id SERIAL PRIMARY KEY,
    problem_set_id INTEGER NOT NULL,
    replaced_by INTEGER,
    share_code VARCHAR (255) UNIQUE NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (problem_set_id) REFERENCES problem_set(id),
    FOREIGN KEY (replaced_by) REFERENCES problem_set_revision(id)
);

CREATE TABLE problem(
    id SERIAL PRIMARY KEY,
    problem_set_id INTEGER NOT NULL,
    replaced_by INTEGER,
    problem_text TEXT NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (problem_set_id) REFERENCES problem_set(id),
    FOREIGN KEY (replaced_by) REFERENCES problem(id)
);

CREATE TABLE problem_solution(
    id SERIAL PRIMARY KEY,
    problem_id INTEGER NOT NULL,
    edit_code VARCHAR (255) UNIQUE NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    date_modified TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problem(id)
);

CREATE TABLE solution_revision(
    id SERIAL PRIMARY KEY,
    problem_solution_id INTEGER NOT NULL,
    replaced_by INTEGER,
    share_code VARCHAR (255) UNIQUE NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (problem_solution_id) REFERENCES problem_solution(id),
    FOREIGN KEY (replaced_by) REFERENCES solution_revision(id)
);

CREATE TABLE solution_step(
    id SERIAL PRIMARY KEY,
    explanation TEXT,
    step_value TEXT NOT NULL,
    solution_id INTEGER NOT NULL,
    replaced_by INTEGER,
    deleted BOOLEAN NOT NULL DEFAULT false,
    date_modified TIMESTAMP NOT NULL,
    FOREIGN KEY (solution_id) REFERENCES problem_solution(id),
    FOREIGN KEY (replaced_by) REFERENCES solution_step(id)
);

CREATE TABLE scratchpad(
    id SERIAL PRIMARY KEY,
    step_id INTEGER NOT NULL,
    content TEXT,
    date_modified TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES solution_step(id)
);
