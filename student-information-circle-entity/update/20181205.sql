insert into sys_pool value(1,now(),now(),0,false,false,10,'Kevin科技',null,false,null);

insert into sys_user(creation,last_modified,version,account,active,audited,cellphone_verified,email_verified,name,password,removed,salt,sys_id,sys_pool_id,level) 
value(now(),now(),0,'123',true,true,false,false,'Kevin','0d92d17f3dcb8eb80b2bc88edc372178485c08b9282926af88f6faaf3c3dd9f4',false,'bsffidvl0oyxnb6g','System',1,10);
---密码是 Shsun2018

insert into sys_role(creation,last_modified,version,level,name,remark,removed,sys_id) values
(now(),now(),0,9,'管理员','管理员最高权限',false,'System'),
(now(),now(),0,5,'平台审核员','审核新闻',false,'System'),
(now(),now(),0,1,'普通用户','只对自己发布的信息有修改删除权限',false,'');
---一般管理员负责可以拥有用户的所有权限，但是只可以查看角色；超级管理员拥有用户和角色的所有权限

insert into sys_user_role values(1,1);

insert into sys_permission(creation,last_modified,version,perm,category,name,remark,seq_num) values
(now(),now(),0,"information:verify","information","新闻审核","新闻审核",0),
(now(),now(),0,"information:update","information","新闻编辑","新闻编辑",0),
(now(),now(),0,"information:removed","information","新闻删除","新闻删除",0),
(now(),now(),0,"information:view","information","新闻查看","新闻查看",0),
(now(),now(),0,"information:create","information","新闻新增","新闻新增",0);

insert into sys_permission(creation,last_modified,version,perm,category,name,remark,seq_num) values
(now(),now(),0,"flexible:update","flexible","软文编辑","软文编辑",0),
(now(),now(),0,"flexible:removed","flexible","软文删除","软文删除",0),
(now(),now(),0,"flexible:view","flexible","软文查看","软文查看",0),
(now(),now(),0,"flexible:create","flexible","软文新增","软文新增",0);

insert into sys_role_permission values
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(2,1),
(3,2),
(3,3),
(3,4),
(3,5);

------创建用户
------Sic@2018
------d5ee5ebc43f50a7c7bc73ee1bc160c535bca17b8818ca7a58a62984bbb1f7b56
------ifrf585h55envlhx
select id,account from sys_user where level=1;

insert into sys_user(creation,last_modified,version,account,active,audited,cellphone_verified,email_verified,name,password,removed,salt,sys_id,sys_pool_id,level) values
(now(),now(),0,'root2018',true,true,false,false,'管理员','d5ee5ebc43f50a7c7bc73ee1bc160c535bca17b8818ca7a58a62984bbb1f7b56',false,'ifrf585h55envlhx','System',1,9),
(now(),now(),0,'verifier2018',true,true,false,false,'审核员','d5ee5ebc43f50a7c7bc73ee1bc160c535bca17b8818ca7a58a62984bbb1f7b56',false,'ifrf585h55envlhx','System',1,5),
(now(),now(),0,'writer2018',true,true,false,false,'撰稿人','d5ee5ebc43f50a7c7bc73ee1bc160c535bca17b8818ca7a58a62984bbb1f7b56',false,'ifrf585h55envlhx','',1,1);

insert into sys_user_role values
(11,1),
(12,2),
(13,3);

insert into sys_user(creation,last_modified,version,account,active,audited,cellphone_verified,email_verified,name,password,removed,salt,sys_id,sys_pool_id,level) values
(now(),now(),0,'writer01',true,true,false,false,'撰稿人01','d5ee5ebc43f50a7c7bc73ee1bc160c535bca17b8818ca7a58a62984bbb1f7b56',false,'ifrf585h55envlhx','',1,1);

insert into sys_user_role values
(27,3);

