-- ==========================
-- 1️⃣ PROFILE
-- ==========================
INSERT INTO PROFILE (
  id, cif, username, first_name, middle_name, last_name, dob,
  phone_number, email_address, nik,
  created_by, created_time, updated_by, updated_time
) VALUES (
  'P001',
  'CIF001',
  'ulionp',
  'Ulion',
  NULL,
  'Pardede',
  TO_TIMESTAMP('1998-07-12 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
  628123456789,
  'ulion.pardede@gmail.com',
  '3210987654321001',
  'SYSTEM',
  SYSTIMESTAMP,
  'SYSTEM',
  SYSTIMESTAMP
);

INSERT INTO PROFILE (
  id, cif, username, first_name, middle_name, last_name, dob,
  phone_number, email_address, nik,
  created_by, created_time, updated_by, updated_time
) VALUES (
  'P002',
  'CIF002',
  'oktaviaqa',
  'Oktavia',
  NULL,
  'QA',
  TO_TIMESTAMP('2000-03-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'),
  628987654321,
  'oktavia.qa@gmail.com',
  '3201122334455667',
  'SYSTEM',
  SYSTIMESTAMP,
  'SYSTEM',
  SYSTIMESTAMP
);


-- ==========================
-- 2️⃣ ROLE_MANAGEMENT
-- ==========================
INSERT INTO role_management (
  id, user_id, role_name,
  created_by, created_time, updated_by, updated_time
) VALUES (
  'R001',
  'P001',
  'ADMIN',
  'SYSTEM',
  SYSTIMESTAMP,
  'SYSTEM',
  SYSTIMESTAMP
);

INSERT INTO role_management (
  id, user_id, role_name,
  created_by, created_time, updated_by, updated_time
) VALUES (
  'R002',
  'P002',
  'NASABAH',
  'SYSTEM',
  SYSTIMESTAMP,
  'SYSTEM',
  SYSTIMESTAMP
);


-- ==========================
-- 3️⃣ USER_AUTH
-- ==========================
INSERT INTO USER_AUTH (
  user_id, username, email_address, role_id, password, is_user_blocked
) VALUES (
  'P001',
  'ulionp',
  'ulion.pardede@gmail.com',
  'R001',
  '$2a$10$wYwXawYF1YAfK0WDbFjV3e0wCGFeM53SRCOQOdwRZljxRzJNHpQZK', -- hash dari "123456"
  0
);

INSERT INTO USER_AUTH (
  user_id, username, email_address, role_id, password, is_user_blocked
) VALUES (
  'P002',
  'oktaviaqa',
  'oktavia.qa@gmail.com',
  'R002',
  '$2a$10$N.x0M5PL6If3pyOvI8HnWeXKUgplWzkCw.DExx7NvjPO6ZOfN5ScS', -- hash dari "password123"
  0
);
