package cn.nihility.boot.model;

/**
 * HelloMessage
 *
 * @author dandelion
 * @date 2020-04-14 17:00
 */
public class HelloMessage {

    private String name;

    public HelloMessage() {
    }

    public HelloMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
