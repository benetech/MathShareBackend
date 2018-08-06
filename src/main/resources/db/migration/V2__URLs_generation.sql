-- Creating pseudo random 64 bit number, based on https://wiki.postgresql.org/wiki/XTEA
CREATE FUNCTION generateBigInt(val bigint)
    RETURNS BIGINT AS $$
BEGIN
     RETURN xtea(val, 'mathshareurlkeyg'::bytea, false);
END
$$ LANGUAGE plpgsql strict immutable;

CREATE OR REPLACE FUNCTION xtea(val bigint, cr_key bytea, encrypt BOOLEAN)
returns bigint AS $$
DECLARE
  bk int[4];
  b bigint;
BEGIN
  IF octet_length(cr_key)<>16 THEN
     RAISE EXCEPTION 'XTEA crypt key must be 16 bytes long.';
  END IF;
  FOR i IN 1..4 LOOP
    b:=0;
    FOR j IN 0..3 LOOP
      b:= (b<<8) | get_byte(cr_key, (i-1)*4+j);
    END LOOP;
    bk[i] := CASE WHEN b>2147483647 THEN b-4294967296 ELSE b END;
  END LOOP;
  RETURN xtea(val, bk, encrypt);
END
$$ immutable language plpgsql;

CREATE OR REPLACE FUNCTION xtea(val bigint, key128 int4[4], encrypt BOOLEAN)
returns bigint AS $$
DECLARE
  v0 bigint;
  v1 bigint;
  _sum bigint:=0;
  cr_key bigint[4]:=ARRAY[
     CASE WHEN key128[1]<0 THEN key128[1]+4294967296 ELSE key128[1] END,
     CASE WHEN key128[2]<0 THEN key128[2]+4294967296 ELSE key128[2] END,
     CASE WHEN key128[3]<0 THEN key128[3]+4294967296 ELSE key128[3] END,
     CASE WHEN key128[4]<0 THEN key128[4]+4294967296 ELSE key128[4] END
   ];
BEGIN
  v0 := (val>>32)&4294967295;
  v1 := val&4294967295;
  IF encrypt THEN
    FOR i IN 0..63 LOOP
      v0 := (v0 + ((
         ((v1<<4)&4294967295 # (v1>>5))
           + v1)&4294967295
           #
           (_sum + cr_key[1+(_sum&3)::int])&4294967295
           ))&4294967295;
      _sum := (_sum + 2654435769) & 4294967295;
      v1 := (v1 + ((
             ((v0<<4)&4294967295 # (v0>>5))
           + v0)&4294967295
          #
          (_sum + cr_key[1+((_sum>>11)&3)::int])&4294967295
          ))&4294967295;
    END LOOP;
  ELSE
    _sum := (2654435769 * 64)&4294967295;
    FOR i IN 0..63 LOOP
      v1 := (v1 - ((
          ((v0<<4)&4294967295 # (v0>>5))
          + v0)&4294967295
          #
          (_sum + cr_key[1+((_sum>>11)&3)::int])&4294967295
          ))&4294967295;
      _sum := (_sum - 2654435769)& 4294967295;
      v0 := (v0 - ((
         ((v1<<4)&4294967295 # (v1>>5))
           + v1)&4294967295
           #
           (_sum + cr_key[1+(_sum&3)::int])&4294967295
           ))&4294967295;
    END LOOP;
  END IF;
  RETURN (v0<<32)|v1;
END
$$ immutable strict language plpgsql;

CREATE sequence url_sequence;

ALTER TABLE problem_set DROP COLUMN edit_code;
ALTER TABLE problem_set ADD COLUMN edit_code BIGINT UNIQUE NOT NULL DEFAULT generateBigInt(nextval('url_sequence')::bigint);

ALTER TABLE problem_set_revision DROP COLUMN share_code;
ALTER TABLE problem_set_revision ADD COLUMN share_code BIGINT UNIQUE NOT NULL DEFAULT generateBigInt(nextval('url_sequence')::bigint);

ALTER TABLE problem_solution DROP COLUMN edit_code;
ALTER TABLE problem_solution ADD COLUMN edit_code BIGINT UNIQUE NOT NULL DEFAULT generateBigInt(nextval('url_sequence')::bigint);

ALTER TABLE solution_revision DROP COLUMN share_code;
ALTER TABLE solution_revision ADD COLUMN share_code BIGINT UNIQUE NOT NULL DEFAULT generateBigInt(nextval('url_sequence')::bigint);
