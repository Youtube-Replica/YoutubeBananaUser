--SIGN UP--
CREATE OR REPLACE FUNCTION public.signup_user (_user_name varchar(255) = NULL, _email varchar(255) = NULL, _password varchar(255) = NULL,_salt VARCHAR(255)=NULL)
RETURNS VOID
AS
$BODY$
BEGIN
INSERT INTO public.app_user(
        user_name,
        email,
        password,
        salt
)values(
    _user_name,
    _email,
    _password,
    _salt
);
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--LOGIN--
CREATE OR REPLACE FUNCTION login_user(_email varchar(255)=NULL,_password varchar(255)=NULL)
RETURNS refcursor AS
$BODY$
DECLARE
ref refcursor;
BEGIN
OPEN ref FOR SELECT * FROM app_user
             WHERE email = _email
                AND password = _password;
                RETURN ref;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--DELETE--
CREATE OR REPLACE FUNCTION delete_user (_id INT = NULL)
RETURNS integer AS
$BODY$
DECLARE
  a_count integer;
BEGIN
DELETE FROM app_user
WHERE id = _id;
GET DIAGNOSTICS a_count = ROW_COUNT;
RETURN a_count;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--CHANGE PASSWORD--
CREATE OR REPLACE FUNCTION change_password_user (_id INT=NULL,_password varchar(255)=NULL)
RETURNS integer AS
$BODY$
DECLARE
  a_count integer;
BEGIN
UPDATE app_user
SET password = _password
WHERE id = _id;
GET DIAGNOSTICS a_count = ROW_COUNT;
RETURN a_count;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--GET USER--
CREATE OR REPLACE FUNCTION get_user_salt (_email varchar(255) = NULL)
RETURNS refcursor AS
$BODY$
DECLARE
ref refcursor;
BEGIN
OPEN ref FOR SELECT * FROM app_user WHERE email= _email;
RETURN ref;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--GET USER--
CREATE OR REPLACE FUNCTION get_user_by_id (_id INT = NULL)
RETURNS refcursor AS
$BODY$
DECLARE
ref refcursor;
BEGIN
OPEN ref FOR SELECT user_name,email FROM app_user WHERE id= _id;
RETURN ref;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;
