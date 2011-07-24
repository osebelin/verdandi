CREATE SEQUENCE recordid_seq;

CREATE DOMAIN user_name as varchar(16);
CREATE DOMAIN project_id as varchar(16);


-- #################################################################
-- # Table definitions                                             #
-- #################################################################

create table projects (
    id project_id NOT NULL PRIMARY KEY,
    name text NOT NULL,
    description text,
    active boolean
);

create table workrecords
(
    id int PRIMARY KEY DEFAULT NEXTVAL('recordid_seq'),
    owner user_name NOT NULL,
    starttime timestamp NOT NULL,
    duration int NOT NULL,
    projectid project_id NOT NULL,
    annotation text,
    FOREIGN KEY(projectid) REFERENCES projects(id)
    -- TODO owner forgein key in der users-Tabelle?
);


-- #################################################################
-- # Views                                                         #
-- #################################################################

create view myrecords as 
    select id, starttime, duration, projectid, annotation
    from workrecords 
    where owner = current_user;

CREATE OR REPLACE RULE _insert_into_myrecords AS
    ON INSERT TO myrecords
    DO INSTEAD
        INSERT INTO workrecords(id, owner, starttime, duration, projectid, annotation)
        VALUES ( new.id, session_user::user_name, new.starttime, new.duration,
                new.projectid, new.annotation);

        
CREATE OR REPLACE RULE _delete_from_myrecords AS
    ON DELETE TO myrecords
    DO INSTEAD
        DELETE FROM workrecords
        WHERE (owner=(session_user::user_name) AND id=old.id);
 
 
CREATE OR REPLACE RULE _update_to_myrecords AS
    ON UPDATE TO myrecords
    DO INSTEAD
        UPDATE workrecords
        SET starttime=new.starttime, duration=new.duration, 
            projectid=new.projectid, annotation=new.annotation
        WHERE (owner=(session_user::user_name) AND id=new.id );



CREATE TABLE users (
	uname user_name NOT NULL PRIMARY KEY,
	firstname TEXT,
	lastname TEXT,
	active boolean
);

-- 
-- CREATE TABLE roles (
-- 	gname user_name NOT NULL PRIMARY KEY,
-- 	description TEXT
-- );
-- 
-- CREATE TABLE users_group_map (
-- 	uname user_name,
-- 	gname user_name,
-- 	primary key (uname,gname),
-- 	foreign key (uname) references users(uname),
-- 	foreign key (gname) references groups(gname)
-- );
-- 



-- #################################################################
-- # Helper                                                        #
-- #################################################################


CREATE FUNCTION thismonday() RETURNS date
    AS 'SELECT current_date- (date_part(\'dow\', now()))::int +1;'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

--sontag
SELECT current_date+ (7-date_part('dow', now()))::int;

CREATE FUNCTION thissunday() RETURNS date
    AS 'SELECT current_date+ (7-date_part(\'dow\', now()))::int;'
    LANGUAGE SQL
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;




