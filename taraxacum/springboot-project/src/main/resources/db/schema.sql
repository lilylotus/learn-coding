
CREATE TABLE bank_logging (
    id INT NOT NULL PRIMARY KEY auto_increment ,
    operator VARCHAR(255) ,
    transfer_to VARCHAR(255) ,
    operation_time DATETIME default CURRENT_TIMESTAMP(),
    money INT NOT NULL
);

CREATE TABLE `test` (
id INT NOT NULL auto_increment ,
name VARCHAR(255) NOT NULL ,
age INT NOT NULL ,
address VARCHAR(255),
PRIMARY KEY(id)
)ENGINE=InnoDB ;

INSERT INTO test (name, age, address) VALUES ('未知', 20, '未知之地');

CREATE TABLE `bank` (
id INT NOT NULL auto_increment ,
name VARCHAR(255) NOT NULL ,
money INT NOT NULL ,
PRIMARY KEY(id)
)ENGINE=InnoDB ;

INSERT INTO `bank` (name, money) VALUES ('未知', 100000) ;