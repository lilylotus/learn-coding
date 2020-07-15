package cn.nihility.jdk8.proxy;

/**
 * HelloService
 *
 * @author dandelion
 * @date 2020-04-13 14:12
 */
public class HelloService {

    public HelloService() {
        System.out.println("HelloService Constructor");
    }

    /**
     * 该方法不能被子类覆盖， cglib 不能代理 final 修饰的方法
     */
    final public String sayOther(String name) {
        System.out.println("HelloService:sayOther >> " + name);
        return "sayOther:" + name;
    }

    public void sayHello() {
        System.out.println("HelloService:sayHello()");
    }

}
