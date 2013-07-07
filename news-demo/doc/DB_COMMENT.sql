/*
 Navicat Premium Data Transfer

 Source Server         : 120.197.94.243-测试
 Source Server Type    : MySQL
 Source Server Version : 50045
 Source Host           : 120.197.94.243
 Source Database       : DB_COMMENT

 Target Server Type    : MySQL
 Target Server Version : 50045
 File Encoding         : utf-8

 Date: 07/07/2013 15:36:09 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `tb_comment_audit`
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment_audit`;
CREATE TABLE `tb_comment_audit` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '评论类型',
  `entry_id` bigint(20) NOT NULL,
  `comment_id` int(11) NOT NULL COMMENT '评论id',
  `status` int(11) NOT NULL COMMENT '评论审核状态 0 入库; 1 人工删除; 2 自动过滤; 3 自动屏蔽; 10 审核通过',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`),
  KEY `idx_type_status` (`type_id`,`status`,`create_time`),
  KEY `idx_type_comment` (`type_id`,`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=934 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_comment_audit_result`
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment_audit_result`;
CREATE TABLE `tb_comment_audit_result` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '业务类型',
  `comment_id` int(11) NOT NULL COMMENT '评论Id',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL,
  `author_uid` int(11) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) default '' COMMENT '评论用户nickname',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  `status` int(11) NOT NULL COMMENT '审核失败状态，1 人工删除，2 自动过滤，3 自动屏蔽',
  PRIMARY KEY  (`id`),
  KEY `INDEX_COMMENT` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2247479 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_comment_blackuser`
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment_blackuser`;
CREATE TABLE `tb_comment_blackuser` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `uid` int(11) NOT NULL COMMENT '黑名单用户id',
  `nickname` varchar(100) default '' COMMENT '黑名单用户nickname',
  `reason` varchar(2000) default '' COMMENT '涉黑原因',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `days` int(11) NOT NULL default '-1',
  `status` int(11) NOT NULL COMMENT '黑名单状态',
  PRIMARY KEY  (`id`),
  KEY `idx_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_component_type`
-- ----------------------------
DROP TABLE IF EXISTS `tb_component_type`;
CREATE TABLE `tb_component_type` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `name` varchar(50) NOT NULL COMMENT '务业类别名称',
  `create_time` datetime NOT NULL COMMENT '评论类别创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_custom_up`
-- ----------------------------
DROP TABLE IF EXISTS `tb_custom_up`;
CREATE TABLE `tb_custom_up` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '论评业务类型',
  `comment_id` int(20) NOT NULL COMMENT '评论对象id',
  `up_esid` varchar(80) NOT NULL COMMENT '用户评论esid',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_entry_uid` (`type_id`,`comment_id`,`up_esid`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_duanzi_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_duanzi_comment`;
CREATE TABLE `tb_duanzi_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL,
  `author_uid` int(11) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_fun_image_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_fun_image_comment`;
CREATE TABLE `tb_fun_image_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL COMMENT '评论对象id',
  `author_uid` int(20) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_image_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_image_comment`;
CREATE TABLE `tb_image_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL,
  `author_uid` int(20) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_createtime` (`create_time`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_manhua_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_manhua_comment`;
CREATE TABLE `tb_manhua_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL,
  `author_uid` int(11) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_news_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_news_comment`;
CREATE TABLE `tb_news_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL COMMENT '评论对象id',
  `author_uid` int(20) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_novel_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_novel_comment`;
CREATE TABLE `tb_novel_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL,
  `author_uid` int(11) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`),
  KEY `idx_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4051676 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_shopping_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_shopping_comment`;
CREATE TABLE `tb_shopping_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL COMMENT '评论对象id',
  `author_uid` int(20) NOT NULL COMMENT '评论用户uid',
  `author_nickname` varchar(100) NOT NULL COMMENT '评论用户nickname',
  `author_profile` varchar(64) default NULL COMMENT '用户头像',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_type` int(11) NOT NULL COMMENT '回复类型：1 评论 2 回复',
  `parent_cid` int(11) default '0' COMMENT '回复的parent comment id 当reply_type为回复时',
  `wonderful` bit(1) NOT NULL COMMENT '是否精彩评论，0 一般， 1 精彩评论，默认为0',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  `up_count` int(11) NOT NULL COMMENT '获赞次数',
  `reply_count` int(11) NOT NULL COMMENT '回复次数',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  PRIMARY KEY  (`id`),
  KEY `idx_parent_id` (`parent_cid`,`id`),
  KEY `idx_entry_reply` (`entry_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_category_reply` (`category_id`,`reply_type`,`id`,`wonderful`),
  KEY `idx_entry_wonderful` (`entry_id`,`wonderful`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_user_comment`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_comment`;
CREATE TABLE `tb_user_comment` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '论评业务类型',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` bigint(20) NOT NULL COMMENT '评论对象id',
  `comment_id` int(11) NOT NULL COMMENT '评论id',
  `comment_uid` int(11) NOT NULL COMMENT '用户评论uid',
  `content` varchar(2000) NOT NULL COMMENT '评论内容',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  PRIMARY KEY  (`id`),
  KEY `idx_uid_type_entry` (`comment_uid`,`type_id`,`entry_id`),
  KEY `idx_uid_id` (`comment_uid`,`id`),
  KEY `idx_uid_type_id` (`comment_uid`,`type_id`,`id`),
  KEY `idx_commentid` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3926301 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_user_reply`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_reply`;
CREATE TABLE `tb_user_reply` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '评论业务类型',
  `category_id` int(11) NOT NULL COMMENT '评论对象所属分类,默认为0无分类',
  `entry_id` int(20) NOT NULL COMMENT '评论对象id',
  `comment_id` int(11) NOT NULL COMMENT '评论id',
  `comment_uid` int(11) NOT NULL COMMENT '评论uid',
  `comment_nickname` varchar(100) default '',
  `comment_content` varchar(2000) NOT NULL COMMENT '评论内容',
  `reply_id` int(11) NOT NULL COMMENT '回复id',
  `reply_uid` int(11) NOT NULL COMMENT '回复者uid',
  `reply_nickname` varchar(100) default '' COMMENT '回复者nickname',
  `reply_profile` varchar(64) default NULL,
  `reply_content` varchar(2000) NOT NULL default '' COMMENT '回复内容',
  `has_read` bit(1) NOT NULL COMMENT '0 代表未读， 1代表已读',
  `generate_src` varchar(300) default '' COMMENT '记录产生内容来源 app手机型号，online',
  `create_time` datetime NOT NULL COMMENT '回复时间',
  PRIMARY KEY  (`id`),
  KEY `idx_reply_type_cmt` (`reply_uid`,`type_id`,`comment_id`),
  KEY `idx_comment` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=125738 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tb_user_up`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_up`;
CREATE TABLE `tb_user_up` (
  `id` int(11) NOT NULL auto_increment COMMENT 'PK',
  `type_id` int(11) NOT NULL COMMENT '论评业务类型',
  `comment_id` int(20) NOT NULL COMMENT '评论对象id',
  `up_uid` int(20) NOT NULL COMMENT '用户评论uid',
  `create_time` datetime NOT NULL COMMENT '评论创建时间',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_entry_uid` (`type_id`,`comment_id`,`up_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
