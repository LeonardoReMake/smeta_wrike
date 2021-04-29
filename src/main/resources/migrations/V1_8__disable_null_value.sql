update work set amount =0 where amount is null;
ALTER TABLE work
   ALTER COLUMN amount SET NOT NULL;

update work set quantity =0 where quantity is null;
ALTER TABLE work
   ALTER COLUMN quantity SET NOT NULL;


update work set unitPrice =0 where unitPrice is null;
ALTER TABLE work
   ALTER COLUMN unitPrice SET NOT NULL;

