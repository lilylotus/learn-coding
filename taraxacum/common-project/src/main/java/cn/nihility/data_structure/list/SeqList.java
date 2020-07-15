package cn.nihility.data_structure.list;

/**
 * 顺序列表
 *
 * @author clover
 * @date 2020-02-11 00:41
 */
public class SeqList<T> {

    private T[] data;
    private int dataSize; // 数据的大小
    private int length; // 默认的数组长度 ， 10

    public SeqList(int length) {
        dataSize = 0;
        this.length = length;
        data = (T[]) new Object[length];
    }

    public SeqList() {
        this(10);
    }

    public SeqList(int length, T defaultValue) {
        this(length);

        for (int i = 0; i < length; i++) {
            data[i] = defaultValue;
        }
    }

    public int size() {
        return dataSize;
    }

    public void add(int index, T value) {
        if (index < 1 || index > length) { return; }

        for (int i = dataSize; i >= index; i--) {
            data[i] = data[i - 1];
        }
        data[index - 1] = value;
        dataSize++;
    }

    public void addToHead(T value) {
        autoExtend();
        if (dataSize == 0) { data[0] = value; dataSize++; }
        else { add(1, value); }
    }

    public void addToTail(T value) {
        autoExtend();
        if (dataSize == 0) { data[0] = value; dataSize++; }
        else { data[dataSize++] = value; }
    }

    public T remove(int index) {
        if (index < 1 || index > dataSize) { return null; }
        T tmp = data[index - 1];

        for (int i = index - 1, j = dataSize - 1; i < j; i++) {
            data[i] = data[i + 1];
        }
        data[--dataSize] = null;

        return tmp;
    }

    public boolean remove(T value) {
        for (int i = 0; i < dataSize; i++) {
            if (data[i].equals(value)) {
                remove(i + 1);
                return true;
            }
        }
        return false;
    }

    public boolean contain(T value) {
        if (0 == dataSize) { return  false; }
        else if (1 == dataSize) { return data[0].equals(value); }
        else {
            for (int i = 0; i < dataSize; i++) {
                if (data[i].equals(value)) { return true; }
            }
        }
        return false;
    }

    public int findIndex(T value) {
        if (0 == dataSize) { return  -1; }
        else if (1 == dataSize) { return data[0].equals(value) ? 1 : -1; }
        else {
            for (int i = 0; i < dataSize; i++) {
                if (data[i].equals(value)) { return i + 1; }
            }
        }
        return -1;
    }

    public void print() {
        System.out.println("---------------------------");
        System.out.println("size = " + dataSize);
        for (int i = 0; i < dataSize; i++) {
            System.out.print(data[i] + " : ");
        }
        System.out.println("\n---------------------------");
    }

    private void autoExtend() {
        if (dataSize >= length) {
            autoScale(length + (length / 2));
        }
    }

    private void autoScale(int newLen) {
        T[] newData = (T[]) new Object[newLen];
        System.arraycopy(data, 0, newData, 0, dataSize);
        this.data = newData;
        this.length = newLen;
        System.out.println("new len " + newLen);
    }

    private void autoReduce() {
        int half = length / 2;
        if (half > 10 && dataSize < half) {
            autoScale(half);
        }
    }

    public static void main(String[] args) {
        SeqList<Integer> sl = new SeqList<>();

        sl.addToHead(10);
        sl.addToHead(20);
        sl.addToHead(30);
        sl.print();

        sl.addToTail(100);
        sl.addToTail(200);
        sl.addToTail(300);
        sl.print();

        System.out.println("======================");

        sl.remove(1);
        sl.remove(200);
        sl.print();

        System.out.println("====================");
        for (int i = 0; i < 200; i++) {
            sl.addToTail(i * i);
        }
        sl.print();

        System.out.println("contain = " + sl.contain(20));
        System.out.println("find = " + sl.findIndex(20));
        System.out.println("find = " + sl.findIndex(201));

    }

}
