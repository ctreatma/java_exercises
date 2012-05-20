spool view.lst

--Charles Treatman
--Assign 7-1:  Views


--This view lists all tenants of each house.  This would allow landlords
--to keep track of their tenants, and find contact info for each one when
--necessary.  It also allows students to look up people indirectly, i.e.
--if you need contact info for someone but you only know the name of his/her
--housemate, this view makes that info easier to find.
create view Housemates as
	select street, firstname, lastname, phone
	from Rents NATURAL JOIN Student
	order by street;


--This view lists the number of beds in each house, and the number of tenants
--renting the house.  This allows students who get off-campus but are having
--trouble finding people to live with to find vacancies around town.  It also
--allows groups of students to find an available and appropriately sized house
--with relative ease.
create view Available as
	(select street, avg(numbeds) as numbeds, count(*) as numtaken
	from House NATURAL JOIN Rents
	group by street)
	union
	(select street, numbeds, 0 as numtaken from House
	where not exists (select * from Rents where street = House.street));

--list the name of all tenants of 261 N Main
select firstname, lastname
from Housemates
where street = '261 N Main Street';

--list all residences with exactly one open room
select street
from Available
where numbeds-numtaken=1;

spool off