--Charles Treatman
--Modified schema file

drop table ctreatma.Owns;
drop table ctreatma.Rents;
drop table ctreatma.Lease;
drop table ctreatma.House;
drop table ctreatma.Landlord;
drop table ctreatma.Compatible;
drop table ctreatma.Student;

create table Landlord (
        SSN  char(9) primary key,
        firstname varchar2(20),
        lastname varchar2(20),
        phone char(10),
        email varchar2(50));

describe ctreatma.Landlord;

create table House (
        street varchar2(50) primary key,
        numbeds int,
        numbaths int);

describe ctreatma.House;

create table Owns (
        SSN char(9) NOT NULL,
        street varchar2(50) NOT NULL,
        foreign key (SSN) references Landlord,
        foreign key (street) references House);

describe ctreatma.Owns;

create table Lease (
        street varchar2(50) NOT NULL,
        months int,
        rent int,
        gasYN char(1),
        sewerYN char(1),
        unique (street),
        foreign key (street) references House);

describe ctreatma.Lease;

create table Student (
        Tnumber varchar2(20) primary key,
        firstname varchar2(20),
        lastname varchar2(20),
        phone char(10),
        OCMR int,
        year varchar(10) check(year in ('Freshman', 'Sophomore', 'Junior', 'Senior', '5th Year')),
        email varchar2(50),
        dorm varchar2(20),
        room int);

describe ctreatma.Student;

create table Rents (
        street varchar2(50) NOT NULL,
        Tnumber varchar2(20) NOT NULL,
        foreign key (street) references Lease(street),
        foreign key (Tnumber) references Student);

describe ctreatma.Rents;

create table Compatible (
        wantingTnum varchar2(20),
        wantedTnum varchar2(20),
        foreign key (wantingTnum) references Student(Tnumber),
        foreign key (wantedTnum) references Student(Tnumber),
        check(wantingTnum <> wantedTnum));

describe ctreatma.Compatible;