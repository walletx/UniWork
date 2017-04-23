-- COMP3311 16s1 Project 2
--
-- Graph Solution Template
-- Pongpol Trisuwan (z3471079)

create or replace function triangle_naive(dataset text) returns integer 
as $$
-- ... SQL statement, possibly using other functions defined by you ...
DECLARE
	triangle_count integer;
BEGIN
	
	-- list all the wedges satisfying id(v1) < id(v2) < id(v3)
	execute
	'create or replace view naive_wedge(vOne, vTwo, vThree) as
	select EOne.v1, EOne.v2, ETwo.v2
	from tbl_' || dataset || ' as EOne
	join tbl_' || dataset || ' as ETwo on (EOne.v1 = ETwo.v1) 
	where EOne.v1 < EOne.v2 and EOne.v2 < ETwo.v2'
	;
	
	-- connect the last edge to get triangle from remaining wedges
	execute 
	'select count(*) 
	from naive_wedge as W
	join tbl_' || dataset || ' as E on (W.vTwo = E.v1 and W.vThree = E.v2)'
	into triangle_count;
	
	return triangle_count;
	
END;
$$ language plpgsql;

create or replace function triangle_in_order(dataset text) returns integer
as $$
-- ... SQL statement, possibly using other functions defined by you ...
DECLARE
	triangle_count integer;
BEGIN
	
	-- create a view of a degrees for all vertices
	execute
	'create or replace view deg_tbl(degV, degree) as
	select coalesce(tblOne.v1, tblTwo.v2), coalesce(degOne + degTwo, degOne, degTwo, 0)
	from (select v1, count(v1) as degOne
	from tbl_' || dataset ||
	' group by v1) as tblOne
	full outer join
	(select v2, count(v2) as degTwo
	from tbl_' || dataset ||
	' group by v2) as tblTwo on (tblOne.v1 = tblTwo.v2)'
	;
	
	-- enforces the condition v1 < v2 that is:
	-- takes wedges where deg(v1) < deg(v2) 
	-- or in the case where deg(v1) = deg(v2) then enforce id(v1) < id(v2)
	-- using case statements so edges don't get lost (we rearrange edges so condition doesnt wrongly 'delete' them)
	execute
	'create or replace view intermmediate(v1, v2, d1, d2) as
	select
	case when (DOne.degree = DTwo.degree) then
	case when (E.v1 < E.v2) then E.v1 else E.v2 end
	when (DOne.degree < DTwo.degree) then E.v1 
	else E.v2 end,

	case when (DOne.degree = DTwo.degree) then
	case when (E.v1 < E.v2) then E.v2 else E.v1 end
	when (DOne.degree < DTwo.degree) then E.v2 
	else E.v1 end,

	case when (DOne.degree = DTwo.degree) then 
	case when (E.v1 < E.v2) then DOne.degree else DTwo.degree end
	when (DOne.degree < DTwo.degree) then DOne.degree
	else DTwo.degree end as Deg1,

	case when (DOne.degree = DTwo.degree) then
	case when (E.v1 < E.v2) then DTwo.degree else DOne.degree end
	when (DOne.degree < DTwo.degree) then DTwo.degree
	else DOne.degree end as Deg2

	from tbl_' || dataset || ' as E
	join deg_tbl as DOne on (E.v1 = DOne.degV)
	join deg_tbl as DTwo on (E.v2 = DTwo.degV)'
	;
	
	-- at this point we are guaranteed v1 < v2 and v1 < v3
	-- so we enforce v2 < v3 using where clause
	-- then we simply join the remaining wedges using the guaranteed edges
	-- the number of wedges here is significantly less than the naive version
	select count(*) into triangle_count
	from (select EOne.v1 as VOne, EOne.v2 as VTwo, ETwo.v1, ETwo.v2 as VThree
	from intermmediate as EOne
	join intermmediate as ETwo on (EOne.v1 = ETwo.v1)
	where (EOne.d2 < ETwo.d2 or (EOne.d2 = ETwo.d2 and EOne.v2 < ETwo.v2))) as W
	join intermmediate as E on (W.vTwo = E.v1 and W.vThree = E.v2)
	;
	
	return triangle_count;
	
END;
$$ language plpgsql;