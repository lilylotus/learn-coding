package cn.nihility.jvm;

/**
 * MyClassLoader
 *
 * @author clover
 * @date 2020-02-22 20:17
 */
public class MyClassExt {
    public void say() {
        System.out.println("Hello Ext ClassLoader. normal public");
    }
	
	private void say2() {
	    System.out.println("Hello Ext ClassLoader. normal private");
    }

    public static void main(String[] args) {

    }
}
