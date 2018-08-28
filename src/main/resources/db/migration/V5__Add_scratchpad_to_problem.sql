ALTER TABLE problem ADD COLUMN scratchpad_id INTEGER;
ALTER TABLE problem ADD FOREIGN KEY (scratchpad_id) REFERENCES scratchpad(id);

ALTER TABLE solution_step ADD COLUMN scratchpad_id INTEGER;
ALTER TABLE solution_step ADD FOREIGN KEY (scratchpad_id) REFERENCES scratchpad(id);

ALTER TABLE scratchpad DROP COLUMN step_id;
