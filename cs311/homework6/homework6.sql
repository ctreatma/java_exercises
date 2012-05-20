--Charles Treatman
--Homework 6
--Queries and Modifications
spool homework6.lst

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

--1
--List the name and email address of all students Charles Treatman would like to live with
--Note that if there are multiple students named Charles Treatman, this query will
--return the list for all students named Charles Treatman.
SELECT S2.firstname, S2.lastname, S2.email
FROM Student S1, Student S2, Compatible
WHERE S1.firstname = 'Charles' AND S1.lastname = 'Treatman'
AND S1.Tnumber = wantingTnum AND S2.Tnumber = wantedTnum;

--2
--List the name and email of all students willing to live with Craig Betchart.
--As before, w/o a Tnumber we may get results for more than one Craig Betchart.
SELECT S2.firstname, S2.lastname, S2.email
FROM Student S2, Compatible, (SELECT Tnumber
			      FROM Student
			      WHERE firstname = 'Craig' AND lastname = 'Betchart') S1
WHERE S1.Tnumber = wantedTnum AND S2.Tnumber = wantingTnum;

--3
--List the name and phone number of all landlords who offer a ten month lease on some property.
SELECT DISTINCT firstname, lastname, phone
FROM Landlord NATURAL JOIN Owns NATURAL JOIN (SELECT street
					      FROM Lease
					      WHERE months = 10);

--4
--List the first and last name of all students currently renting some property
--and group the tenants by the rental's street address.
SELECT street, firstname, lastname 
FROM Student NATURAL JOIN Rents
ORDER BY street;

--5
--List all students not currently renting from any landlord.
SELECT Student.firstname, Student.lastname
FROM Student
WHERE NOT EXISTS (SELECT * FROM Student S1, Rents 
		  WHERE Student.Tnumber = S1.Tnumber AND S1.Tnumber = Rents.Tnumber);

--6
--List the SSN and number of houses owned for each landlord.
SELECT SSN, count(*)
FROM Owns
GROUP BY SSN;

--7
--List the names of all students not eligible for off-campus housing (i.e., less than Junior
--standing).
SELECT firstname, lastname
FROM Student
WHERE NOT (year = 'Senior' OR year = 'Junior');

--8
--List the pairs of Tnumbers that are mutually compatible.
SELECT wantedTnum, wantingTnum
FROM Compatible
WHERE wantingTnum in (SELECT C2.wantedTnum FROM Compatible C2
		      WHERE C2.wantingTnum = Compatible.wantedTnum);

--9
--List all houses with at least 4 bedrooms.
SELECT *
FROM House
WHERE numbeds >= 4;

--10
--List all houses owned by any landlord named Duane Baker.
SELECT street
FROM Landlord NATURAL JOIN Owns
WHERE firstname = 'Duane' AND lastname = 'Baker';

--Simple insert to add student Burton Betchart to Student relation.
insert into Student values ('T00061928','Burton','Betchart','9172247015','0238','Senior','bbetchar@oberlin.edu',NULL,NULL);

--Simple insert to add 195 N Professor to the list of houses available to college students.
insert into House values ('195 N Professor St', 5, 1);

--Insert with subquery to add Burton Betchart as Krista Egger's housemate.
insert into Rents
select street, 'T00061928'
from Rents
where Tnumber='T00042434';

--Insert with subquery to add the landlord of 216 N Main St as the landlord of 195 N Professor St.
insert into Owns
select SSN, '195 N Professor St'
from Owns
where street='216 N Main St';

--Increase the rent on all leases that include both gas and sewer.
update Lease
set rent=rent+15
where gasyn = 'Y' AND seweryn = 'Y';

--Update a specific student's class standing.
update Student
set year='Sophomore'
where Tnumber='T00067312';

--delete all landlords named Lois Lane
delete from Landlord
where firstname='Lois' AND lastname='Lane';

--delete all freshmen from the database
delete from Student
where year='Freshman';

spool off