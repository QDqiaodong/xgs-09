CREATE DATABASE IF NOT EXISTS journal_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE journal_db;
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  avatar VARCHAR(255),
  bio VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS journal_work;
CREATE TABLE journal_work (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  cover_image VARCHAR(500),
  content TEXT,
  layout_idea TEXT,
  color_scheme TEXT,
  inspiration TEXT,
  view_count INT DEFAULT 0,
  like_count INT DEFAULT 0,
  favorite_count INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  type VARCHAR(20) NOT NULL,
  icon VARCHAR(100),
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS work_category;
CREATE TABLE work_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_work_id (work_id),
  INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  work_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_work (user_id, work_id),
  INDEX idx_user_id (user_id),
  INDEX idx_work_id (work_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_user (username, password, nickname, bio) VALUES
('admin', '123456', '手账达人', '热爱手账，分享生活中的小美好 ✨');

INSERT INTO category (name, type, sort) VALUES
('复古风', 'style', 1),
('简约风', 'style', 2),
('可爱风', 'style', 3),
('森系', 'style', 4),
('盐系', 'style', 5),
('甜系', 'style', 6),
('暗黑系', 'style', 7),
('ins风', 'style', 8);

INSERT INTO category (name, type, sort) VALUES
('日常记录', 'scene', 1),
('节日主题', 'scene', 2),
('旅行手账', 'scene', 3),
('读书摘抄', 'scene', 4),
('美食记录', 'scene', 5),
('观影心得', 'scene', 6),
('工作计划', 'scene', 7),
('习惯打卡', 'scene', 8);

INSERT INTO journal_work (user_id, title, cover_image, content, layout_idea, color_scheme, inspiration, view_count, favorite_count, status) VALUES
(1, '春日手账 | 记录花开的美好', 
 'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&h=500&fit=crop',
 '春天来了，万物复苏，用手账记录下这个美好的季节。',
 '采用左右分栏布局，左侧放照片，右侧写文字，配上花朵贴纸装饰。',
 '主色调选用嫩粉色和草绿色，搭配白色底，整体清新自然。',
 '看到小区里的樱花盛开，突然想要记录下这美好的瞬间，于是就有了这篇手账。',
 128, 32, 1),

(1, '简约工作手账排版分享', 
 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=500&fit=crop',
 '分享一下我常用的工作手账排版方式，简单又实用。',
 '使用时间轴布局，从上到下按时间顺序记录，重要事项用便签标出。',
 '黑白灰为主色调，偶尔用蓝色标注重点，简洁大方。',
 '工作太忙，需要简单高效的记录方式，于是研究出了这套排版。',
 256, 64, 1),

(1, '可爱风生日手账制作教程', 
 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&h=500&fit=crop',
 '教大家如何制作一个可爱风的生日手账，送给好朋友的生日礼物。',
 '以居中构图为主，搭配各种可爱的装饰元素和贴纸，营造欢快氛围。',
 '粉色、黄色、淡紫色作为主色调，色彩丰富但不杂乱。',
 '好朋友生日快到了，想做一个特别的礼物，于是就有了这个手账。',
 512, 128, 1),

(1, '旅行手账 | 厦门之旅', 
 'https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=800&h=500&fit=crop',
 '记录厦门旅行的点点滴滴，鼓浪屿、曾厝垵、沙坡尾...',
 '拼贴风格，将车票、门票、照片都贴在手账上，配上文字说明。',
 '蓝色系为主，呼应海边城市的感觉，搭配一些棕色增加复古感。',
 '每次旅行回来都会做一本旅行手账，老了以后翻看都是美好的回忆。',
 384, 96, 1),

(1, '森系手账 | 秋天的味道', 
 'https://images.unsplash.com/photo-1476820865390-c52aeebb9891?w=800&h=500&fit=crop',
 '秋天是最适合做手账的季节，落叶、暖阳、桂花...',
 '自然风布局，加入干花、树叶等真实元素，页面留白较多。',
 '棕色、橙色、金黄色为主，满满的秋意。',
 '走在铺满落叶的小路上，突发奇想做一个秋天主题的手账。',
 192, 48, 1),

(1, '盐系手账 | 极简美学', 
 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&h=500&fit=crop',
 '盐系手账的特点是清淡、简约、有质感，分享一下我的做法。',
 '大量留白，文字为主，偶尔点缀一些小图案，整体干净整洁。',
 '黑白灰+米色，偶尔一点淡蓝色点缀，非常克制的色彩搭配。',
 '看多了花哨的手账，想要回归简单，于是尝试了盐系风格。',
 160, 40, 1);

INSERT INTO work_category (work_id, category_id) VALUES
(1, 3), (1, 5), (1, 9),
(2, 2), (2, 15),
(3, 3), (3, 6), (3, 10),
(4, 4), (4, 11),
(5, 4), (5, 9),
(6, 2), (6, 5);
