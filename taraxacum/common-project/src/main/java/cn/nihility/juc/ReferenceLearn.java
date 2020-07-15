package cn.nihility.juc;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferenceLearn
 *
 * @author dandelion
 * @date 2020-04-13 16:52
 */
public class ReferenceLearn {

    public static void main(String[] args) {
        // 强引用
        List<String> list = new ArrayList<>();
        list.add("First");
        System.out.println(list);

        // 弱引用，在　GC 时候会自动销毁
        SoftReference<List<String>> soft = new SoftReference<>(list);
        list = soft.get();
        if (null == list) {
            list = new ArrayList<>();
            soft = new SoftReference<>(list);
        }
        list.add("softReference");
        System.out.println(list);


    }

}
