package cn.nihility.util;

import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * AlgorithmUtils
 *
 * @author clover
 * @date 2020-02-25 21:09
 */
public class AlgorithmUtil {

    private final static Random RANDOM = new Random(System.currentTimeMillis());

    public static Integer[] randomIntegerArray(int len, int min, int max) {
        Integer[] array = new Integer[len];
        int gap = max - min + 1;
        for (int i = 0; i < len; i++) {
            array[i] = RANDOM.nextInt(gap) + min;
        }
        return array;
    }

    private static SortItem[] randomSortItemArray(int len) {
        SortItem[] array = new SortItem[len];
        for (int i = 0; i < len; i++) {
            array[i] = new SortItem(randonInt(50, 50), i);
        }
        return array;
    }

    public static char randomAlphabet() {
        return (char) (RANDOM.nextInt(26) + 97);
    }

    private static int randonInt(int start, int len) {
        return RANDOM.nextInt(len + 1) + start;
    }



    public static ArrayList<Integer> randomIntegerArrayList(int len, int min, int max) {
        ArrayList<Integer> list = new ArrayList<>();

        int gap = max - min + 1;
        for (int i = 0; i < len; i++) {
            list.add(RANDOM.nextInt(gap) + min);
        }

        return list;
    }

    /**
     * 比较两个元素的大小， 默认 x - y 操作
     * @param x 比较对象 x
     * @param y 比较对象 y
     * @param <E> 泛型参数
     * @return true -> x < y
     */
    public static <E extends Comparable<E>> boolean lt(E x, E y) {
        return x.compareTo(y) < 0;
    }

    /**
     * 比较两个元素大小， x <= y
     * @param x 比较对象 x
     * @param y 比较对象 y
     * @param <E> 泛型参数
     * @return true -> x <= y
     */
    public static <E extends Comparable<E>> boolean le(E x, E y) {
        return lt(x, y) || eq(x, y);
    }

    /**
     * 比较两个元素是否相等
     * @param x 对比元素 x
     * @param y 对比元素 y
     * @param <E> 泛型参数 E
     * @return true -> x == y
     */
    public static <E extends Comparable<E>> boolean eq(E x, E y) {
        return x.compareTo(y) == 0;
    }

    /**
     * 元素 x 与 y 是否不相等
     * @return true -> x != y
     */
    public static <E extends Comparable<E>> boolean ne(E x, E y) {
        return !eq(x, y);
    }

    /**
     * 判断元素 x 是否大于 y
     * @param x 对比元素 x
     * @param y 对比元素 y
     * @param <E> 泛型参数 E
     * @return true -> x > y
     */
    public static <E extends Comparable<E>> boolean gt(E x, E y) {
        return x.compareTo(y) > 0;
    }

    /**
     * 判断 x 是否大于/等于 y
     * @return true -> x >= y
     */
    public static <E extends Comparable<E>> boolean ge(E x, E y) {
        return gt(x, y) || eq(x, y);
    }

    /**
     * 交换数组两个元素位置
     * @param array 要操作的数组
     * @param l 要交换的左元素 index
     * @param r 交换元素右元素 index
     * @return true -> 操作成功
     */
    public static <E extends Comparable<E>> boolean swap(E[] array, int l, int r) {
        if (array == null || array.length == 0) { return false; }
        if (array.length <= l || l < 0) { return false; }
        if (array.length <= r || r < 0) { return false; }
        /*if (l >= r) { return false; }*/

        E tmp = array[l];
        array[l] = array[r];
        array[r] = tmp;
        return true;
    }

    /**
     * 打印数组内容
     * @param array 要打印的数组
     */
    public static <E extends Comparable<E>> void print(E[] array) {
        if (null == array || array.length == 0) { return; }

        Arrays.stream(array).forEach(item -> {
            System.out.print(item + " : ");
        });

        System.out.println();
    }

    public static <E extends Comparable<E>> void processSort(ISort<E> sort) {
        processSort(sort, 20, 10, 400);
    }

    public static <E extends Comparable<E>> void processSort(ISort<E> sort, int len) {
        processSort(sort, len, 10, 1000);
    }


    public static <E extends Comparable<E>> void processSort(ISort<E> sort, int len, int min, int max) {
        StopWatch stopWatch = new StopWatch();

        Integer[] array = randomIntegerArray(len, min, max);
        print(array);
        stopWatch.start();
        sort.sort((E[]) array);
        stopWatch.stop();
        print(array);

        System.out.println("time : " + stopWatch.getTime(TimeUnit.MICROSECONDS));
    }

    public static <E extends Comparable<E>> void processSortItem(ISort<E> sort) {
        StopWatch stopWatch = new StopWatch();

        SortItem[] array = randomSortItemArray(15);
        print(array);
        stopWatch.start();
        sort.sort((E[]) array);
        stopWatch.stop();
        print(array);

        System.out.println("time : " + stopWatch.getTime(TimeUnit.MICROSECONDS));
    }

    /**
     * 判断 array 是否为空 (null 和 empty)
     * @return true array 为空
     */
    public static <E> boolean arrayIsEmptyOrNull(E[] array) {
        return null == array || array.length == 0;
    }

    public static class SortItem implements Comparable<SortItem> {
        private int data;
        private int index;

        public SortItem(int data, int index) {
            this.data = data;
            this.index = index;
        }

        @Override
        public int compareTo(SortItem o) {
            if (null == o) { return 1; }
            return Integer.compare(this.data, o.data);
        }

        @Override
        public String toString() {
            return "[" + data + "," + index + "]";
        }
    }

    public interface ISort<E extends Comparable<E>> {
        void sort(E[] array);
    }
}
