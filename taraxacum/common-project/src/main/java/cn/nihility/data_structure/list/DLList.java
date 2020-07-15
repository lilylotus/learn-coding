package cn.nihility.data_structure.list;

import java.util.Objects;

/**
 * 双向列表
 *
 * @author clover
 * @date 2020-02-11 14:16
 */
public class DLList<T> {

    private DLNode<T> head, tail;
    private int nodeSize;

    public void addHead(final T data) {
        if (head == null) {
            head = tail = new DLNode<>(data);
        } else {
            head = new DLNode<>(data, null, head);
            head.next.previous = head;
        }

        nodeSize++;
    }

    public void addTail(final T data) {
        if (head == null) {
            head = tail = new DLNode<>(data);
        } else {
            tail = new DLNode<>(data, tail, null);
            tail.previous.next = tail;
        }
        nodeSize++;
    }

    public boolean deleteFromHead() {
        if (head == null) { return false; }
        if (head == tail) { head = null; nodeSize = 0; return true; }

        DLNode<T> tmp = head.next;

        head.next = null;
        tmp.previous = null;
        head = tmp;

        nodeSize--;

        return true;
    }

    public boolean deleteFromTail() {
        if (head == null) { return false; }
        if (head == tail) { head = null; nodeSize = 0; return  true; }

        DLNode<T> tmp = tail.previous;

        tail.previous = null;
        tmp.next = null;
        tail = tmp;

        nodeSize--;

        return true;
    }

    public boolean deleteNode(final T data) {
        if (head == null) { return false; }
        if (head == tail && head.data.equals(data)) {
            head = null;
            nodeSize = 0;
            return true;
        }

        DLNode<T> node = head, tmp;
        while (node != null) {
            if (node.data.equals(data)) {
                if (head == node) { // 删除的头节点
                    head = node.next;
                    head.previous.next = null;
                    head.previous = null;
                } else if (tail == node) { // 删除的是尾节点
                    tail = tail.previous;

                    tail.next.previous = null;
                    tail.next = null;

                    /*tmp = node.previous;
                    tail.previous = null;
                    tmp.next = null;
                    tail = tmp;*/
                } else { // 删除的是中间节点
                    node.previous.next = node.next;
                    node.next.previous = node.previous;

                    node.previous = null;
                    node.next = null;

                    /*tmp = node.previous;
                    tmp.next = null;
                    node.previous = null;
                    tmp.next = node.next;
                    node.next.previous = tmp;
                    node.next = null;*/
                }
                nodeSize--;
                return true;
            }

            node = node.next;
        }

        return false;
    }

    public DLNode<T> find(final T data) {
        if (null == head) { return null; }
        if (tail == head && Objects.equals(data, head.data)) {
            return head;
        }

        DLNode<T> node = head;
        while (node != null) {
            if (Objects.equals(data, node.data)) {
                return node;
            }
            node = node.next;
        }

        return null;
    }

    public boolean contain(final T data) {
        if (null == head) { return false; }
        if (tail == head && Objects.equals(data, head.data)) {
            return true;
        }

        DLNode<T> node = head;
        while (node != null) {
            if (Objects.equals(data, node.data)) {
                return true;
            }
            node = node.next;
        }

        return false;
    }

    public void print() {
        System.out.println("size = " + nodeSize);
        DLNode<T> node = head;
        while (node != null) {
            System.out.print(node.data + " : ");
            node = node.next;
        }
        System.out.println("\n--------------------------------");
    }

    private static class DLNode<T> {
        T data;
        DLNode<T> previous, next;

        DLNode(T data) {
            this.data = data;
        }

        DLNode(T data, DLNode<T> previous, DLNode<T> next) {
            this.data = data;
            this.previous = previous;
            this.next = next;
        }

        @Override
        public String toString() {
            return "DLNode{" +
                    "data=" + data +
                    '}';
        }
    }

    public static void main(String[] args) {

        DLList<Integer> dl = new DLList<>();

        dl.addHead(1);
        dl.addHead(2);
        dl.addHead(3);
        dl.print();

        dl.addTail(100);
        dl.addTail(200);
        dl.addTail(300);
        dl.print();


        System.out.println("delete 100 " + dl.deleteNode(100));
        System.out.println("delete 100 " + dl.deleteNode(3));
        System.out.println("delete 100 " + dl.deleteNode(300));

        dl.print();

        dl.deleteFromHead();
        dl.print();

        dl.deleteFromTail();
        dl.print();

        System.out.println("contain 200 " + dl.contain(200));
        System.out.println("find 1 " + dl.find(1));

    }
}
