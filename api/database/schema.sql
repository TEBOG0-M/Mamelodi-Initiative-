CREATE DATABASE IF NOT EXISTS mamelodi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mamelodi;

CREATE TABLE user_profiles (
  firebase_uid VARCHAR(128) PRIMARY KEY,
  email        VARCHAR(254) NOT NULL,
  display_name VARCHAR(100) NOT NULL DEFAULT '',
  is_sample    TINYINT(1)   NOT NULL DEFAULT 0,
  created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE volunteer_applications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  firebase_uid VARCHAR(128) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  qualification VARCHAR(120) NOT NULL,
  field_of_study VARCHAR(120) NOT NULL,
  programme_preference ENUM('summer_jam','winter_jam','road_to_finals','ylc') NOT NULL,
  subjects JSON NOT NULL,
  availability JSON NOT NULL,
  motivation TEXT NOT NULL,
  consent_background_check TINYINT(1) NOT NULL,
  status ENUM('submitted','under_review','accepted','declined','withdrawn') NOT NULL DEFAULT 'submitted',
  is_sample TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_app_user FOREIGN KEY (firebase_uid) REFERENCES user_profiles(firebase_uid) ON DELETE CASCADE,
  INDEX idx_app_uid (firebase_uid)
);

CREATE TABLE news_articles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  slug VARCHAR(200) NOT NULL UNIQUE,
  category ENUM('news','story','report') NOT NULL DEFAULT 'news',
  summary VARCHAR(400) NOT NULL,
  body TEXT NOT NULL,
  published_at DATE NOT NULL,
  is_sample TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE contact_messages (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(254) NOT NULL,
  subject VARCHAR(150) NOT NULL,
  message TEXT NOT NULL,
  is_sample TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ===== SAMPLE DATA (simulated, flagged is_sample=1, 10 rows per table) =====
INSERT INTO user_profiles (firebase_uid, email, display_name, is_sample)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i+1 FROM n WHERE i < 10)
SELECT CONCAT('SAMPLE-UID-', LPAD(i,2,'0')), CONCAT('sample', LPAD(i,2,'0'), '@example.invalid'),
       CONCAT('Sample User ', LPAD(i,2,'0')), 1 FROM n;

INSERT INTO volunteer_applications
 (firebase_uid, full_name, email, phone, qualification, field_of_study, programme_preference,
  subjects, availability, motivation, consent_background_check, status, is_sample, created_at)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i+1 FROM n WHERE i < 10)
SELECT CONCAT('SAMPLE-UID-', LPAD(i,2,'0')), CONCAT('Sample Applicant ', LPAD(i,2,'0')),
       CONCAT('sample', LPAD(i,2,'0'), '@example.invalid'), CONCAT('+2700000000', LPAD(i,2,'0')),
       'Sample qualification', 'Sample field of study',
       ELT(1 + MOD(i,4), 'summer_jam','winter_jam','road_to_finals','ylc'),
       JSON_ARRAY(ELT(1 + MOD(i,3), 'Mathematics','Physical Sciences','Coding / IT')),
       JSON_ARRAY(ELT(1 + MOD(i,3), 'Saturday Mornings','School Holiday Camps','Weekday Afternoons')),
       'SAMPLE RECORD: simulated motivation text for demonstration only.', 1,
       ELT(1 + MOD(i,4), 'submitted','under_review','accepted','declined'), 1,
       DATE_SUB(NOW(), INTERVAL i DAY) FROM n;

INSERT INTO news_articles (title, slug, category, summary, body, published_at, is_sample)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i+1 FROM n WHERE i < 10)
SELECT CONCAT('SAMPLE article ', LPAD(i,2,'0')), CONCAT('sample-article-', LPAD(i,2,'0')),
       ELT(1 + MOD(i,3), 'news','story','report'),
       'Sample summary. Replace with verified content from the organisation.',
       'Sample body text for demonstration only. This is not a real news item.',
       DATE_SUB(CURDATE(), INTERVAL i*7 DAY), 1 FROM n;

INSERT INTO contact_messages (name, email, subject, message, is_sample)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i+1 FROM n WHERE i < 10)
SELECT CONCAT('Sample Sender ', LPAD(i,2,'0')), CONCAT('sample', LPAD(i,2,'0'), '@example.invalid'),
       CONCAT('Sample enquiry ', LPAD(i,2,'0')), 'Sample message for demonstration only.', 1 FROM n;

-- ===== VERIFY: every count must be >= 10 =====
SELECT 'user_profiles' t, COUNT(*) c FROM user_profiles UNION ALL
SELECT 'volunteer_applications', COUNT(*) FROM volunteer_applications UNION ALL
SELECT 'news_articles', COUNT(*) FROM news_articles UNION ALL
SELECT 'contact_messages', COUNT(*) FROM contact_messages;