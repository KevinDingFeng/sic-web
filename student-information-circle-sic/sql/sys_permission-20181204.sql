# Host: 192.168.3.7:4406  (Version 5.7.22-log)
# Date: 2018-12-04 14:32:15
# Generator: MySQL-Front 6.0  (Build 2.20)


#
# Data for table "sys_permission"
#

REPLACE INTO `sys_permission` 
(`id`,`creation`,`last_modified`,`version`,`category`,`name`,`perm`,`remark`,`seq_num`) 
VALUES 
(1,now(),now(),0,'root','administrator','root','管理员',1),
(2,now(),now(),0,'information','verify','information:verify','新闻审核',2),
(3,now(),now(),0,'information','update','information:update','新闻编辑',3),
(4,now(),now(),0,'information','removed','information:removed','新闻删除',4),
(5,now(),now(),0,'information','view','information:view','新闻查看',5),
(6,now(),now(),0,'information','create','information:create','新闻新增',6);
