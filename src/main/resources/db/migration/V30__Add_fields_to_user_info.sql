ALTER TABLE user_info ADD info_version int4 NULL DEFAULT 1;
ALTER TABLE user_info ADD disability text NULL;
ALTER TABLE user_info ADD gender varchar(7) NULL;
ALTER TABLE user_info ADD year_of_birth int4 NULL;
ALTER TABLE user_info ADD county varchar(127) NULL;
ALTER TABLE user_info ADD zipcode varchar(15) NULL;
ALTER TABLE user_info ADD grade varchar(31) NULL;
