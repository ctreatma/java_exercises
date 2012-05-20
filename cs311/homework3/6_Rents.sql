--Charles Treatman
--definition of Rents table

drop table ctreatma.Rents;

create table Rents (
        street varchar2(50) NOT NULL,
        Tnumber varchar2(20) NOT NULL,
	foreign key (street) references Lease(street) on delete cascade,
	foreign key (Tnumber) references Student on delete cascade);

describe ctreatma.Rents;

insert into ctreatma.Rents values (
	'261 N Main Street',
	'T00032696');

insert into ctreatma.Rents values (
	'261 N Main Street',
	'T00054342');

insert into ctreatma.Rents values (
	'119 N Pleasant Street',
	'T00054342');

insert into ctreatma.Rents values (
	'64 N Park St',
	'T0011329');

insert into ctreatma.Rents values (
	'261 N Main Street',
	'T0091234');

select * from ctreatma.Rents;