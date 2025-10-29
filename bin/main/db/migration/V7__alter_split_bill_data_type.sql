-- V7__alter_split_bill_data_type.sql (Oracle)

-- Perlebar presisi untuk uang
ALTER TABLE SPLIT_BILL MODIFY ( total_amount NUMBER(18,2) );
ALTER TABLE SPLIT_BILL_MEMBER MODIFY ( amount_share NUMBER(18,2) );
