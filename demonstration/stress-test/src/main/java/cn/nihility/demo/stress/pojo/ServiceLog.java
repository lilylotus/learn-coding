package cn.nihility.demo.stress.pojo;

import java.time.LocalDateTime;

public class ServiceLog {

    private Long id;
    private String operation;
    private String content;
    private String addBy;
    private String updateBy;
    private LocalDateTime addTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ServiceLog{" +
            "id=" + id +
            ", operation='" + operation + '\'' +
            ", content='" + content + '\'' +
            ", addBy='" + addBy + '\'' +
            ", updateBy='" + updateBy + '\'' +
            ", addTime=" + addTime +
            ", updateTime=" + updateTime +
            '}';
    }
}
