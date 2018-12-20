update information set amount=10,cost_times=3 ;

update information_type set active=false where code='Recommend';

insert into information_type(creation,last_modified,version,active,code,name,removed) values
(now(),now(),0,true,'Headlines','校园头条',false),
(now(),now(),0,true,'Education','教育',false),
(now(),now(),0,true,'Movies','影视',false),
(now(),now(),0,true,'ESports','电竞',false),
(now(),now(),0,true,'Constellation','星座',false);