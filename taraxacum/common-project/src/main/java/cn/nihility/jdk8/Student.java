package cn.nihility.jdk8;

/**
 * Student
 *
 * @author clover
 * @date 2020-01-04 16:36
 */
public class Student {

    private String name;
    private Integer age;

    public Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public static class StudentComparator {
        public int compareByAge(Student s1, Student s2) {
            return s1.getAge() - s2.getAge();
        }

        public int compareByName(Student s1, Student s2) {
            return s1.getName().compareToIgnoreCase(s2.getName());
        }
    }

    public static int compareByAge(Student s1, Student s2) {
        return s1.getAge() - s2.getAge();
    }

    public static int compareByName(Student s1, Student s2) {
        return s1.getName().compareToIgnoreCase(s2.getName());
    }

    public int compareWithAge(Student s) {
        return getAge() - s.getAge();
    }

    public int compareWithName(Student s) {
        return getName().compareToIgnoreCase(s.getName());
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
}


