CREATE TABLE city
(
  id bigint NOT NULL,
  name character varying(255),
  report_id bigint,
  CONSTRAINT city_pkey PRIMARY KEY (id)
);

CREATE TABLE report
(
  id bigint NOT NULL,
  "number" integer NOT NULL,
  CONSTRAINT report_pkey PRIMARY KEY (id)
);
CREATE TABLE taskfilter
(
  id bigint NOT NULL,
  enddate timestamp without time zone,
  startdate timestamp without time zone,
  city_id bigint,
  CONSTRAINT taskfilter_pkey PRIMARY KEY (id),
  CONSTRAINT fk31hr0qyqqk038dfl125go4oml FOREIGN KEY (city_id)
      REFERENCES city (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE task ADD COLUMN city_id bigint;
ALTER TABLE ONLY city ADD CONSTRAINT fk7y0ywlkteayglo5qoqkg8y4fo FOREIGN KEY (report_id) REFERENCES report(id);

ALTER TABLE task ADD COLUMN completeddate timestamp without time zone;