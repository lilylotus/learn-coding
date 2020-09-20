package cn.nihility.jvm;

/**
 * @author dandelion
 * @date 2020-09-20 15:10
 */
public class ClassInstance {

    public static void say() {
        System.out.println("ClassInstance say.");
    }

    public void sayInstance() {
        System.out.println("ClassInstance sayInstance");
    }

    private void say2() {
        System.out.println("Hello ClassInstance ClassLoader. normal private");
    }


}
