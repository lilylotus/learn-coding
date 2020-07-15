package cn.nihility.data_structure.queue;

/**
 * ListQuque
 *
 * @author clover
 * @date 2020-02-11 16:23
 */
public class ListQueue<T> {

    private QueueNode<T> front, rear;
    private int size;

    public void enQueue(T data) {
        if (front == null) {
            front = rear = new QueueNode<>(data);
        } else {
           front = new QueueNode<>(data, front);
        }
        size++;
    }

    public T deQueue() {
        if (rear == null) { return null; }
        if (rear == front) {
            QueueNode<T> tmp = rear;
            rear = front = null;
            size--;
            return tmp.data;
        }

        QueueNode<T> rearPre = front;
        while (rearPre.next != rear) {
            rearPre = rearPre.next;
        }
        T re = rear.data;

        rearPre.next = null;
        rear = rearPre;
        size--;

        return re;
    }

    public void print() {
        System.out.println("Size " + size);
        QueueNode<T> tmp = front;
        while (tmp != null) {
            System.out.print(tmp.data + " : ");
            tmp = tmp.next;
        }
        System.out.println("\n--------------------");
    }


    private static class QueueNode<T> {
        T data;
        QueueNode<T> next;

        QueueNode(T data) {
            this.data = data;
        }

        QueueNode(T data, QueueNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    public static void main(String[] args) {
        ListQueue<Integer> queue = new ListQueue<>();

        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);
        queue.enQueue(4);
        queue.print();

        System.out.println("deQueue " + queue.deQueue());
        System.out.println("deQueue " + queue.deQueue());
        System.out.println("deQueue " + queue.deQueue());
        queue.print();

        System.out.println("deQueue " + queue.deQueue());
        System.out.println("deQueue " + queue.deQueue());
        queue.print();
    }
}
