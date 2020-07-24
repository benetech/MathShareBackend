ALTER TABLE problem_step ADD COLUMN in_progress BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE solution_step ADD COLUMN in_progress BOOLEAN NOT NULL DEFAULT false;
