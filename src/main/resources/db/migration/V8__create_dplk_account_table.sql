-- V8__create_dplk_account_uuid.sql (UUID 36 chars)
CREATE TABLE DPLK_ACCOUNT (
  id                    VARCHAR2(36)   NOT NULL,
  user_id               VARCHAR2(36)   NOT NULL,            -- FK -> PROFILE(id) [36]
  cif                   VARCHAR2(21)   NOT NULL,
  account_number        VARCHAR2(21),                       -- FK -> ACCOUNT(account_number)
  account_number_dplk   VARCHAR2(21),
  currency_code         VARCHAR2(10),
  dplk_product_name     VARCHAR2(100),
  dplk_initial_deposit  NUMBER(15,3),
  product_type          VARCHAR2(20),
  created_time          TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
  created_by            VARCHAR2(50)   NOT NULL,
  updated_time          TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by            VARCHAR2(50)   NOT NULL,
  CONSTRAINT PK_DPLK_ACCOUNT PRIMARY KEY (id)
);

CREATE INDEX IDX_DPLK_USER_ID          ON DPLK_ACCOUNT (user_id);
CREATE INDEX IDX_DPLK_CIF              ON DPLK_ACCOUNT (cif);
CREATE INDEX IDX_DPLK_ACCNO_DPLK       ON DPLK_ACCOUNT (account_number_dplk);
CREATE INDEX IDX_DPLK_ACCNO_REF        ON DPLK_ACCOUNT (account_number);

ALTER TABLE DPLK_ACCOUNT
  ADD CONSTRAINT FK_DPLK_USER
  FOREIGN KEY (user_id) REFERENCES PROFILE(id);
--
--ALTER TABLE DPLK_ACCOUNT
--  ADD CONSTRAINT FK_DPLK_ACCOUNT_REF
--  FOREIGN KEY (account_number) REFERENCES ACCOUNT(account_number);
