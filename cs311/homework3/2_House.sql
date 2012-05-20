--Charles Treatman
--definition of house table

drop table ctreatma.House;

create table House (
        street varchar2(50) primary key,
        numbeds int,
        numbaths int);

describe ctreatma.House;

insert into ctreatma.House values (
	'261 N Main Street',
	'4',
	'1');

insert into ctreatma.House values (
	'27 Union Street',
	'5',
	'2');

insert into ctreatma.House values (
	'119 N Pleasant Street',
	'4',
	'2');

insert into ctreatma.House values (
	'64 N Park St',
	'4',
	'1');

insert into ctreatma.House values (
	'84 Groveland St',
	'4',
	'2');

select * from ctreatma.House;