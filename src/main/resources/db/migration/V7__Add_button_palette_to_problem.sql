ALTER TABLE problem_set ADD COLUMN palettes TEXT DEFAULT '';
UPDATE problem_set SET palettes = 'Edit;Operators;Notations;Geometry' WHERE palettes = '';