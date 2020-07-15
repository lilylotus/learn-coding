package cn.nihility.data_structure.tree;

import lombok.Data;

import java.util.Objects;

/**
 * Person
 *
 * @author clover
 * @date 2020-02-25 14:44
 */
@Data
public class Person implements Comparable<Person> {

    private String name;
    private int age;
    private int index;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, int index) {
        this.name = name;
        this.age = age;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age &&
                Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public int compareTo(Person o) {
        if (null == o) { return -1; }
        return Integer.compare(age, o.age);
    }

    @Override
    public String toString() {
        return "{" + name + "," + age + "," + index + "}";
    }
}
