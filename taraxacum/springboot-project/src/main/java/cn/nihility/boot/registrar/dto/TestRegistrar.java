package cn.nihility.boot.registrar.dto;

/**
 * @author dandelion
 * @date 2020:06:27 19:00
 */
public class TestRegistrar {
    private String name;

    public TestRegistrar() {
    }

    public TestRegistrar(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
