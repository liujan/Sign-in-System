create database db_si CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';

use db_si;

create table teacher
(
	teacher_id int primary key not null auto_increment,
	user_name varchar(50) not null,
	teacher_name varchar(50) not null,
	user_pwd varchar(50) not null,
	email varchar(50)
)engine=InnoDB default charset=utf8;

create table student
(
	stu_id varchar(20) not null primary key,
	name varchar(50) not null,
	mac_address varchar(50) not null,
	user_pwd varchar(50) not null,
	email varchar(50),
	course_list VARCHAR(200) ,
	foreign key(teacher_id) references teacher(teacher_id)
)engine=InnoDB default charset=utf8;



create table course
(
	course_id int primary key not null auto_increment,
	course_name varchar(100) not null,
	begin_time time,
	end_time time,
	day_in_week int,
	teacher_id int,
	foreign key(teacher_id) references teacher(teacher_id)
)engine=InnoDB default charset=utf8;
alter table course auto_increment=1;

create table statistic
(
	statistic_id int primary key not null auto_increment,
	stu_id varchar(20),
	course_id int,
	sigin_time datetime,
	week int,
	foreign key(stu_id) references student(stu_id),
	foreign key(course_id) references course(course_id)
)engine=InnoDB default charset=utf8;

create table info
(
	id int  primary key default 1,
	teacher_id int ,
	start_time datetime,
	week int,
	current datetime,
	course_id int default 0,
	foreign key(teacher_id) references teacher(teacher_id),
	foreign key(course_id) references course(course_id)
)engine=InnoDB default charset=utf8;



#URIEncoding="UTF-8"

#老师先登陆，设置好当前是第几周，和当前是上哪一门课，然后学生才可以开始签到