ALTER TABLE users MODIFY COLUMN id int(11);
ALTER TABLE users DROP PRIMARY KEY;
ALTER TABLE users DROP COLUMN id;
ALTER TABLE users ADD PRIMARY KEY (ldapName);
ALTER TABLE USERS ADD authorities VARCHAR(255);