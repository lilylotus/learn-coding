package cn.nihility.jdk8;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * OptionalTest
 *
 * @author clover
 * @date 2020-01-04 15:35
 */
public class OptionalTest {

    public static void main(String[] args) {

        Optional<String> optional = Optional.of("hello");
        Optional.ofNullable("Hello");
        // 要先调用 isPresent() 在调用 get(), 不可直接调用 get()
        // 不推荐 if (optional.isPresent()) { System.out.println(optional.get()); }
        // 推荐使用函数式的方式使用
        optional.ifPresent(item -> System.out.println(item));

        FunctionPerson person3 = new FunctionPerson("wangwu", 16);
        FunctionPerson person4 = new FunctionPerson("mazi", 24);
        FunctionPerson person5 = new FunctionPerson("liushi", 18);

        Company company = new Company("Company");
        List<FunctionPerson> list = Arrays.asList(person3, person4, person5);
        company.setEmployee(list);

        List<FunctionPerson> employee = company.getEmployee();
        /*if (null == employee) {
            // return new ArrayList<FunctionPerson>();
        } else {
            // return employee;
        }*/

        Optional<Company> op = Optional.ofNullable(company);
        System.out.println(op.map(theCompany -> theCompany.getEmployee())
                .orElse(Collections.emptyList()));

    }


}
