CREATE SCHEMA hackathon CHARACTER SET utf8 COLLATE utf8_general_ci;

use hackathon;

CREATE TABLE link (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  marktplaats_user_id int(11) NOT NULL,
  twitter_screen_name varchar(200) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

