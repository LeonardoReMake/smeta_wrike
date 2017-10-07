ALTER TABLE task
   ALTER COLUMN name TYPE character varying(1024);
ALTER TABLE task
   ALTER COLUMN shopname TYPE character varying(1024);
ALTER TABLE task
   ALTER COLUMN orderNumber TYPE character varying(1024);
ALTER TABLE task
   ALTER COLUMN wrikeLink TYPE character varying(1024);
ALTER TABLE task
   ALTER COLUMN path TYPE character varying(1024);

ALTER TABLE city
   ALTER COLUMN name TYPE character varying(1024);