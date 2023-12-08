CREATE DATABASE IF NOT EXISTS kindergarten;

DROP TABLE IF EXISTS kindergarten.child;
DROP TABLE IF EXISTS kindergarten.group;

CREATE TABLE IF NOT EXISTS kindergarten.group (
    number INT PRIMARY KEY AUTO_INCREMENT,
    name   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS kindergarten.child (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    group_number INT                     NOT NULL,
    full_name    VARCHAR(255)            NOT NULL,
    gender       ENUM ('MALE', 'FEMALE') NOT NULL,
    age          TINYINT unsigned        NOT NULL,
    FOREIGN KEY (group_number) REFERENCES kindergarten.group (number) ON DELETE CASCADE
);