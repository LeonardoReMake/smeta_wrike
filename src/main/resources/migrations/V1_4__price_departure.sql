CREATE TABLE pricedeparture
(
  id bigint NOT NULL,
  daytimeprice double precision,
  nightlytimeprice double precision,
  urgenttimeprice double precision,
  CONSTRAINT pricedeparture_pkey PRIMARY KEY (id)
);
