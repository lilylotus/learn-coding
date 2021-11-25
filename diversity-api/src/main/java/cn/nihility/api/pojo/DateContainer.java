package cn.nihility.api.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateContainer {

    private String id;
    private Date date;
    private LocalDate localDate;
    private LocalDateTime localDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "DateContainer{" +
            "id='" + id + '\'' +
            ", date=" + date +
            ", localDate=" + localDate +
            ", localDateTime=" + localDateTime +
            '}';
    }
}
