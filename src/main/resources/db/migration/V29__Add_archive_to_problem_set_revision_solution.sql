ALTER TABLE problem_set_revision_solution ADD COLUMN archive_mode varchar(16);
ALTER TABLE problem_set_revision_solution ADD COLUMN archived_at TIMESTAMP;
ALTER TABLE problem_set_revision_solution ADD COLUMN archived_by varchar(36) NULL;
