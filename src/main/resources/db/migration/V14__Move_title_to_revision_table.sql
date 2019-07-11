ALTER TABLE problem_set_revision ADD COLUMN title VARCHAR(255);

UPDATE problem_set_revision SET title=(SELECT p.title from problem_set p WHERE p.id=problem_set_id);

ALTER TABLE problem_set DROP COLUMN title;
UPDATE problem_set SET palettes = 'Edit;Operators;Notations;Geometry' WHERE palettes = '';