--Charles Treatman
--Constraint violations

spool scenaria.lst

column firstname format a12
column lastname format a15
column ssn format a9
column Tnumber format a10
column phone format a10
column email format a24
column street format a30
column numbeds format 0
column numbaths format 0
column wantedtnum format a10
column wantingtnum format a11
column year format a10

--Insert and Update to cause key violations
insert into student values ('T00032696', 'Ben', 'Pierce', '4407744174', 2817, 
				'Junior', 'bpierce@oberlin.edu', NULL, NULL);

update student
set Tnumber = 'T00032696'
where firstname = 'Craig' AND lastname = 'Betchart';

--Insert, delete, and update to cause referential integrity violations
insert into rents values ('101 Main Street', 'T00032696');

delete from landlord
where firstname = 'Rahula' AND lastname = 'Strohl';

update rents
set street = 'Where I live'
where Tnumber = 'T00032696';

--Insert and update to cause check constraint violations
insert into compatible values ('T00032696', 'T00032696');

update student
set year = 'Mullet'
where year = 'Freshman';

spool off