CREATE TABLE OTP_VERIFICATION (
  id            VARCHAR2(36)   PRIMARY KEY,      -- otp_ref
  user_id       VARCHAR2(36)   NOT NULL,         -- FK -> PROFILE.id (atau USER_AUTH.user_id)
  otp_code      VARCHAR2(6)    NOT NULL,
  email_to      VARCHAR2(100)  NOT NULL,
  expires_at    TIMESTAMP      NOT NULL,
  is_used       NUMBER(1,0)    DEFAULT 0 NOT NULL,
  created_time  TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL
);
CREATE INDEX IDX_OTP_USER_EXPIRE ON OTP_VERIFICATION (user_id, expires_at);
