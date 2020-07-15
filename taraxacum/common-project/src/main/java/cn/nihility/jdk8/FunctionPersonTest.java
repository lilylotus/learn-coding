package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * FunctionPersonTest
 *
 * @author clover
 * @date 2020-01-04 14:24
 */
public class FunctionPersonTest {

    public static void main(String[] args) {


        FunctionPerson person1 = new FunctionPerson("zhangsan", 20);
        FunctionPerson person2 = new FunctionPerson("lishi", 30);
        FunctionPerson person3 = new FunctionPerson("wangwu", 16);
        FunctionPerson person4 = new FunctionPerson("mazi", 24);
        FunctionPerson person5 = new FunctionPerson("liushi", 18);


        List<FunctionPerson> list = Arrays.asList(person1, person2, person3, person4, person5);


        FunctionPersonTest test = new FunctionPersonTest();
        List<FunctionPerson> resultList = test.getPersonsByUserName(list, "mazi");

        resultList.forEach(person -> System.out.println(person));

        System.out.println("=====================");
        List<FunctionPerson> personAgeList = test.getPersonByAge(list, 20);
        personAgeList.forEach(System.out::println);

        System.out.println("==========================");
        List<FunctionPerson> personByAge2 = test.getPersonByAge2(list, 20,
                (ageOfPerson, personList) -> personList.stream().filter(p -> p.getAge() > ageOfPerson).collect(Collectors.toList()));
        personByAge2.forEach(System.out::println);


    }

    public List<FunctionPerson> getPersonsByUserName(List<FunctionPerson> list, String name) {
        return list.stream().filter(person -> person.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<FunctionPerson> getPersonByAge(final List<FunctionPerson> list, final int age) {
        BiFunction<Integer, List<FunctionPerson>, List<FunctionPerson>> biFunction =
                (ageOfPerson, personList) -> personList.stream().filter(p -> p.getAge() > ageOfPerson).collect(Collectors.toList());
        return biFunction.apply(age, list);
    }

    public List<FunctionPerson> getPersonByAge2(final List<FunctionPerson> list, final int age,
                                                BiFunction<Integer, List<FunctionPerson>, List<FunctionPerson>> function) {
        return function.apply(age, list);
    }


}
