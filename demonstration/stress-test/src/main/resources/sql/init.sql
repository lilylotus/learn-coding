CREATE TABLE flower (
    id varchar(64) NOT NULL,
    name_english varchar(255) NOT NULL,
    name_chinese varchar(255) NOT NULL,
    age int NOT NULL,
    add_time datetime NOT NULL,
    update_time datetime NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB ;

CREATE TABLE service_log (
    id bigint not null ,
    operation varchar(255) not null ,
    content varchar(1024) not null ,
    add_by varchar(64) not null ,
    update_by varchar(64) ,
    add_time datetime not null ,
    update_time datetime ,
    primary key (id)
) ENGINE = InnoDB ;