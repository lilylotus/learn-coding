package cn.nihility.unify.vo;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class TestEntity implements Serializable {
    private static final long serialVersionUID = 1585203892768191758L;

    private Instant instant;
    private LocalTime localTime;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private Date date;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "instant=" + instant +
                ", localTime=" + localTime +
                ", localDateTime=" + localDateTime +
                ", localDate=" + localDate +
                ", date=" + date +
                '}';
    }
}
