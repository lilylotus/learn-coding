package cn.nihility.jdk8;

/**
 * FunctionPerson
 *
 * @author clover
 * @date 2020-01-04 14:23
 */
public class FunctionPerson {

    private String name;
    private Integer age;

    public FunctionPerson() {
    }

    public FunctionPerson(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "FunctionPerson{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
