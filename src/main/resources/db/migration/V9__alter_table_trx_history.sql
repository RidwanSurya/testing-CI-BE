-- rename column name
ALTER TABLE TRX_HISTORY RENAME COLUMN trx_amount TO transaction_amount;
ALTER TABLE TRX_HISTORY RENAME COLUMN trx_note TO transaction_description;
ALTER TABLE TRX_HISTORY RENAME COLUMN trx_date TO transaction_date;
ALTER TABLE TRX_HISTORY RENAME COLUMN trx_type TO transaction_type;

-- drop column yang tidak perlu
ALTER TABLE TRX_HISTORY MODIFY (transaction_description VARCHAR2(100));
ALTER TABLE TRX_HISTORY DROP CONSTRAINT FK_TRX_HISTORY_CAT;
ALTER TABLE TRX_HISTORY DROP COLUMN trx_cat_id;
ALTER TABLE TRX_HISTORY DROP COLUMN trx_target;

-- add new column
ALTER TABLE TRX_HISTORY ADD (
    party_name          VARCHAR2(55) NOT NULL,
    party_detail        VARCHAR2(55) NOT NULL,
    payment_method      VARCHAR2(55),
    debit_credit        VARCHAR2(20) NOT NULL,
    split_bill_id       VARCHAR2(21)
)

ALTER TABLE TRX_HISTORY
ADD CONSTRAINT FK_TRX_HISTORY_SPLIT_BILL
FOREIGN KEY (split_bill_id) REFERENCES SPLIT_BILL(id)