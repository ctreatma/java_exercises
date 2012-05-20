--Charles Treatman    
--definition of owns table

drop table ctreatma.Owns;

create table Owns (
        SSN char(9) NOT NULL,
        street varchar2(50) NOT NULL,
	foreign key (SSN) references Landlord on delete cascade,
	foreign key (street) references House on delete cascade);

describe table ctreatma.Owns;

insert into ctreatma.Owns values (
	'112233344',
	'84 Groveland St');

insert into ctreatma.Owns values (
	'009234122',
	'64 N Park St');

insert into ctreatma.Owns values (
	'105679998',
	'27 Union Street');

insert into ctreatma.Owns values (
	'119864543',
	'119 N Pleasant Street');

insert into ctreatma.Owns values (
	'009234122',
	'261 N Main Street');

select * from ctreatma.Owns;