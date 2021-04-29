CREATE TABLE manager
(
  id   BIGINT NOT NULL
    CONSTRAINT manager_pkey
    PRIMARY KEY,
  name VARCHAR(255)
    CONSTRAINT uk_nj1wgg2d9dg0ev2nr87370m8d
    UNIQUE
);

ALTER TABLE task ADD CONSTRAINT wrikeid_unique UNIQUE(wrikeId);
ALTER TABLE task ADD COLUMN manager_id BIGINT REFERENCES manager;