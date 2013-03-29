CREATE TABLE role (
  role_id varchar(20) not null,
  description varchar(256),
  primary key (role_id)
) Engine = MyISAM;

CREATE TABLE user (
  id int not null auto_increment,
  role_id varchar (20) not null,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  password varchar(256) not null,
  email varchar(256) not null,
  unique uq_user_1 (id, first_name, last_name),
  unique uq_user_2 (email),
  primary key (id),
  constraint fk_user_role_1 foreign key (role_id) references role (role_id)
) Engine = MyISAM;

insert into role values ('ROLE_ADMIN', 'Administrator');
insert into role values ('ROLE_MODER', 'Moderator');
insert into role values ('ROLE_USER', 'User');

CREATE TABLE photo (
  id int not null auto_increment,
  user_id int not null,
  name varchar(256),
  author varchar (64),
  description text,
  address text,
  latitude varchar(256),
  longitude varchar(256),
  marked boolean,
  license tinyint(1),
  day1 int(2) default 0,
  month1 int(2) default 0,
  year1 int(4) default 0,
  day2 int(2) default 0,
  month2 int(2) default 0,
  year2 int(4) default 0,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_photo_1 foreign key (user_id) references user (id)
) Engine = MyISAM;

CREATE TABLE tag (
  id int not null auto_increment,
  name varchar(256) not null,
  primary key (id)
) Engine = MyISAM;

CREATE TABLE photo_tag(
  photo_id int not null,
  tag_id int not null,
  primary key (photo_id, tag_id),
  constraint fk_photo_tag_1 foreign key (photo_id) references photo (id),
  constraint fk_photo_tag_2 foreign key (tag_id) references  tag (id)
) Engine = MyISAM;

alter table photo add views int default 0;

create table fave (
  id int not null auto_increment,
  user_id int not null,
  photo_id int not null,
  unique uq_fave (user_id, photo_id),
  primary key (id),
  constraint fk_user_fave foreign key (user_id) references user (id),
  constraint fk_photo_fave foreign key (photo_id) references user (id)
) Engine = MyISAM;

create table comment (
  id int not null auto_increment,
  description text not null,
  user_id int not null,
  photo_id int not null,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_comment foreign key (user_id) references user (id),
  constraint fk_photo_comment foreign key (photo_id) references photo (id)
) Engine = MyISAM;

create table album (
  id int not null auto_increment,
  name varchar(255) not null,
  description text,
  user_id int not null,
  thumbnail int,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_album foreign key (user_id) references user (id),
  constraint fk_photo_album foreign key (thumbnail) references photo (id)
) Engine = MyISAM;

create table photo_album (
  photo_id int not null,
  album_id int not null,
  primary key (photo_id, album_id),
  constraint fk_photo_album_1 foreign key (photo_id) references photo (id),
  constraint fk_photo_album_2 foreign key (album_id) references album (id)
) Engine = MyISAM;

create table assortment (
  id int not null auto_increment,
  name varchar(255) not null,
  description text,
  user_id int not null,
  thumbnail int,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_assortment foreign key (user_id) references user (id),
  constraint fk_photo_assortment foreign key (thumbnail) references photo (id)
) Engine = MyISAM;

create table photo_assortment (
  photo_id int not null,
  assortment_id int not null,
  primary key (photo_id, assortment_id),
  constraint fk_photo_assortment_1 foreign key (photo_id) references photo (id),
  constraint fk_photo_assortment_2 foreign key (assortment_id) references assortment (id)
) Engine = MyISAM;

create table team (
  id int not null auto_increment,
  name varchar(255) not null,
  description text,
  marked boolean,
  rules text,
  closed boolean,
  user_id int not null,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_team foreign key (user_id) references user (id)
) Engine = MyISAM;

create table photo_team (
  id int not null auto_increment,
  user_id int not null,
  team_id int not null,
  photo_id int not null,
  agreed boolean default false,
  primary key (id),
  unique uq_photo_user (photo_id, user_id, team_id),
  constraint fk_user_photo_team foreign key (user_id) references user (id),
  constraint fk_photo_team_1 foreign key (team_id) references team (id),
  constraint fk_photo_team_2 foreign key (photo_id) references photo (id)
) Engine = MyISAM;

create table user_team (
  id int not null auto_increment,
  user_id int not null,
  team_id int not null,
  agreed tinyint(1) default 0,
  primary key (id),
  unique uq_user_team (user_id, team_id),
  constraint fk_user_team_1 foreign key (user_id) references user (id),
  constraint fk_user_team_2 foreign key (team_id) references team (id)
) Engine = MyISAM;

create table topic (
  id int not null auto_increment,
  theme varchar(255) not null,
  description text,
  user_id int not null,
  team_id int not null,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_topic foreign key (user_id) references user (id),
  constraint fk_team_topic foreign key (team_id) references team (id)
) Engine = MyISAM;

create table reply (
  id int not null auto_increment,
  description text not null,
  user_id int not null,
  topic_id int not null,
  create_at timestamp default current_timestamp,
  primary key (id),
  constraint fk_user_reply foreign key (user_id) references user (id),
  constraint fk_topic_reply foreign key (topic_id) references topic (id)
) Engine = MyISAM;

alter table user add about text;
alter table user add gender boolean;
alter table user add day int(2) default 0;
alter table user add month int(2) default 0;
alter table user add year int(4) default 0;
alter table user add current_city text;
alter table user add c_latitude varchar(128);
alter table user add c_longitude varchar(128);
alter table user add home_town text;
alter table user add h_latitude varchar(128);
alter table user add h_longitude varchar(128);

create table avatar(
  id int not null auto_increment,
  user_id int not null,
  primary key (id),
  constraint fk_user_avatar foreign key (user_id) references user (id)
) Engine = MyISAM;

alter table team add deleted boolean;
alter table photo add privacy tinyint(1) default 2;

create table contact (
  user_id int not null,
  contact_id int not null,
  unique user_contact (user_id, contact_id),
  constraint fk_user_1 foreign key (user_id) references user (id),
  constraint fk_contact_1 foreign key (contact_id) references user (id)
) Engine = MyISAM;

alter table user add fblink varchar(255);
alter table user add vklink varchar(255);