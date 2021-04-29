CREATE TABLE filter_city
(
  taskfilter_id BIGINT NOT NULL
    CONSTRAINT fkq730ulywfud4jjna965uru23l
    REFERENCES taskfilter,
  city_id       BIGINT NOT NULL
    CONSTRAINT fkplaep3v3ittqo2wolrrqsn0hd
    REFERENCES city,
  CONSTRAINT filter_city_pkey
  PRIMARY KEY (taskfilter_id, city_id)
);
