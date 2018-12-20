# Host: 192.168.3.7:4406  (Version 5.7.22-log)
# Date: 2018-12-04 14:32:29
# Generator: MySQL-Front 6.0  (Build 2.20)


#
# Data for table "sys_role"
#

REPLACE INTO `sys_role` 
(`id`,`creation`,`last_modified`,`version`,`level`,`name`,`remark`,`removed`,`sys_id`) 
VALUES 
(1,now(),now(),0,9,'管理员','管理员最高权限',b'0','9'),
(2,now(),now(),0,5,'平台审核员','审核新闻',b'0','5'),
(3,now(),now(),0,1,'普通用户','只对自己发布的信息有修改删除权限',b'0','1');
