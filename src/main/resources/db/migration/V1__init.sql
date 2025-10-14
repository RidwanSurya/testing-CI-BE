-- PROFILE
CREATE TABLE PROFILE (
  id             VARCHAR2(36)     NOT NULL,
  cif            VARCHAR2(21)     NOT NULL,
  username       VARCHAR2(30),
  first_name     VARCHAR2(21)     NOT NULL,
  middle_name    VARCHAR2(65),
  last_name      VARCHAR2(65),
  dob            TIMESTAMP,
  phone_number   NUMBER(15,0)     NOT NULL,
  email_address  VARCHAR2(50)     NOT NULL,
  nik            VARCHAR2(32)     NOT NULL,
  created_by     VARCHAR2(50)     NOT NULL,
  created_time   TIMESTAMP        NOT NULL,
  updated_by     VARCHAR2(50)     NOT NULL,
  updated_time   TIMESTAMP        NOT NULL,
  CONSTRAINT PK_PROFILE PRIMARY KEY (id),
  CONSTRAINT UQ_PROFILE_CIF UNIQUE (cif),
  CONSTRAINT UQ_PROFILE_EMAIL UNIQUE (email_address)
);

CREATE INDEX IDX_PROFILE_USERNAME ON PROFILE(username);

-- USER_AUTH
CREATE TABLE USER_AUTH (
  user_id          VARCHAR2(36)   NOT NULL, -- FK to PROFILE.id
  username         VARCHAR2(30),            -- shadow username (opsional)
  email_address    VARCHAR2(50)   NOT NULL,
  role_id          VARCHAR2(21)   NOT NULL, -- FK ke role_management.id
  password         VARCHAR2(250)  NOT NULL, -- hashed (BCrypt)
  is_user_blocked  NUMBER(1,0),
  CONSTRAINT PK_USER_AUTH PRIMARY KEY (user_id),
  CONSTRAINT FK_USER_AUTH_PROFILE FOREIGN KEY (user_id) REFERENCES PROFILE(id)
);

CREATE INDEX IDX_USER_AUTH_USERNAME ON USER_AUTH(username);

-- role_management (penetapan role per user)
CREATE TABLE role_management (
  id            VARCHAR2(36)   NOT NULL,
  user_id       VARCHAR2(36)   NOT NULL, -- FK ke PROFILE.id
  role_name     VARCHAR2(21)   NOT NULL, -- 'ADMIN' atau 'NASABAH'
  created_by    VARCHAR2(50)   NOT NULL,
  created_time  TIMESTAMP      NOT NULL,
  updated_by    VARCHAR2(50)   NOT NULL,
  updated_time  TIMESTAMP      NOT NULL,
  CONSTRAINT PK_ROLE_MGMT PRIMARY KEY (id),
  CONSTRAINT FK_ROLE_MGMT_PROFILE FOREIGN KEY (user_id) REFERENCES PROFILE(id)
);

CREATE UNIQUE INDEX UQ_ROLE_PER_USER ON role_management(user_id, role_name);

-- Hubungkan USER_AUTH.role_id -> role_management.id
ALTER TABLE USER_AUTH
  ADD CONSTRAINT FK_USER_AUTH_ROLE
  FOREIGN KEY (role_id) REFERENCES role_management(id);