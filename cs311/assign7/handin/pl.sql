--Charles Treatman
--PL/SQL Program
--Creates a new table LandlordIncome showing name, ssn, and projected
--income for Oberlin landlords.  Projected income is the income generated
--if the landlord manages to fill all of his/her vacancies.
spool pl.lst

select * from landlordincome;

create table LandlordIncome (
	firstname varchar2(20),
	lastname varchar2(20),
	ssn char(9),
	income int);

--create function calc_num_houses ( s in owns.ssn%type ) return integer
--as
--	i integer;
--begin
--	select count(*) into i
--	from owns
--	group by ssn
--	having ssn = s;
--	return i;
--exception
--	when no_data_found then return 0;
--end;

declare
	f landlord.firstname%type;
	l landlord.lastname%type;
	s landlord.ssn%type;
	i int;
	cursor c is select firstname, lastname, ssn
		    from landlord;
	function calc_income(s in owns.ssn%type) return int as
		r lease.rent%type;
		m lease.months%type;
		sa owns.ssn%type;
		i int;
		cursor b is select rent, months, ssn
			    from owns natural join lease;
	begin
		open b;
		i := 0;
		loop
			fetch b into r, m, sa;
			exit when b%notfound;
			if sa=s then i := i + (r * m);
			end if;
		end loop;
		return i;
	exception
		when no_data_found then return 0;
	end;


begin
	open c;
	loop
		fetch c into f, l, s;
		exit when c%notfound;
		i := calc_income(s);
		insert into LandlordIncome values (f, l, s, i);
	end loop;
end;

.
run


select * from landlordincome;

spool off