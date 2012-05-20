--Charles Treatman
--definition of the compatible table
--recursive relation connecting students to students

drop table ctreatma.Compatible;

create table Compatible (
	wantingTnum varchar2(20),
	wantedTnum varchar2(20),
	foreign key (wantingTnum) references Student(Tnumber) on delete cascade,
	foreign key (wantedTnum) references Student(Tnumber) on delete cascade);

describe ctreatma.Compatible;

insert into ctreatma.Compatible values (
	'T00032696',
	'T00054342');

insert into ctreatma.Compatible values (
	'T00054342',
	'T00032696');

insert into ctreatma.Compatible values (
	'T00032696',
	'T0011329');

insert into ctreatma.Compatible values (
	'T00032696',
	'T00459871');

insert into ctreatma.Compatible values (
	'T0011329',
	'T00054342');

select * from ctreatma.Compatible;