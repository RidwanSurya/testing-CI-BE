-- ACCOUNT
CREATE TABLE ACCOUNT (
    id                  VARCHAR2(21)    NOT NULL,
    user_id             VARCHAR2(21)    NOT NULL,
    account_number      VARCHAR2(21)    NOT NULL,
    cif                 VARCHAR2(21)    NOT NULL,
    effective_balance   NUMBER(15,4)    NOT NULL,
    account_type        VARCHAR2(65)    NOT NULL,
    sub_cat             VARCHAR2(65)    NOT NULL,
    currency_code       VARCHAR2(10)    NOT NULL,
    account_holder_name VARCHAR2(10)    NOT NULL,
    is_main_account     NUMBER(1,0)     NOT NULL,
    account_status      VARCHAR2(20)    NOT NULL,
    is_deleted          NUMBER(1,0)     DEFAULT 0 NOT NULL,
    created_by          VARCHAR2(50)    NOT NULL,
    created_time        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by          VARCHAR2(50)    NOT NULL,
    updated_time        TIMESTAMP       NOT NULL,
    CONSTRAINT PK_ACCOUNT PRIMARY KEY (id),
    CONSTRAINT FK_ACCOUNT_USER FOREIGN KEY (user_id) REFERENCES PROFILE(id),
    CONSTRAINT CK_ACCOUNT_MAIN CHECK (is_main_account IN (0,1)),
    CONSTRAINT CK_ACCOUNT_DELETED CHECK (is_deleted IN (0,1)),
    CONSTRAINT CK_ACCOUNT_TYPE CHECK (account_type IN ('DEP','DPLK', 'SAV', 'LFG')),
    CONSTRAINT CK_ACCOUNT_STATUS CHECK (account_status IN ('BUKA','BARU','DORM','TTUP'))
);

CREATE INDEX IDX_ACCOUNT_USER_ID ON ACCOUNT(user_id);
CREATE INDEX IDX_ACCOUNT_NUMBER  ON ACCOUNT(account_number);

-- CARD_PRODUCT
CREATE TABLE CARD_PRODUCT (
    id                   VARCHAR2(21)   NOT NULL,
    product_type         VARCHAR2(20)   NOT NULL,
    sub_cat              VARCHAR2(20)   NOT NULL,
    product_name         VARCHAR2(50)   NOT NULL,
    is_eligible_product  NUMBER(1,0)    DEFAULT 1,
    is_deleted           NUMBER(1,0)    DEFAULT 0,
    created_by           VARCHAR2(50)   NOT NULL,
    created_time         TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by           VARCHAR2(50)   NOT NULL,
    updated_time         TIMESTAMP      NOT NULL,
    CONSTRAINT PK_CARD_PRODUCT PRIMARY KEY (id),
    CONSTRAINT CK_CARD_ELIGIBLE CHECK (is_eligible_product IN (0,1)),
    CONSTRAINT CK_CARD_DELETED CHECK (is_deleted IN (0,1))
);

CREATE INDEX IDX_CARD_PRODUCT_TYPE ON CARD_PRODUCT(product_type);
CREATE INDEX IDX_CARD_PRODUCT_NAME ON CARD_PRODUCT(product_name);

-- TIME_DEPOSIT_ACCOUNT
CREATE TABLE TIME_DEPOSIT_ACCOUNT (
    id                         VARCHAR2(21)   NOT NULL,
    user_id                    VARCHAR2(21)   NOT NULL,
    deposit_account_number     VARCHAR2(21)   NOT NULL,
    cif                        VARCHAR2(21)   NOT NULL,
    effective_balance          NUMBER(15,0),
    account_type               VARCHAR2(65)   NOT NULL, -- DEP / DPLK
    sub_cat                    VARCHAR2(65)   NOT NULL,
    account_holder_name        VARCHAR2(10)   NOT NULL,
    is_deleted                 NUMBER(1,0)    DEFAULT 0 NOT NULL,
    disbursement_account_number VARCHAR2(21)  NOT NULL,
    currency_code              VARCHAR2(10)   NOT NULL,
    tenor_months               NUMBER(2,0)    NOT NULL,
    maturity_date              TIMESTAMP      NOT NULL,
    interest_rate              NUMBER(5,2)    NOT NULL,
    interest_payment_type      VARCHAR2(20)   NOT NULL, -- rollover, autorollover, etc
    deposit_account_status     VARCHAR2(20)   NOT NULL,
    created_by                 VARCHAR2(50)   NOT NULL,
    created_time               TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by                 VARCHAR2(50)   NOT NULL,
    updated_time               TIMESTAMP      NOT NULL,
    CONSTRAINT PK_TIME_DEPOSIT_ACCOUNT PRIMARY KEY (id),
    CONSTRAINT FK_TDA_USER FOREIGN KEY (user_id) REFERENCES PROFILE(id),
    CONSTRAINT CK_TDA_TYPE CHECK (account_type IN ('DEP','DPLK')),
    CONSTRAINT CK_TDA_DELETED CHECK (is_deleted IN (0,1))
);

CREATE INDEX IDX_TDA_USER_ID ON TIME_DEPOSIT_ACCOUNT(user_id);
CREATE INDEX IDX_TDA_CIF ON TIME_DEPOSIT_ACCOUNT(cif);
CREATE INDEX IDX_TDA_STATUS ON TIME_DEPOSIT_ACCOUNT(deposit_account_status);

-- SPLIT BILL
CREATE TABLE SPLIT_BILL (
    id               VARCHAR2(21)   NOT NULL,
    user_id          VARCHAR2(21)   NOT NULL,
    cif              VARCHAR2(21)   NOT NULL,
    account_number   VARCHAR2(50)   NOT NULL,
    transaction_id   VARCHAR2(21)   NOT NULL,
    split_bill_title VARCHAR2(50)   NOT NULL,
    currency         VARCHAR2(10)   NOT NULL,
    total_amount     NUMBER(10,5)   NOT NULL,
    has_paid         NUMBER(1,0)    DEFAULT 0 NOT NULL,
    payment_time     TIMESTAMP,
    is_deleted       NUMBER(1,0)    DEFAULT 0 NOT NULL,
    created_by       VARCHAR2(50)   NOT NULL,
    created_time     TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by       VARCHAR2(50)   NOT NULL,
    updated_time     TIMESTAMP      NOT NULL,
    CONSTRAINT PK_SPLIT_BILL PRIMARY KEY (id),
    CONSTRAINT FK_SPLIT_BILL_USER FOREIGN KEY (user_id) REFERENCES PROFILE(id),
    CONSTRAINT CK_SPLIT_BILL_PAID CHECK (has_paid IN (0,1)),
    CONSTRAINT CK_SPLIT_BILL_DELETED CHECK (is_deleted IN (0,1))
);

CREATE INDEX IDX_SPLIT_BILL_USER ON SPLIT_BILL(user_id);
CREATE INDEX IDX_SPLIT_BILL_ACC  ON SPLIT_BILL(account_number);

-- SPLIT_BILL_MEMBER
CREATE TABLE SPLIT_BILL_MEMBER (
    id              VARCHAR2(21)   NOT NULL,
    split_bill_id   VARCHAR2(21)   NOT NULL,
    user_id         VARCHAR2(21)   NOT NULL,
    amount_share    NUMBER(10,5)   NOT NULL,
    has_paid        NUMBER(1,0)    DEFAULT 0 NOT NULL,
    payment_date    TIMESTAMP,
    member_name     VARCHAR2(50)   NOT NULL,
    is_deleted      NUMBER(1,0)    DEFAULT 0 NOT NULL,
    created_by      VARCHAR2(50)   NOT NULL,
    created_time    TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by      VARCHAR2(50)   NOT NULL,
    updated_time    TIMESTAMP      NOT NULL,
    CONSTRAINT PK_SPLIT_BILL_MEMBER PRIMARY KEY (id),
    CONSTRAINT FK_SPLIT_MEMBER_BILL FOREIGN KEY (split_bill_id) REFERENCES SPLIT_BILL(id),
    CONSTRAINT CK_SPLIT_MEMBER_PAID CHECK (has_paid IN (0,1)),
    CONSTRAINT CK_SPLIT_MEMBER_DELETED CHECK (is_deleted IN (0,1))
);

CREATE INDEX IDX_SPLIT_MEMBER_BILL ON SPLIT_BILL_MEMBER(split_bill_id);

-- TRX_CATEGORY
CREATE TABLE TRX_CATEGORY (
    id              VARCHAR2(21)   NOT NULL,
    category_name   VARCHAR2(21),
    created_by      VARCHAR2(50)   NOT NULL,
    created_time    TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by      VARCHAR2(50)   NOT NULL,
    updated_time    TIMESTAMP      NOT NULL,
    CONSTRAINT PK_TRX_CATEGORY PRIMARY KEY (id)
);

-- TRX_HISTORY
CREATE TABLE TRX_HISTORY (
    id             VARCHAR2(21)   NOT NULL,
    user_id        VARCHAR2(21)   NOT NULL,
    account_number VARCHAR2(21)   NOT NULL,
    trx_date       TIMESTAMP      DEFAULT SYSTIMESTAMP,
    trx_cat_id     VARCHAR2(21)   NOT NULL,
    trx_amount     NUMBER(15,2)   NOT NULL,
    trx_target     VARCHAR2(65),
    trx_note       VARCHAR2(65),
    trx_type       VARCHAR2(65)   NOT NULL, -- DEBIT / CREDIT
    created_by     VARCHAR2(50)   NOT NULL,
    created_time   TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by     VARCHAR2(50)   NOT NULL,
    updated_time   TIMESTAMP      NOT NULL,
    CONSTRAINT PK_TRX_HISTORY PRIMARY KEY (id),
    CONSTRAINT FK_TRX_HISTORY_USER FOREIGN KEY (user_id) REFERENCES PROFILE(id),
    CONSTRAINT FK_TRX_HISTORY_CAT FOREIGN KEY (trx_cat_id) REFERENCES TRX_CATEGORY(id),
    CONSTRAINT CK_TRX_TYPE CHECK (trx_type IN ('DEBIT','CREDIT'))
);

CREATE INDEX IDX_TRX_HISTORY_USER ON TRX_HISTORY(user_id);
CREATE INDEX IDX_TRX_HISTORY_ACC  ON TRX_HISTORY(account_number);
CREATE INDEX IDX_TRX_HISTORY_CAT  ON TRX_HISTORY(trx_cat_id);