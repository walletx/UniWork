-- COMP3311 16s1 Project 2
--
-- MyMyUNSW Solution Template
-- Pongpol Trisuwan (z3471079)



-- Q1: ...

-- Function from proj1.sql for semester abbreviation
create or replace function
	semesterabbrev(integer) returns text
as
$$
select substring(cast(Semesters.year as text) from 3 for 2) || lower(Semesters.term)
from Semesters
where Semesters.id = $1
$$ language sql;

-- Only returns results where course_enrolments and program_enrolment exist in the same semester
-- Treats other cases as invalid
create or replace view Q1MainView(peopleid, semabbrev, subcode, courseid, progcode, subname, coursemark, coursegrade, subuoc)
as
select People.unswid, Semesters.id, Subjects.code, Courses.id, Programs.code,  
Subjects.name, Course_enrolments.mark, Course_enrolments.grade, Subjects.uoc
from People
join Course_enrolments on (People.id = Course_enrolments.student) --mark, grade
join Courses on (Course_enrolments.course = Courses.id) --course, uoc
join Subjects on (Courses.subject = Subjects.id) -- code,
join Semesters on (Courses.semester = Semesters.id)
join Program_enrolments on (Program_enrolments.student = People.id) 
join Programs on (Program_enrolments.program = Programs.id) -- prog
where Program_enrolments.semester = Courses.semester
;

create type TranscriptRecord as (code text, term text, course integer, prog text, name text, mark integer, grade text, uoc integer, rank integer, totalEnrols integer);
create or replace function Q1(integer)
	returns setof TranscriptRecord
as $$
-- ... one SQL statement, possibly using other functions defined by you ...
DECLARE
	transcript TranscriptRecord;
	studentid alias for $1;
	rankvar integer;
	enrolvar integer;
	r Q1MainView%ROWTYPE;
BEGIN
	
	-- get tuples for specific student then put into transcript
	
	for r in
	select *  
    from Q1MainView
	where Q1MainView.peopleid = studentid
	loop
			
		transcript.code := r.subcode;
		transcript.term := semesterabbrev(r.semabbrev);
		transcript.course := r.courseid;
		transcript.prog := r.progcode;
		transcript.name := r.subname;
		transcript.mark := r.coursemark;
		transcript.grade := r.coursegrade;
		
		
		-- Awards uoc for courses which have been passed
		-- Assume other 'non-common grades' (not FL,UF,PC,PS,CR,DN,HD) are all null
		if (r.coursegrade != 'SY' and r.coursegrade != 'PC' and
		r.coursegrade != 'PS' and r.coursegrade != 'CR' and
		r.coursegrade != 'DN' and r.coursegrade != 'HD' and
		r.coursegrade != 'PT' and r.coursegrade != 'A' and
		r.coursegrade != 'B' and r.coursegrade != 'C') and r.coursemark is not null then
			transcript.uoc := 0;
		else
			transcript.uoc := r.subuoc;
		end if;
		
				
		-- if no mark then no rank else give rank for course
		if r.coursemark is null then
			transcript.rank := null;	
		else
			select ranks into transcript.rank
			from ( select People.unswid as peopleid, Course_enrolments.course as courseid, 
				rank() over (partition by Course_enrolments.course order by Course_enrolments.mark desc nulls last)  as ranks
			from People
			join Course_enrolments on (People.id = Course_enrolments.student)
			where Course_enrolments.course = r.courseid) as I
			where I.peopleid = r.peopleid;
		end if;
		
		-- get total enrolments for specific course
		select count(*) into transcript.totalEnrols
		from Course_enrolments
		where Course_enrolments.mark is not null
		and Course_enrolments.course = r.courseid
		group by Course_enrolments.course;
		
		return next transcript;
	end loop;
	return;
END;
$$ language plpgsql;



-- Q2: ...
create type MatchingRecord as ("table" text, "column" text, nexamples integer);
create or replace function Q2("table" text, pattern text) 
	returns setof MatchingRecord
as $$
-- ... one SQL statement, possibly using other functions defined by you ...
DECLARE
	matchrec MatchingRecord;
	colcount integer;
	colname text;
BEGIN

	-- get column names
	-- then inside loop check each column for pattern 
	for colname in 	
	select column_name
	from information_schema.columns
	where table_name = "table" 
	loop
		
		-- pattern matching
		execute 'select count(*)
		from ' || "table" ||
		' where cast(' || colname || ' as text) ~ ''' || pattern || ''''
		into colcount;
		continue when colcount = 0;
		
		matchrec.table = "table";
		matchrec.column = colname;
		matchrec.nexamples = colcount;
		
		return next matchrec;
	end loop;
	return;
END;
$$ language plpgsql;


-- Q3: ...

-- view representing staff and affiliations
create or replace view Q3MainView(unswid, staffname, sortname, stafforg, staffrole, rolestart, roleend)
as
select People.unswid, People.name, People.sortname, Affiliations.orgUnit, Affiliations.role, Affiliations.starting, Affiliations.ending
from People
join Affiliations on (People.id = Affiliations.staff)
;


create type EmploymentRecord as (unswid integer, name text, roles text);
create or replace function Q3(integer) 
	returns setof EmploymentRecord 
as $$
-- ... one SQL statement, possibly using other functions defined by you ...
DECLARE
	emprec EmploymentRecord;
	orgid alias for $1;
	r Q3MainView%ROWTYPE;
	currstartdate date default null;
	prevenddate date default null;
	currunswid integer default null;
	prevunswid integer default null;
	orgname text;
	staffposition text;
BEGIN

	-- create table of all child organisation recursively
	
	drop table if exists temporgs;
	
	create temp table temporgs on commit drop as	
	with recursive childOrg(member, owner) as (
	    	select member, owner
			from OrgUnit_groups 
			where owner = orgid
		union all
	    	select OrgUnit_groups.member, OrgUnit_groups.owner
	    	from childOrg, OrgUnit_groups
	    	where OrgUnit_groups.owner = childOrg.member
	  )
	select member
	from childOrg;
	
	-- insert parent organisation since above does not take that into account
	insert into temporgs values (orgid);
	
	-- get rid of staff that do not belong to org and do not appear at least twice
	-- then inside loop check if position concurrent or not
	
	for r in
	select *
	from Q3MainView as a
	inner join (select unswid 
		from Q3MainView as b
		inner join temporgs on (temporgs.member = b.stafforg)
		group by unswid
		having count(unswid) > 1) as b on (a.unswid = b.unswid)
	order by a.sortname, a.rolestart
	loop
		
		currunswid := r.unswid;
		currstartdate := r.rolestart;
		
		-- first entry case		
		if prevunswid is null then
			prevunswid := r.unswid;
			prevenddate := r.roleend;
			continue;
		end if;
		
		-- reset when change staff		
		if prevunswid != currunswid then
			prevunswid := r.unswid;
			prevenddate := r.roleend;
			
			select OrgUnits.name into orgname
			from OrgUnits
			where OrgUnits.id = r.stafforg;
			
			select Staff_roles.name into staffposition
			from Staff_roles
			where Staff_roles.id = r.staffrole;
					
			if emprec.unswid is not null then
				return next emprec;
			end if;
			emprec := null;
			
			-- assign for first iteration (for a new staff) case
			emprec.roles := staffposition || ', ' || orgname || ' (' || r.rolestart || '..' || r.roleend || ')' || E'\n'; 
			continue;
		end if;
		
		-- check staff role concurrency
		if prevenddate <= currstartdate and prevenddate is not null then
				
			emprec.unswid := r.unswid;
			emprec.name := r.staffname;
			
			select OrgUnits.name into orgname
			from OrgUnits
			where OrgUnits.id = r.stafforg;
			
			select Staff_roles.name into staffposition
			from Staff_roles
			where Staff_roles.id = r.staffrole;
			
			-- in the case that latest position has not ended then do not concat r.roleend (since it is NULL)
			if r.roleend is null then
				emprec.roles := emprec.roles || staffposition || ', ' || orgname || ' (' || r.rolestart || '..)' || E'\n'; 
			else
				emprec.roles := emprec.roles || staffposition || ', ' || orgname || ' (' || r.rolestart || '..' || r.roleend || ')' || E'\n'; 
			end if;	
		end if;
	end loop;
	
	-- last user case
	if emprec.unswid is not null then
		return next emprec;
	end if;
	return;
END;
$$ language plpgsql;