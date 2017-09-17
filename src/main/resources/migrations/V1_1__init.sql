CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE material (
    id bigint NOT NULL,
    amount double precision,
    name character varying(255),
    quantity double precision,
    unitprice double precision,
    units character varying(255),
    task_id bigint,
    template_id bigint
);

CREATE TABLE task (
    id bigint NOT NULL,
    amount double precision,
    city character varying(255),
    createddate timestamp without time zone,
    filled boolean NOT NULL,
    name character varying(255),
    ordernumber character varying(255),
    path character varying(255),
    shopname character varying(255),
    wrikeid character varying(255),
    wrikelink character varying(255)
);



CREATE TABLE template (
    id bigint NOT NULL,
    name character varying(255)
);

CREATE TABLE work (
    id bigint NOT NULL,
    amount double precision,
    name character varying(255),
    quantity double precision,
    unitprice double precision,
    units character varying(255),
    task_id bigint,
    template_id bigint
);


--

ALTER TABLE ONLY material  ADD CONSTRAINT material_pkey PRIMARY KEY (id);
ALTER TABLE ONLY task  ADD CONSTRAINT task_pkey PRIMARY KEY (id);
ALTER TABLE ONLY template ADD CONSTRAINT template_pkey PRIMARY KEY (id);
ALTER TABLE ONLY work  ADD CONSTRAINT work_pkey PRIMARY KEY (id);
ALTER TABLE ONLY material ADD CONSTRAINT fk60ihq6jjrdg36cfog6lwporj1 FOREIGN KEY (template_id) REFERENCES template(id);
ALTER TABLE ONLY work ADD CONSTRAINT fk9y9jkm68j5swttlp3i4ubrbe8 FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE ONLY work ADD CONSTRAINT fkk04p9qwd9sydqowvvbhapl082 FOREIGN KEY (template_id) REFERENCES template(id);
ALTER TABLE ONLY material ADD CONSTRAINT fkr4ij9r3m2agy9lfp7k55jh29g FOREIGN KEY (task_id) REFERENCES task(id);

