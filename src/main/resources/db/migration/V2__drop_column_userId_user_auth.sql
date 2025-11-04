DECLARE
    v_constraint_name VARCHAR2(100);
BEGIN
    -- Cek apakah constraint dengan nama ini ada di user_constraints
    SELECT constraint_name INTO v_constraint_name
    FROM user_constraints
    WHERE table_name = 'USER_AUTH'
      AND constraint_name = 'FK_USER_AUTH_USER_ID'
      AND constraint_type = 'R';

    -- Jika ketemu, drop constraint-nya
    EXECUTE IMMEDIATE 'ALTER TABLE user_auth DROP CONSTRAINT FK_USER_AUTH_USER_ID';

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Constraint FK_USER_AUTH_USER_ID not found, skipping.');
    WHEN OTHERS THEN
        -- Hanya abaikan error ORA-02443 (constraint tidak ada)
        IF SQLCODE != -2443 THEN
            RAISE;
        END IF;
END;
/

ALTER TABLE USER_AUTH DROP COLUMN USER_ID;