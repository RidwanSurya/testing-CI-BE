CREATE TABLE LIFEGOALS_ACCOUNT (
    id                      VARCHAR2(21)     NOT NULL,
    user_id                 VARCHAR2(21)     NOT NULL,
    cif                     VARCHAR2(21)     NOT NULL,
    account_number          VARCHAR2(21),
    lifegoals_name          VARCHAR2(255),
    lifegoals_category_name VARCHAR2(100),
    account_deposit         NUMBER(15,3),
    account_target          NUMBER(15,3),
    lifegoals_trx_creation_id VARCHAR2(21),
    account_target_amount   NUMBER(15,3),
    estimation_amount       NUMBER(15,3),
    lifegoals_description   VARCHAR2(255),
    maturity_date           NUMBER(2,3),
    lifegoals_duration      NUMBER(3),
    created_time            TIMESTAMP        NOT NULL,
    created_by              VARCHAR2(50)     NOT NULL,
    updated_time            TIMESTAMP        NOT NULL,
    updated_by              VARCHAR2(50)     NOT NULL,

    CONSTRAINT PK_LIFEGOALS_ACCOUNT PRIMARY KEY (id),
    CONSTRAINT FK_LIFEGOALS_ACCOUNT_PROFILE FOREIGN KEY (user_id)
        REFERENCES PROFILE(id)
);

-- Optional index untuk mempercepat query
CREATE INDEX IDX_LIFEGOALS_ACCOUNT_USER_CIF ON LIFEGOALS_ACCOUNT(user_id, cif);
CREATE INDEX IDX_LIFEGOALS_ACCOUNT_NUMBER ON LIFEGOALS_ACCOUNT(account_number);