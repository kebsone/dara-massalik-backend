ALTER TABLE pedagogical_tracking
    ADD CONSTRAINT uk_pedagogical_tracking_student_date
        UNIQUE (student_id, date);