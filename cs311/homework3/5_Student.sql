--Charles Treatman
--definition of Student table   

drop table ctreatma.Student;

create table Student (
        Tnumber varchar2(20) primary key,
        firstname varchar2(20),
        lastname varchar2(20),
        phone char(10),
        OCMR int,
        year varchar2(10),
        email varchar2(50),
        dorm varchar2(20),
        room int);

describe ctreatma.Student;

insert into ctreatma.Student values (
	'T00032696',
	'Charles',
	'Treatman',
	'4407756005',
	'2817',
	'Senior',
	'ctreatma@oberlin.edu',
	NULL,
	NULL);

insert into ctreatma.Student values (
	'T00459871',
	'Joe',
	'Bazooka',
	'4407755901',
	'2073',
	'Freshman',
	'bubblegum@yahoo.com',
	'North',
	'304');

insert into ctreatma.Student values (
	'T0011329',
	'Igor',
	'Stravinsky',
	'6106622991',
	'1702',
	'Junior',
	'igor@cs.oberlin.edu',
	'South',
	'105');

insert into ctreatma.Student values (
	'T00054342',
	'Kathryn',
	'Powers',
	'4407756005',
	'2276',
	'Senior',
	'kat4342@earthlink.net',
	NULL,
	NULL);

insert into ctreatma.Student values (
	'T0091234',
	'Freddy',
	'Moyer',
	'9172247015',
	'0912',
	'Sophomore',
	'fmoyer@oberlin.edu',
	'East',
	'204');

select * from ctreatma.Student;