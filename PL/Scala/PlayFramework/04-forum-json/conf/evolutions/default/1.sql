# --- !Ups
CREATE TABLE `posts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `date` DATETIME NOT NULL,
  `body` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`)
);

# --- !Downs
DROP TABLE `posts`;