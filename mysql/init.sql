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
  cover_type TINYINT DEFAULT 1 COMMENT '1:横向拼贴 3:2, 2:竖向整页 3:4, 3:局部特写 1:1',
  content TEXT,
  layout_idea TEXT,
  layout_config TEXT,
  layout_template_id BIGINT COMMENT '关联的排版模板ID',
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
  INDEX idx_create_time (create_time),
  INDEX idx_layout_template_id (layout_template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  type VARCHAR(20) NOT NULL,
  icon VARCHAR(100),
  description VARCHAR(500),
  banner_color VARCHAR(100),
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS scene_task;
CREATE TABLE scene_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scene_category_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  prompt_tips TEXT,
  layout_suggestion VARCHAR(255),
  icon VARCHAR(100),
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_scene_category_id (scene_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS work_category;
CREATE TABLE work_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_work_category (work_id, category_id),
  INDEX idx_work_id (work_id),
  INDEX idx_category_id (category_id),
  INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS work_element;
CREATE TABLE work_element (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  category TINYINT NOT NULL,
  name VARCHAR(100),
  description VARCHAR(500),
  quantity INT DEFAULT 1,
  position VARCHAR(100),
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_work_id (work_id),
  INDEX idx_category (category)
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

DROP TABLE IF EXISTS color_palette;
CREATE TABLE color_palette (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  style_description VARCHAR(500),
  color_scheme TEXT NOT NULL,
  cover_color VARCHAR(20),
  category_ids VARCHAR(255),
  use_count INT DEFAULT 0,
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_sort (sort),
  INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS work_scene_task_check;
CREATE TABLE work_scene_task_check (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  scene_task_id BIGINT NOT NULL,
  scene_category_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  checked TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_work_task (work_id, scene_task_id),
  INDEX idx_work_id (work_id),
  INDEX idx_scene_task_id (scene_task_id),
  INDEX idx_scene_category_id (scene_category_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS scene_style_relation;
CREATE TABLE scene_style_relation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scene_category_id BIGINT NOT NULL COMMENT '场景分类ID',
  style_category_id BIGINT NOT NULL COMMENT '风格分类ID',
  match_score INT DEFAULT 5 COMMENT '匹配度评分 1-10，越高越推荐',
  is_primary TINYINT DEFAULT 0 COMMENT '是否为该场景的首选风格 1是 0否',
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_scene_style (scene_category_id, style_category_id),
  INDEX idx_scene_category_id (scene_category_id),
  INDEX idx_style_category_id (style_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景与风格关联表';

DROP TABLE IF EXISTS layout_template;
CREATE TABLE layout_template (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码，如 twoColumnLeftImage',
  template_name VARCHAR(100) NOT NULL COMMENT '模板名称，如 左图右文双栏',
  template_type VARCHAR(20) NOT NULL COMMENT '模板类型：twoColumn双栏/collage拼贴/minimalist留白/bordered边框/timeline时间轴/centerFocus居中/magazine杂志风/natural自然',
  description VARCHAR(500) COMMENT '模板描述',
  preview_image VARCHAR(500) COMMENT '预览图URL',
  layout_config TEXT NOT NULL COMMENT '布局配置JSON',
  style_tags VARCHAR(255) COMMENT '适用风格标签，逗号分隔',
  scene_tags VARCHAR(255) COMMENT '适用场景标签，逗号分隔',
  use_count INT DEFAULT 0 COMMENT '使用次数',
  is_preset TINYINT DEFAULT 1 COMMENT '是否系统预设 1是 0否',
  sort INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_template_type (template_type),
  INDEX idx_is_preset (is_preset)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排版模板档案表';

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

INSERT INTO category (name, type, icon, description, banner_color, sort) VALUES
('日常记录', 'scene', '📝', '记录生活中的点滴小事，平凡日子里的闪光时刻', 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)', 1),
('节日主题', 'scene', '🎉', '记录节日的温馨与喜悦，让每一个纪念日都值得珍藏', 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)', 2),
('旅行手账', 'scene', '✈️', '用手账丈量世界，记录旅途中的风景与人情味', 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 3),
('读书摘抄', 'scene', '📚', '在书页间与灵魂对话，摘录触动心弦的文字', 'linear-gradient(135deg, #fddb92 0%, #d1fdff 100%)', 4),
('美食记录', 'scene', '🍜', '用舌尖品味生活，记录每一餐的满足与感动', 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)', 5),
('观影心得', 'scene', '🎬', '记录光影中的故事与感悟，保留影片带来的情感共鸣', 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', 6),
('工作计划', 'scene', '💼', '规划目标、复盘成长，让每一天都充实而有意义', 'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)', 7),
('习惯打卡', 'scene', '✅', '坚持与自律，见证每天进步一小步的力量', 'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)', 8);

INSERT INTO scene_task (scene_category_id, title, description, prompt_tips, layout_suggestion, icon, sort) VALUES
(1, '今日心情记录', '用文字和简单插画记录今天的心情变化，捕捉情绪的流动', '今天发生了什么让你开心的小事？有没有什么小烦恼？用颜色来表达心情吧。可以画一个简单的表情，配上几句话。', '三栏式布局：左侧日期天气，中间心情插画，右侧文字描述', '😊', 1),
(1, '周度生活回顾', '回顾这一周的生活点滴，选出印象最深刻的3件事', '这一周最大的收获是什么？有什么遗憾？下周想做什么改变？可以用贴纸和时间轴来组织内容。', '时间轴布局 + 贴纸拼贴', '📅', 2),
(1, '晨间日记', '早起的第一笔，记录梦境、灵感或今日期待', '昨晚做了什么梦？今天最期待什么？给自己一句鼓励的话。配以清新简洁的排版。', '一页式留白排版，大面积空白 + 手写字体', '🌅', 3),
(1, '睡前碎碎念', '入睡前的随想，记录一天中没说出口的话', '今天最感激的一件事？有什么想对明天的自己说的？用温柔的色调和贴纸营造氛围。', '手写信格式，配以小装饰边框', '🌙', 4),

(2, '春节手账特辑', '记录春节的团圆时光，贴上年味满满的照片和贴纸', '年夜饭吃了什么？收到压岁钱了吗？和家人有什么有趣的对话？可以贴上红包、福字、照片。', '红色主题拼贴，大量中国红元素，左右分栏', '🧧', 1),
(2, '生日派对记录', '记录生日这一天的特别时刻，让这个日子有仪式感', '许了什么愿望？收到了什么礼物？和谁一起庆祝？贴上生日贺卡、蛋糕照片。', '居中构图，彩带装饰，数字高亮', '🎂', 2),
(2, '情人节专属', '记录甜蜜的二人时光或单身的浪漫仪式感', '约会去了哪里？对方做了什么让你感动的事？一个人也要好好爱自己哦。', '粉紫色系，心形装饰，双栏对话式排版', '💝', 3),
(2, '圣诞节日志', '记录圣诞季的温馨，从装饰圣诞树到交换礼物', '圣诞愿望清单是什么？怎么度过圣诞节的？贴上圣诞树、雪花贴纸、礼物包装纸。', '绿红金三色搭配，雪花边框，拼贴风格', '🎄', 4),

(3, '旅行出发前准备', '写下出发前的期待与准备清单，期待值拉满', '目的地是哪里？要带什么行李？有哪些必去的地方？可以贴上机票、车票、行程单。', '清单式布局 + 地图装饰 + 便签', '🎒', 1),
(3, '每日行程记录', '用手账记录旅行中的每一天，图文并茂', '今天去了哪些地方？吃了什么好吃的？遇到了什么有趣的人？贴上景区门票、照片。', '图文混排拼贴风，多图多便签', '🗺️', 2),
(3, '美食探店合集', '旅途中的美食不能错过，每家店都值得被记录', '店名、地址、点了什么、口味评分、人均消费？贴上餐巾纸、菜单、美食照片。', '网格布局，每个餐厅一个小卡片', '🍽️', 3),
(3, '旅行感悟总结', '旅行结束后的总结，记录这次旅程的收获与感动', '最大的收获是什么？最难忘的瞬间？下次旅行想去哪里？配以手绘地图和盖章。', '总结页式排版，大面积文字 + 装饰', '💫', 4),

(4, '书中金句摘录', '摘抄书中打动你的句子，配上自己的解读', '书名、作者、页码、原文摘录、为什么这句话打动了你？配以简单的书本插画。', '左右两栏：左侧引用，右侧感悟', '✍️', 1),
(4, '读书心得笔记', '读完一本书后的完整读后感，记录思考与启发', '这本书讲了什么？核心观点是什么？对你有什么启发？配以封面插画和标签。', '卡片式分点布局，配以书脊装饰', '📖', 2),
(4, '本月书单分享', '分享这个月读的书，给每本书写一句短评', '读了几本书？最推荐哪本？下个月的阅读计划是什么？配以书籍封面拼贴。', '书籍排列式布局，封面缩略图 + 星级评分', '📚', 3),
(4, '角色人物分析', '对书中印象深刻的人物进行分析和记录', '人物性格、经典台词、人物关系图、你的看法？配以人物插画和关系连线。', '思维导图式布局 + 人物小卡片', '👤', 4),

(5, '早餐打卡', '记录每天的早餐，开启元气满满的一天', '今天早餐吃了什么？在哪里吃的？自己做的还是外卖？配上美食照片和食谱小贴士。', '网格布局，每日一格，配以食物简笔画', '🍳', 1),
(5, '探店美食测评', '详细记录一家餐厅的用餐体验，给其他吃货参考', '店名、环境、服务、每道菜的点评、性价比？贴上菜单、小票、美食照片。', '评分卡式布局，多维度雷达图展示', '⭐', 2),
(5, '家常菜食谱', '记录自己做的菜，写上配方和步骤供日后参考', '菜名、食材清单、步骤、小贴士、成品照片？配以手绘食材和步骤图。', '步骤式纵向排版，分步配图', '👨‍🍳', 3),
(5, '下午茶时光', '记录悠闲的下午茶时刻，甜点配咖啡的小确幸', '咖啡馆名称、环境氛围、点了什么、搭配的书或音乐？贴上杯垫、糖包、照片。', '文艺风左右排版，柔和色调，大量留白', '☕', 4),

(6, '电影观后感', '看完一部电影后的完整观感，记录情绪与思考', '片名、导演、主演、剧情简介、打动你的场景、主题思考？贴电影票根、海报。', '海报大图 + 影评文字，双栏排版', '🎞️', 1),
(6, '剧集追剧记录', '追一部剧的过程记录，每集的感想和推测', '剧名、集数、当前进度、每集亮点、剧情预测？贴剧照和剧集海报。', '时间轴+进度条展示，分集卡片式', '📺', 2),
(6, '纪录片札记', '看完纪录片的知识笔记和感悟', '片名、主题、知识点摘录、你的思考、推荐理由？配以数据图表和地图。', '信息图风格，数据可视化 + 引用框', '🎓', 3),
(6, '本月观影总结', '汇总本月看过的所有影片，做一个观影报告', '观影数量、类型分布、最佳影片、年度榜单进度？配以海报拼贴和统计图表。', '数据看板式布局，图表+评分列表', '🏆', 4),

(7, '周工作计划', '规划一周的工作任务，有条不紊地推进', '本周目标、每日待办、重点项目、时间分配？配以进度条和优先级标记。', '甘特图风格，分日分任务块', '📋', 1),
(7, '月度目标拆解', '把月度大目标拆解成可执行的小任务', '本月核心目标、关键结果、里程碑、每周计划？配以目标追踪进度环。', '目标树状图 + 里程碑时间轴', '🎯', 2),
(7, '工作复盘总结', '周/月末复盘，总结经验教训，持续改进', '完成了什么？哪些做得好？哪些需要改进？下一步计划？配以数据对比。', 'SWOT分析布局，四象限分区', '🔍', 3),
(7, '灵感创意收集', '工作中闪现的灵感和好点子，随时记录', '创意来源、适用场景、具体想法、可实施性？贴上便签、草图、剪报。', '便签墙风格，彩色便签自由排列', '💡', 4),

(8, '早起打卡挑战', '记录每天早起的时间和感受，培养早睡早起习惯', '起床时间、睡眠质量、早起做了什么、心情指数？配以折线图展示趋势。', '日历热力图 + 每日心情打卡格', '🌞', 1),
(8, '运动健身日记', '记录每次运动的内容和身体变化', '运动类型、时长、消耗卡路里、身体感受、体重变化？配以身体数据追踪图。', '数据追踪看板，健身数据可视化', '🏃', 2),
(8, '每日读书打卡', '坚持每天阅读，记录阅读进度和收获', '书名、今日读了多少页、摘录金句、阅读感受？配以阅读进度条和书籍堆叠图。', '书籍进度列表 + 每日阅读时长打卡', '📖', 3),
(8, '喝水健康提醒', '记录每天的饮水量，保持健康的生活习惯', '目标饮水量、每次喝水记录、是否达标、皮肤状态？配以水杯图标打卡和折线图。', '8杯水打卡格 + 日/周/月达标统计', '💧', 4);

INSERT INTO journal_work (user_id, title, cover_image, cover_type, content, layout_idea, layout_config, color_scheme, inspiration, view_count, favorite_count, status) VALUES
(1, '春日手账 | 记录花开的美好', 
 'https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&h=500&fit=crop',
 1,
 '春天来了，万物复苏，用手账记录下这个美好的季节。',
 '采用左右分栏布局，左侧放照片，右侧写文字，配上花朵贴纸装饰。',
 '{"columns":2,"columnGap":"16px","rowGap":"12px","padding":"24px","whiteSpacePosition":"bottom","whiteSpaceSize":"60px","partitionMode":"fixed","partitionRatios":[0.4,0.6],"imageTextLayout":"left-image-right-text","layoutDirection":"vertical","gridStyle":"dashed","areas":[{"type":"image","label":"春日照片","columnSpan":1,"rowSpan":2,"areaRatio":0.4,"imageTextRelation":"left-image-right-text"},{"type":"handwriting","label":"春日手写字","columnSpan":1,"rowSpan":1,"areaRatio":0.2},{"type":"text","label":"赏花记录","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"left-image-right-text","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"花朵贴纸","columnSpan":1,"rowSpan":1,"stickerCount":4,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"樱花贴纸","quantity":4,"position":"corner"}]},{"type":"tape","label":"装饰胶带","columnSpan":2,"rowSpan":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"粉色胶带","quantity":2,"position":"border"}]},{"type":"stamp","label":"樱花印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"樱花印章","quantity":2,"position":"scattered"}]}]}',
 '[{"color":"#FFB6C1","name":"嫩粉色","purpose":"主色调，用于标题和花朵装饰"},{"color":"#90EE90","name":"草绿色","purpose":"辅助色，用于叶子和边框"},{"color":"#FFFAF0","name":"米白色","purpose":"背景底色，清新自然"}]',
 '看到小区里的樱花盛开，突然想要记录下这美好的瞬间，于是就有了这篇手账。',
 128, 32, 1),

(1, '简约工作手账排版分享', 
 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=500&fit=crop',
 2,
 '分享一下我常用的工作手账排版方式，简单又实用。',
 '使用时间轴布局，从上到下按时间顺序记录，重要事项用便签标出。',
 '{"columns":1,"columnGap":"0","rowGap":"16px","padding":"20px","whiteSpacePosition":"left","whiteSpaceSize":"30px","partitionMode":"flow","partitionRatios":[0.15,0.25,0.6],"imageTextLayout":"timeline","layoutDirection":"vertical","gridStyle":"dotted","areas":[{"type":"handwriting","label":"日期标题","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"timeline"},{"type":"sticker","label":"待办便签","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.25,"decorativeElements":[{"elementType":"sticker","elementName":"便签贴纸","quantity":3,"position":"left"}]},{"type":"text","label":"工作记录","columnSpan":1,"rowSpan":2,"areaRatio":0.6,"imageTextRelation":"timeline","textAlign":"left","textVerticalAlign":"top"},{"type":"tape","label":"分类胶带","columnSpan":1,"rowSpan":1,"areaRatio":0.1,"decorativeElements":[{"elementType":"tape","elementName":"灰色胶带","quantity":1,"position":"divider"}]},{"type":"stamp","label":"完成印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"完成印章","quantity":2,"position":"corner"}]}]}',
 '[{"color":"#333333","name":"深灰色","purpose":"主色调，用于正文文字"},{"color":"#666666","name":"中灰色","purpose":"辅助色，用于次要信息"},{"color":"#4A90D9","name":"蓝色","purpose":"强调色，标注重点事项"}]',
 '工作太忙，需要简单高效的记录方式，于是研究出了这套排版。',
 256, 64, 1),

(1, '可爱风生日手账制作教程', 
 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800&h=500&fit=crop',
 3,
 '教大家如何制作一个可爱风的生日手账，送给好朋友的生日礼物。',
 '以居中构图为主，搭配各种可爱的装饰元素和贴纸，营造欢快氛围。',
 '{"columns":2,"columnGap":"12px","rowGap":"14px","padding":"28px","whiteSpacePosition":"around","whiteSpaceSize":"50px","partitionMode":"center","partitionRatios":[0.2,0.6,0.2],"imageTextLayout":"center-text-with-decoration","layoutDirection":"vertical","gridStyle":"double","areas":[{"type":"tape","label":"生日彩带胶带","columnSpan":2,"rowSpan":1,"areaRatio":0.1,"decorativeElements":[{"elementType":"tape","elementName":"彩带胶带","quantity":3,"position":"top"}]},{"type":"sticker","label":"生日装饰贴纸","columnSpan":2,"rowSpan":1,"stickerCount":5,"areaRatio":0.15,"imageTextRelation":"center-text-with-decoration","decorativeElements":[{"elementType":"sticker","elementName":"气球贴纸","quantity":5,"position":"scattered"}]},{"type":"handwriting","label":"生日祝福手写字","columnSpan":2,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"center-text-with-decoration"},{"type":"text","label":"生日祝福","columnSpan":2,"rowSpan":2,"areaRatio":0.4,"imageTextRelation":"center-text-with-decoration","textAlign":"center","textVerticalAlign":"middle"},{"type":"image","label":"生日照片","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"center-text-with-decoration"},{"type":"sticker","label":"可爱贴纸","columnSpan":1,"rowSpan":1,"stickerCount":4,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"蛋糕贴纸","quantity":4,"position":"corner"}]},{"type":"stamp","label":"生日印章","columnSpan":2,"rowSpan":1,"stampCount":3,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"生日印章","quantity":3,"position":"bottom"}]}]}',
 '[{"color":"#FF69B4","name":"粉色","purpose":"主色调，营造甜美氛围"},{"color":"#FFD700","name":"金黄色","purpose":"辅助色，用于礼物和装饰"},{"color":"#DDA0DD","name":"淡紫色","purpose":"点缀色，增加梦幻感"}]',
 '好朋友生日快到了，想做一个特别的礼物，于是就有了这个手账。',
 512, 128, 1),

(1, '旅行手账 | 厦门之旅', 
 'https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=800&h=500&fit=crop',
 1,
 '记录厦门旅行的点点滴滴，鼓浪屿、曾厝垵、沙坡尾...',
 '拼贴风格，将车票、门票、照片都贴在手账上，配上文字说明。',
 '{"columns":3,"columnGap":"8px","rowGap":"8px","padding":"16px","whiteSpacePosition":"scattered","whiteSpaceSize":"20px","partitionMode":"masonry","partitionRatios":[0.3,0.35,0.35],"imageTextLayout":"mixed-collage","layoutDirection":"mixed","gridStyle":"none","areas":[{"type":"image","label":"风景照","columnSpan":1,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"mixed-collage"},{"type":"text","label":"行程","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"mixed-collage","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"票根贴纸","columnSpan":1,"rowSpan":1,"stickerCount":2,"areaRatio":0.1,"decorativeElements":[{"elementType":"sticker","elementName":"票根贴纸","quantity":2,"position":"scattered"}]},{"type":"tape","label":"胶带装饰","columnSpan":2,"rowSpan":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"复古胶带","quantity":2,"position":"border"}]},{"type":"text","label":"见闻","columnSpan":2,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"mixed-collage","textAlign":"left","textVerticalAlign":"top"},{"type":"image","label":"美食照","columnSpan":1,"rowSpan":2,"areaRatio":0.25,"imageTextRelation":"mixed-collage"},{"type":"sticker","label":"纪念贴纸","columnSpan":2,"rowSpan":1,"stickerCount":4,"areaRatio":0.1,"decorativeElements":[{"elementType":"sticker","elementName":"纪念贴纸","quantity":4,"position":"scattered"}]},{"type":"stamp","label":"纪念印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"景点印章","quantity":2,"position":"corner"}]},{"type":"handwriting","label":"手写备注","columnSpan":2,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"mixed-collage"}]}]}',
 '[{"color":"#4682B4","name":"海蓝色","purpose":"主色调，呼应海边城市"},{"color":"#F5DEB3","name":"浅棕色","purpose":"辅助色，增加复古质感"},{"color":"#FFFAF0","name":"米白色","purpose":"背景色，清新明亮"}]',
 '每次旅行回来都会做一本旅行手账，老了以后翻看都是美好的回忆。',
 384, 96, 1),

(1, '森系手账 | 秋天的味道', 
 'https://images.unsplash.com/photo-1476820865390-c52aeebb9891?w=800&h=500&fit=crop',
 2,
 '秋天是最适合做手账的季节，落叶、暖阳、桂花...',
 '自然风布局，加入干花、树叶等真实元素，页面留白较多。',
 '{"columns":2,"columnGap":"20px","rowGap":"16px","padding":"24px","whiteSpacePosition":"organic","whiteSpaceSize":"50px","partitionMode":"asymmetric","partitionRatios":[0.45,0.55],"imageTextLayout":"left-nature-right-text","layoutDirection":"vertical","gridStyle":"wavy","areas":[{"type":"image","label":"落叶标本","columnSpan":1,"rowSpan":2,"areaRatio":0.45,"imageTextRelation":"left-nature-right-text"},{"type":"handwriting","label":"秋日手写字","columnSpan":1,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"left-nature-right-text"},{"type":"text","label":"秋日随笔","columnSpan":1,"rowSpan":1,"areaRatio":0.35,"imageTextRelation":"left-nature-right-text","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"树叶装饰贴纸","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"树叶贴纸","quantity":3,"position":"scattered"}]},{"type":"tape","label":"纸胶带","columnSpan":2,"rowSpan":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"复古胶带","quantity":2,"position":"border"}]},{"type":"stamp","label":"植物印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"植物印章","quantity":2,"position":"corner"}]}]}',
 '[{"color":"#8B4513","name":"深棕色","purpose":"主色调，营造秋日氛围"},{"color":"#FF8C00","name":"橙色","purpose":"辅助色，用于落叶和暖阳"},{"color":"#DAA520","name":"金黄色","purpose":"点缀色，增加秋意"}]',
 '走在铺满落叶的小路上，突发奇想做一个秋天主题的手账。',
 192, 48, 1),

(1, '盐系手账 | 极简美学', 
 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&h=500&fit=crop',
 3,
 '盐系手账的特点是清淡、简约、有质感，分享一下我的做法。',
 '大量留白，文字为主，偶尔点缀一些小图案，整体干净整洁。',
 '{"columns":1,"columnGap":"0","rowGap":"20px","padding":"40px","whiteSpacePosition":"all-around","whiteSpaceSize":"80px","partitionMode":"minimal","partitionRatios":[0.15,0.75,0.1],"imageTextLayout":"text-only-with-minimal-decoration","layoutDirection":"vertical","gridStyle":"none","areas":[{"type":"text","label":"标题","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"text-only-with-minimal-decoration","textAlign":"left","textVerticalAlign":"top"},{"type":"handwriting","label":"手写日期","columnSpan":1,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"text-only-with-minimal-decoration"},{"type":"text","label":"正文","columnSpan":1,"rowSpan":3,"areaRatio":0.75,"imageTextRelation":"text-only-with-minimal-decoration","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"小装饰贴纸","columnSpan":1,"rowSpan":1,"stickerCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"sticker","elementName":"简约贴纸","quantity":2,"position":"corner"}]},{"type":"stamp","label":"日期印章","columnSpan":1,"rowSpan":1,"stampCount":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"日期印章","quantity":1,"position":"corner"}]}]}',
 '[{"color":"#333333","name":"炭黑色","purpose":"主色调，用于标题和正文"},{"color":"#F5F5DC","name":"米色","purpose":"背景色，温润质感"},{"color":"#ADD8E6","name":"淡蓝色","purpose":"点缀色，克制的装饰"}]',
 '看多了花哨的手账，想要回归简单，于是尝试了盐系风格。',
 160, 40, 1),

(1, '私密手账 | 我的小秘密', 
 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800&h=500&fit=crop',
 2,
 '这是一篇只属于我自己的手账，记录一些私密的心情。',
 '日记式排版，自由书写，配上一些小插画。',
 '{"columns":1,"columnGap":"0","rowGap":"14px","padding":"32px","whiteSpacePosition":"top","whiteSpaceSize":"40px","partitionMode":"flow","partitionRatios":[0.1,0.15,0.6,0.15],"imageTextLayout":"timeline","layoutDirection":"vertical","gridStyle":"dashed","areas":[{"type":"tape","label":"心情胶带","columnSpan":1,"rowSpan":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"粉色胶带","quantity":1,"position":"top"}]},{"type":"handwriting","label":"日期天气手写字","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"timeline"},{"type":"text","label":"心情日记","columnSpan":1,"rowSpan":3,"areaRatio":0.6,"imageTextRelation":"timeline","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"小插画贴纸","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"心情贴纸","quantity":3,"position":"scattered"}]},{"type":"stamp","label":"心情印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"心情印章","quantity":2,"position":"bottom"}]}]}',
 '[{"color":"#FFB6C1","name":"浅粉色","purpose":"主色调，温柔私密"},{"color":"#DDA0DD","name":"淡紫色","purpose":"辅助色，温馨氛围"},{"color":"#FFF0F5","name":"粉白色","purpose":"背景色，柔和舒适"}]',
 '有些心情只想自己知道，于是有了这篇私密手账。',
 0, 0, 2),

(1, '旧作品归档 | 第一篇手账', 
 'https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=800&h=500&fit=crop',
 1,
 '我的第一篇手账作品，现在看起来有些稚嫩，但很有纪念意义。',
 '最基础的排版，文字加一些简单的装饰。',
 '{"columns":2,"columnGap":"14px","rowGap":"12px","padding":"20px","whiteSpacePosition":"bottom","whiteSpaceSize":"30px","partitionMode":"fixed","partitionRatios":[0.7,0.3],"imageTextLayout":"left-text-right-image","layoutDirection":"vertical","gridStyle":"solid","areas":[{"type":"text","label":"记录","columnSpan":2,"rowSpan":2,"areaRatio":0.7,"textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"简单装饰贴纸","columnSpan":2,"rowSpan":1,"stickerCount":3,"areaRatio":0.2,"decorativeElements":[{"elementType":"sticker","elementName":"基础贴纸","quantity":3,"position":"scattered"}]},{"type":"tape","label":"装饰胶带","columnSpan":1,"rowSpan":1,"tapeCount":1,"areaRatio":0.1,"decorativeElements":[{"elementType":"tape","elementName":"基础胶带","quantity":1,"position":"divider"}]}]}',
 '朴素的颜色，那时候还不会搭配色彩。',
 '刚开始学做手账的时候做的，现在归档留作纪念。',
 50, 5, 3);

INSERT INTO work_category (work_id, category_id) VALUES
(1, 3), (1, 5), (1, 9),
(2, 2), (2, 15),
(3, 3), (3, 6), (3, 10),
(4, 4), (4, 11),
(5, 4), (5, 9),
(6, 2), (6, 5),
(7, 3), (7, 9),
(8, 2), (8, 9);

INSERT INTO color_palette (name, style_description, color_scheme, cover_color, category_ids, use_count, sort) VALUES
('春日樱花粉', '温柔浪漫的春日色调，粉嫩柔美，适合记录春天、少女心事、甜蜜日常',
 '[{"color":"#FFB6C1","name":"嫩粉色","purpose":"主色调，用于标题和花朵装饰","type":"primary"},{"color":"#90EE90","name":"草绿色","purpose":"辅助色，用于叶子和边框","type":"secondary"},{"color":"#FFFAF0","name":"米白色","purpose":"背景底色，清新自然","type":"secondary"},{"color":"#FF69B4","name":"深粉色","purpose":"点缀色，突出重点内容","type":"accent"}]',
 '#FFB6C1', '1,3,9', 42, 1),

('简约商务蓝', '冷静专业的蓝灰配色，简洁高效，适合工作计划、读书笔记、项目记录',
 '[{"color":"#333333","name":"深灰色","purpose":"主色调，用于正文文字","type":"primary"},{"color":"#666666","name":"中灰色","purpose":"辅助色，用于次要信息","type":"secondary"},{"color":"#4A90D9","name":"蓝色","purpose":"强调色，标注重点事项","type":"accent"},{"color":"#F5F5F5","name":"浅灰色","purpose":"背景底色，干净清爽","type":"secondary"}]',
 '#4A90D9', '2,15', 56, 2),

('糖果生日派', '欢快活泼的糖果色组合，甜蜜梦幻，适合生日派对、节日庆祝、少女风手账',
 '[{"color":"#FF69B4","name":"粉色","purpose":"主色调，营造甜美氛围","type":"primary"},{"color":"#FFD700","name":"金黄色","purpose":"辅助色，用于礼物和装饰","type":"secondary"},{"color":"#DDA0DD","name":"淡紫色","purpose":"点缀色，增加梦幻感","type":"accent"},{"color":"#FFF0F5","name":"粉白色","purpose":"背景底色，柔和舒适","type":"secondary"},{"color":"#87CEEB","name":"天蓝色","purpose":"点缀色，增加清新感","type":"accent"}]',
 '#FF69B4', '3,6,10', 38, 3),

('海边旅行蓝', '清新治愈的海边色调，蔚蓝开阔，适合旅行手账、夏日记录、海边回忆',
 '[{"color":"#4682B4","name":"海蓝色","purpose":"主色调，呼应海边城市","type":"primary"},{"color":"#F5DEB3","name":"浅棕色","purpose":"辅助色，增加复古质感","type":"secondary"},{"color":"#FFFAF0","name":"米白色","purpose":"背景色，清新明亮","type":"secondary"},{"color":"#20B2AA","name":"浅海绿","purpose":"点缀色，增添层次","type":"accent"},{"color":"#FFA07A","name":"浅珊瑚","purpose":"点缀色，夕阳暖色","type":"accent"}]',
 '#4682B4', '4,11', 45, 4),

('秋日森系棕', '温暖浓郁的秋日色调，自然复古，适合秋天记录、森系手账、复古风格',
 '[{"color":"#8B4513","name":"深棕色","purpose":"主色调，营造秋日氛围","type":"primary"},{"color":"#FF8C00","name":"橙色","purpose":"辅助色，用于落叶和暖阳","type":"secondary"},{"color":"#DAA520","name":"金黄色","purpose":"点缀色，增加秋意","type":"accent"},{"color":"#F5DEB3","name":"小麦色","purpose":"背景色，温暖柔和","type":"secondary"},{"color":"#556B2F","name":"橄榄绿","purpose":"辅助色，自然元素","type":"secondary"}]',
 '#8B4513', '4,9', 33, 5),

('盐系极简灰', '清淡克制的盐系配色，简约有质感，适合日常记录、极简风格、随笔手账',
 '[{"color":"#333333","name":"炭黑色","purpose":"主色调，用于标题和正文","type":"primary"},{"color":"#F5F5DC","name":"米色","purpose":"背景色，温润质感","type":"secondary"},{"color":"#ADD8E6","name":"淡蓝色","purpose":"点缀色，克制的装饰","type":"accent"},{"color":"#A9A9A9","name":"暗灰色","purpose":"辅助色，次要信息","type":"secondary"}]',
 '#333333', '2,5', 28, 6),

('复古怀旧棕', '浓郁复古的暖棕色调，怀旧文艺，适合复古风、老照片、回忆录手账',
 '[{"color":"#8B4513","name":"深咖色","purpose":"主色调，奠定复古基调","type":"primary"},{"color":"#D2691E","name":"巧克力色","purpose":"辅助色，层次过渡","type":"secondary"},{"color":"#F4A460","name":"沙棕色","purpose":"辅助色，温暖底色","type":"secondary"},{"color":"#FFFFF0","name":"象牙白","purpose":"背景色，纸张质感","type":"secondary"},{"color":"#B8860B","name":"暗金色","purpose":"点缀色，提亮画面","type":"accent"}]',
 '#8B4513', '1,9', 31, 7),

('清新薄荷绿', '清爽自然的薄荷绿调，干净治愈，适合春夏记录、健康生活、清新风格',
 '[{"color":"#98FB98","name":"薄荷绿","purpose":"主色调，清新基调","type":"primary"},{"color":"#20B2AA","name":"浅海绿","purpose":"辅助色，层次丰富","type":"secondary"},{"color":"#F0FFF0","name":"蜜露白","purpose":"背景色，清透干净","type":"secondary"},{"color":"#FFB6C1","name":"浅粉色","purpose":"点缀色，增添柔美","type":"accent"},{"color":"#FFD700","name":"浅黄色","purpose":"点缀色，阳光感","type":"accent"}]',
 '#98FB98', '4,5', 26, 8),

('梦幻紫霞仙', '浪漫神秘的紫色调，梦幻唯美，适合少女心事、梦境记录、奇幻风格',
 '[{"color":"#9370DB","name":"中紫色","purpose":"主色调，梦幻基调","type":"primary"},{"color":"#DDA0DD","name":"梅红色","purpose":"辅助色，柔和过渡","type":"secondary"},{"color":"#E6E6FA","name":"薰衣草色","purpose":"背景色，温柔浪漫","type":"secondary"},{"color":"#FF69B4","name":"热粉色","purpose":"点缀色，甜美提亮","type":"accent"},{"color":"#FFD700","name":"星光金","purpose":"点缀色，星光闪耀","type":"accent"}]',
 '#9370DB', '3,6', 29, 9),

('温暖圣诞红', '喜庆热烈的圣诞配色，温馨热闹，适合节日手账、冬季记录、庆祝主题',
 '[{"color":"#B22222","name":"圣诞红","purpose":"主色调，节日氛围","type":"primary"},{"color":"#228B22","name":"森林绿","purpose":"辅助色，经典搭配","type":"secondary"},{"color":"#FFD700","name":"金色","purpose":"点缀色，华丽装饰","type":"accent"},{"color":"#FFFAF0","name":"米白色","purpose":"背景色，干净温暖","type":"secondary"},{"color":"#8B4513","name":"深棕色","purpose":"松果等元素","type":"secondary"}]',
 '#B22222', '7,10', 22, 10);

INSERT INTO scene_style_relation (scene_category_id, style_category_id, match_score, is_primary, sort) VALUES
(9, 6, 9, 1, 1),
(9, 1, 8, 0, 2),
(9, 3, 7, 0, 3),
(9, 4, 5, 0, 4),

(10, 6, 10, 1, 1),
(10, 3, 9, 0, 2),
(10, 1, 8, 0, 3),
(10, 8, 6, 0, 4),

(11, 4, 9, 1, 1),
(11, 1, 8, 0, 2),
(11, 8, 7, 0, 3),
(11, 6, 5, 0, 4),

(12, 2, 9, 1, 1),
(12, 5, 8, 0, 2),
(12, 1, 6, 0, 3),
(12, 8, 7, 0, 4),

(13, 6, 8, 1, 1),
(13, 4, 7, 0, 2),
(13, 3, 9, 0, 3),
(13, 1, 6, 0, 4),

(14, 1, 9, 1, 1),
(14, 8, 8, 0, 2),
(14, 7, 7, 0, 3),
(14, 2, 6, 0, 4),

(15, 2, 10, 1, 1),
(15, 5, 9, 0, 2),
(15, 8, 7, 0, 3),
(15, 7, 6, 0, 4),

(16, 2, 8, 1, 1),
(16, 5, 9, 0, 2),
(16, 7, 7, 0, 3),
(16, 8, 6, 0, 4);

INSERT INTO layout_template (template_code, template_name, template_type, description, layout_config, style_tags, scene_tags, use_count, is_preset, sort) VALUES
('twoColumnLeftImage', '左图右文双栏', 'twoColumn', '经典左图右文双栏布局，适合图文结合的记录方式，视觉平衡舒适',
 '{"columns":2,"columnGap":"16px","rowGap":"12px","padding":"24px","whiteSpacePosition":"bottom","whiteSpaceSize":"60px","partitionMode":"fixed","partitionRatios":[0.4,0.6],"imageTextLayout":"left-image-right-text","layoutDirection":"vertical","gridStyle":"dashed","areas":[{"type":"image","label":"图片区","columnSpan":1,"rowSpan":2,"areaRatio":0.4,"imageTextRelation":"left-image-right-text"},{"type":"text","label":"主文字区","columnSpan":1,"rowSpan":1,"areaRatio":0.3,"imageTextRelation":"left-image-right-text","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"装饰贴纸","columnSpan":1,"rowSpan":1,"stickerCount":4,"areaRatio":0.3,"decorativeElements":[{"elementType":"sticker","elementName":"装饰贴纸","quantity":4,"position":"corner"}]}]}',
 'ins风,复古风', '读书摘抄,旅行手账,日常记录', 128, 1, 1),

('twoColumnRightImage', '左文右图双栏', 'twoColumn', '左文右图双栏布局，突出文字内容的同时配有配图点缀',
 '{"columns":2,"columnGap":"16px","rowGap":"12px","padding":"24px","whiteSpacePosition":"top","whiteSpaceSize":"40px","partitionMode":"fixed","partitionRatios":[0.55,0.45],"imageTextLayout":"left-text-right-image","layoutDirection":"vertical","gridStyle":"solid","areas":[{"type":"text","label":"主文字区","columnSpan":1,"rowSpan":2,"areaRatio":0.55,"imageTextRelation":"left-text-right-image","textAlign":"left","textVerticalAlign":"top"},{"type":"image","label":"图片区","columnSpan":1,"rowSpan":1,"areaRatio":0.3,"imageTextRelation":"left-text-right-image"},{"type":"sticker","label":"贴纸区","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"贴纸","quantity":3,"position":"scattered"}]}]}',
 '复古风,ins风', '观影心得,日常记录', 96, 1, 2),

('timelineVertical', '纵向时间轴', 'timeline', '纵向时间轴布局，按时间顺序记录，条理清晰适合日程和打卡',
 '{"columns":1,"columnGap":"0","rowGap":"16px","padding":"20px","whiteSpacePosition":"left","whiteSpaceSize":"30px","partitionMode":"flow","partitionRatios":[0.15,0.25,0.6],"imageTextLayout":"timeline","layoutDirection":"vertical","gridStyle":"dotted","areas":[{"type":"tape","label":"日期胶带","columnSpan":1,"rowSpan":1,"tapeCount":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"日期胶带","quantity":1,"position":"top"}]},{"type":"handwriting","label":"标题手写字","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"timeline"},{"type":"sticker","label":"便签贴纸","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.25,"decorativeElements":[{"elementType":"sticker","elementName":"便签贴纸","quantity":3,"position":"left"}]},{"type":"text","label":"记录区","columnSpan":1,"rowSpan":2,"areaRatio":0.6,"imageTextRelation":"timeline","textAlign":"left","textVerticalAlign":"top"},{"type":"stamp","label":"打卡印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"打卡印章","quantity":2,"position":"corner"}]}]}',
 '简约风,盐系', '工作计划,日常记录,习惯打卡', 156, 1, 3),

('centerFocus', '居中聚焦', 'centerFocus', '居中聚焦布局，主内容在视觉中心，四周装饰环绕，适合主题突出的作品',
 '{"columns":2,"columnGap":"12px","rowGap":"14px","padding":"28px","whiteSpacePosition":"around","whiteSpaceSize":"50px","partitionMode":"center","partitionRatios":[0.2,0.6,0.2],"imageTextLayout":"center-text-with-decoration","layoutDirection":"vertical","gridStyle":"double","areas":[{"type":"tape","label":"顶部装饰胶带","columnSpan":2,"rowSpan":1,"tapeCount":2,"areaRatio":0.1,"decorativeElements":[{"elementType":"tape","elementName":"装饰胶带","quantity":2,"position":"top"}]},{"type":"sticker","label":"顶部装饰","columnSpan":2,"rowSpan":1,"stickerCount":5,"areaRatio":0.15,"imageTextRelation":"center-text-with-decoration","decorativeElements":[{"elementType":"sticker","elementName":"装饰贴纸","quantity":5,"position":"scattered"}]},{"type":"handwriting","label":"手写字标题","columnSpan":2,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"center-text-with-decoration"},{"type":"text","label":"主内容区","columnSpan":2,"rowSpan":2,"areaRatio":0.4,"imageTextRelation":"center-text-with-decoration","textAlign":"center","textVerticalAlign":"middle"},{"type":"image","label":"配图区","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"center-text-with-decoration"},{"type":"sticker","label":"小贴纸","columnSpan":1,"rowSpan":1,"stickerCount":4,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"小贴纸","quantity":4,"position":"corner"}]},{"type":"stamp","label":"装饰印章","columnSpan":2,"rowSpan":1,"stampCount":3,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"装饰印章","quantity":3,"position":"bottom"}]}]}',
 '可爱风,甜系', '节日主题,日常记录', 112, 1, 4),

('collageStyle', '拼贴风', 'collage', '自由拼贴风格，多图多便签错落排列，营造丰富活泼的视觉效果',
 '{"columns":3,"columnGap":"8px","rowGap":"8px","padding":"16px","whiteSpacePosition":"scattered","whiteSpaceSize":"20px","partitionMode":"masonry","partitionRatios":[0.3,0.35,0.35],"imageTextLayout":"mixed-collage","layoutDirection":"mixed","gridStyle":"none","areas":[{"type":"image","label":"照片1","columnSpan":1,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"mixed-collage"},{"type":"text","label":"文字","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"mixed-collage","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"票根","columnSpan":1,"rowSpan":1,"stickerCount":2,"areaRatio":0.1,"decorativeElements":[{"elementType":"sticker","elementName":"票根贴纸","quantity":2,"position":"scattered"}]},{"type":"tape","label":"胶带装饰","columnSpan":2,"rowSpan":1,"tapeCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"复古胶带","quantity":2,"position":"border"}]},{"type":"text","label":"记录","columnSpan":2,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"mixed-collage","textAlign":"left","textVerticalAlign":"top"},{"type":"image","label":"照片2","columnSpan":1,"rowSpan":2,"areaRatio":0.25,"imageTextRelation":"mixed-collage"},{"type":"sticker","label":"贴纸","columnSpan":2,"rowSpan":1,"stickerCount":4,"areaRatio":0.1,"decorativeElements":[{"elementType":"sticker","elementName":"纪念贴纸","quantity":4,"position":"scattered"}]},{"type":"stamp","label":"纪念印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"景点印章","quantity":2,"position":"corner"}]},{"type":"handwriting","label":"手写备注","columnSpan":2,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"mixed-collage"}]}',
 '复古风,森系', '旅行手账,美食记录', 188, 1, 5),

('minimalist', '极简留白', 'minimalist', '极简留白风格，大面积空白配合精炼文字，干净高级有质感',
 '{"columns":1,"columnGap":"0","rowGap":"20px","padding":"40px","whiteSpacePosition":"all-around","whiteSpaceSize":"80px","partitionMode":"minimal","partitionRatios":[0.15,0.75,0.1],"imageTextLayout":"text-only-with-minimal-decoration","layoutDirection":"vertical","gridStyle":"none","areas":[{"type":"text","label":"标题","columnSpan":1,"rowSpan":1,"areaRatio":0.15,"imageTextRelation":"text-only-with-minimal-decoration","textAlign":"left","textVerticalAlign":"top"},{"type":"handwriting","label":"手写日期","columnSpan":1,"rowSpan":1,"areaRatio":0.1,"imageTextRelation":"text-only-with-minimal-decoration"},{"type":"text","label":"正文","columnSpan":1,"rowSpan":3,"areaRatio":0.75,"imageTextRelation":"text-only-with-minimal-decoration","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"小装饰","columnSpan":1,"rowSpan":1,"stickerCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"sticker","elementName":"简约贴纸","quantity":2,"position":"corner"}]},{"type":"stamp","label":"日期印章","columnSpan":1,"rowSpan":1,"stampCount":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"日期印章","quantity":1,"position":"corner"}]}]}',
 '简约风,盐系', '工作计划,读书摘抄,习惯打卡', 144, 1, 6),

('naturalStyle', '自然森系', 'natural', '自然森系风格，不对称布局带有机感，配干花叶等自然元素装饰',
 '{"columns":2,"columnGap":"20px","rowGap":"16px","padding":"24px","whiteSpacePosition":"organic","whiteSpaceSize":"50px","partitionMode":"asymmetric","partitionRatios":[0.45,0.55],"imageTextLayout":"left-nature-right-text","layoutDirection":"vertical","gridStyle":"wavy","areas":[{"type":"image","label":"干花区","columnSpan":1,"rowSpan":2,"areaRatio":0.45,"imageTextRelation":"left-nature-right-text"},{"type":"handwriting","label":"手写字","columnSpan":1,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"left-nature-right-text"},{"type":"text","label":"主文字","columnSpan":1,"rowSpan":1,"areaRatio":0.35,"imageTextRelation":"left-nature-right-text","textAlign":"left","textVerticalAlign":"top"},{"type":"sticker","label":"树叶装饰","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.15,"decorativeElements":[{"elementType":"sticker","elementName":"树叶贴纸","quantity":3,"position":"scattered"}]},{"type":"tape","label":"纸胶带","columnSpan":2,"rowSpan":1,"tapeCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"复古胶带","quantity":2,"position":"border"}]},{"type":"stamp","label":"植物印章","columnSpan":1,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"植物印章","quantity":2,"position":"corner"}]}]}',
 '森系,复古风', '旅行手账,日常记录,美食记录', 92, 1, 7),

('threeColumnMagazine', '三栏杂志风', 'magazine', '三栏杂志风布局，类似时尚杂志的排版，大图配侧栏文字专业感强',
 '{"columns":3,"columnGap":"10px","rowGap":"10px","padding":"20px","whiteSpacePosition":"editorial","whiteSpaceSize":"30px","partitionMode":"editorial","partitionRatios":[0.25,0.5,0.25],"imageTextLayout":"magazine-layout","layoutDirection":"mixed","gridStyle":"solid","areas":[{"type":"image","label":"大图","columnSpan":2,"rowSpan":2,"areaRatio":0.5,"imageTextRelation":"magazine-layout"},{"type":"text","label":"侧栏文字","columnSpan":1,"rowSpan":2,"areaRatio":0.25,"imageTextRelation":"magazine-layout","textAlign":"left","textVerticalAlign":"top"},{"type":"handwriting","label":"手写标题","columnSpan":2,"rowSpan":1,"areaRatio":0.2,"imageTextRelation":"magazine-layout"},{"type":"sticker","label":"装饰","columnSpan":1,"rowSpan":1,"stickerCount":3,"areaRatio":0.05,"decorativeElements":[{"elementType":"sticker","elementName":"装饰贴纸","quantity":3,"position":"scattered"}]},{"type":"tape","label":"侧边胶带","columnSpan":1,"rowSpan":1,"tapeCount":1,"areaRatio":0.05,"decorativeElements":[{"elementType":"tape","elementName":"侧边胶带","quantity":1,"position":"divider"}]},{"type":"stamp","label":"刊号印章","columnSpan":2,"rowSpan":1,"stampCount":2,"areaRatio":0.05,"decorativeElements":[{"elementType":"stamp","elementName":"刊号印章","quantity":2,"position":"corner"}]}]}',
 '暗黑系,ins风', '习惯打卡,观影心得', 80, 1, 8);
