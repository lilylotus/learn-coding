package cn.nihility.data_structure.queue;

/**
 * ArrayQueue
 *
 * @author clover
 * @date 2020-02-11 16:06
 */
public class ArrayQueue<T> {

    private T[] data;
    private int arraySize;
    private int queueSize;

    public ArrayQueue() {
        this(10);
    }

    public ArrayQueue(int length) {
        arraySize = length;
        data = (T[]) new Object[arraySize];
    }

    public void enQueue(T value) {
        if (queueSize == arraySize) { throw new IndexOutOfBoundsException("Queue full"); }

        if (0 == queueSize) {
            data[queueSize++] = value;
        } else {
            for (int i = queueSize; i >= 1; i--) {
                data[i] = data[i -1];
            }
            data[0] = value;
            queueSize++;
        }
    }

    public T deQueue() {
        if (queueSize == 0) { return null; }

        return data[--queueSize];
    }

    public void print() {
        System.out.println("size = " + queueSize);
        for (int i = 0; i < queueSize; i++) {
            System.out.print(data[i] + " : ");
        }
        System.out.println("\n-------------------------------");
    }

    public static void main(String[] args) {
        ArrayQueue<Integer> queue = new ArrayQueue<>();

        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        queue.enQueue(4);
        queue.enQueue(5);
        queue.print();

        System.out.println("deQueue " + queue.deQueue());
        System.out.println("deQueue " + queue.deQueue());
        System.out.println("deQueue " + queue.deQueue());
        queue.print();
    }

}
