ALTER TABLE problem_set_revision ADD COLUMN palettes TEXT DEFAULT '';
UPDATE problem_set_revision SET palettes = (SELECT p.palettes FROM problem_set p where p.id = problem_set_id);
UPDATE problem_set_revision SET palettes = 'Edit;Operators;Notations;Geometry' WHERE palettes = '' or palettes is null;
