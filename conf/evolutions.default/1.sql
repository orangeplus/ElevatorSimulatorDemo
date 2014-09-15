# --- Initial database schema

# --- !Ups
CREATE TABLE building (
  id int(11) NOT NULL,
  name varchar(100) NOT NULL,
  max_floor int(11) NOT NULL,
  people int(11) NOT NULL,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  constraint pk_building primary key (id)
);
create sequence account_seq start with 100;

CREATE TABLE elevator (
  id int(11) NOT NULL,
  building_id int(11) NOT NULL,
  max_people int(11) NOT NULL,
  is_express tinyint(1) DEFAULT '0',
  constraint pk_elevator primary key (id)
);
create sequence account_seq start with 100;

CREATE TABLE elevator_floor (
  id int(11) NOT NULL,
  elevator_id int(11) NOT NULL,
  floor int(11) NOT NULL,
  constraint pk_elevator_floor primary key (id)
);
create sequence account_seq start with 100;


INSERT INTO `building` VALUES (1,'building 1',10,10,'2014-09-13 17:25:12'),(2,'Building 2',10,1,'2014-09-13 18:57:00'),(3,'Building 3',10,100,'2014-09-13 18:57:14');
INSERT INTO `elevator` VALUES (1,1,4,0),(2,1,40,1);
INSERT INTO `elevator_floor` VALUES (1,1,7),(2,1,2),(3,1,7),(4,2,3),(5,2,10),(6,2,4),(7,2,2),(8,2,8);

# --- !Downs
