CREATE sequence url_sequence;

ALTER TABLE problem_set DROP COLUMN edit_code;
ALTER TABLE problem_set ADD COLUMN edit_code BIGINT AUTO_INCREMENT UNIQUE NOT NULL;

ALTER TABLE problem_set_revision DROP COLUMN share_code;
ALTER TABLE problem_set_revision ADD COLUMN share_code BIGINT AUTO_INCREMENT UNIQUE NOT NULL;

ALTER TABLE problem_solution DROP COLUMN edit_code;
ALTER TABLE problem_solution ADD COLUMN edit_code BIGINT AUTO_INCREMENT UNIQUE NOT NULL;

ALTER TABLE solution_revision DROP COLUMN share_code;
ALTER TABLE solution_revision ADD COLUMN share_code BIGINT AUTO_INCREMENT UNIQUE NOT NULL;
