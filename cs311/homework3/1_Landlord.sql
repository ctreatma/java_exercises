--Charles Treatman
--definition of landlord table

drop table ctreatma.Landlord;

create table Landlord (
        SSN  char(9) primary key,
        firstname varchar2(20),
        lastname varchar2(20),
        phone char(10),
        email varchar2(50));

describe ctreatma.Landlord;
	
insert into ctreatma.Landlord values (
	'112233344',
	'Barry',
	'Gibbs',
	'4407744174',
	'bgibbs@earthlink.net');

insert into ctreatma.Landlord values (
	'091876543',
	'Lois',
	'Lane',
	'6107242990',
	'lane@dailyplanet.com');

insert into ctreatma.Landlord values (
	'105679998',
	'James',
	'Brown',
	'7182389098',
	'giveupdafunk@hotmail.com');

insert into ctreatma.Landlord values (
	'009234122',
	'Doctor',
	'Frankenstein',
	'3138722442',
	'itsalive@hotfromtransylvania.com');

insert into ctreatma.Landlord values (
	'119864543',
	'Toby',
	'Tyler',
	'4152607089',
	'ijoinedthecircus@sybase.com');

select * from ctreatma.Landlord;