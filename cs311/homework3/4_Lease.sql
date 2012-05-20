--Charles Treatman
--definition of table lease

drop table ctreatma.Lease;

create table Lease (
        street varchar2(50) NOT NULL,
        months int,
        rent int,
        gasYN char(1),
        sewerYN char(1),
	unique (street),
	foreign key (street) references House on delete cascade);

describe ctreatma.Lease;

insert into ctreatma.Lease values (
	'261 N Main Street',
	'10',
	'260',
	'N',
	'N');

insert into ctreatma.Lease values (
	'64 N Park St',
	'9',
	'280',
	'Y',
	'Y');

insert into ctreatma.Lease values (
	'119 N Pleasant Street',
	'10',
	'260',
	'N',
	'N');

insert into ctreatma.Lease values (
	'84 Groveland St',
	'12',
	'220',
	'N',
	'Y');

insert into ctreatma.Lease values (
	'27 Union Street',
	'9',
	'315',
	'Y',
	'Y');

select * from ctreatma.Lease;