
    private Integer id;
    private String operator;
    private String transferTo;
    private LocalDateTime operationTime;
    private Integer money;

    transfer_to, operation_time,

CREATE TABLE bank_logging (
    id INT NOT NULL PRIMARY KEY auto_increment ,
    operator VARCHAR(255) ,
    transfer_to VARCHAR(255) ,
    operation_time DATETIME default CURRENT_TIMESTAMP(),
    money INT NOT NULL
);