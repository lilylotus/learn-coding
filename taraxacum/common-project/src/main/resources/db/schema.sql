CREATE TABLE mybatis_user (
id int not null primary key auto_increment ,
name varchar(255) not null ,
age int not null ,
address varchar(255)
)ENGINE=InnoDB DEFAULT charset=utf8mb4 ;

insert into mybatis_user (name, age, address) values ('测试名称01', 20, '未知之地01') ;
insert into mybatis_user (name, age, address) values ('测试名称02', 20, '未知之地02') ;
insert into mybatis_user (name, age, address) values ('测试名称03', 20, '未知之地03') ;