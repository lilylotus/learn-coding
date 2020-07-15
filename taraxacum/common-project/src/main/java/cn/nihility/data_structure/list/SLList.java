package cn.nihility.data_structure.list;

/**
 * 单向链表
 *
 * @author clover
 * @date 2020-02-11 13:24
 */
public class SLList<T> {

    private SLNode<T> head, tail;
    private int nodeSize;

    public void addToHead(T data) {
        SLNode<T> tmp = new SLNode<T>(data, head);
        if (null == head) {
            head = tail = tmp;
        } else {
            head = tmp;
        }
        nodeSize++;
    }

    public void addToTail(T data) {

        if (null == head) {
            head = tail = new SLNode<>(data);
        } else {
            tail.next = new SLNode<>(data);
            tail = tail.next;
        }
        nodeSize++;
    }

    public boolean removeNode(T value) {
        if (head == null) { return false; }
        else if (head == tail && tail.data.equals(value)) { return true; }

        SLNode<T> cur = head, pre = null;

        while (cur != null) {
            if (cur.data.equals(value)) {
                // 是删除的头节点
               if (cur == head) {
                   SLNode<T> tmp = cur.next;
                   cur.next = null;
                   head = tmp;
               } else if (cur == tail) { // 删除尾节点
                    pre.next = null;
                    tail = pre;
               } else {
                   pre.next = cur.next;
                   cur.next = null;
               }

               nodeSize--;
               return true;
            }
            pre = cur;
            cur = cur.next;
        }
        return false;
    }

    public boolean removeHead() {
        if (head == null) { return false; }
        if (head == tail) { head = null; nodeSize = 0; return true; }

        SLNode<T> tmp = head.next;
        head.next = null;
        head = tmp;
        nodeSize--;

        return false;
    }

    public boolean removeTail() {
        if (head == null) { return false; }
        if (head == tail) { head = null; nodeSize = 0; return true; }

        SLNode<T> node = head;
        while (node.next != tail) {
            node = node.next;
        }

        node.next = null;
        tail = node;
        nodeSize--;

        return true;
    }

    public SLNode<T> findNode(final T node) {
        if (head == null) { return null; }
        if (head == tail && head.data.equals(node)) { return head; }

        SLNode<T> tmp = head;
        while (tmp != null) {
            if (tmp.data.equals(node)) {
                return tmp;
            }
            tmp = tmp.next;
        }

        return null;
    }

    public boolean contain(final T node) {
        if (head == null) { return false; }
        if (head == tail && head.data.equals(node)) { return true; }

        SLNode<T> tmp = head;
        while (tmp != null) {
            if (tmp.data.equals(node)) {
                return true;
            }
            tmp = tmp.next;
        }

        return false;
    }

    public void print() {
        System.out.println("---------------------------");
        System.out.println("size = " + nodeSize);
        SLNode<T> tmp = head;
        while (tmp != null) {
            System.out.print(tmp.data + " : ");
            tmp = tmp.next;
        }
        System.out.println("\n---------------------------");
    }

    public static void main(String[] args) {
        SLList<Integer> sl = new SLList<>();

        sl.addToHead(1);
        sl.addToHead(2);
        sl.addToHead(3);
        sl.print();

        sl.addToTail(100);
        sl.addToTail(200);
        sl.addToTail(300);
        sl.print();

        sl.removeHead();
        sl.print();

        sl.removeHead();
        sl.print();

        sl.removeTail();
        sl.print();

        sl.removeTail();
        sl.print();

        System.out.println("remove 100 " + sl.removeNode(100));
        sl.print();

        sl.removeTail();
        sl.print();

        System.out.println("=================================");
        for (int i = 0; i < 10; i++) {
            sl.addToHead(i * i);
        }
        sl.print();

        System.out.println("contain 81 " + sl.contain(81));
        System.out.println("contain 9 " + sl.contain(9));

        System.out.println("find node 9 " + sl.findNode(9));
        System.out.println("find node 9 " + sl.findNode(19));

    }


    private static class SLNode<T> {
        T data;
        SLNode<T> next;

        SLNode() { }

        SLNode(T data) {
            this.data = data;
        }

        SLNode(T data, SLNode<T> next) {
            this.data = data;
            this.next = next;
        }

        @Override
        public String toString() {
            return "SLNode{" +
                    "data=" + data +
                    '}';
        }
    }
}

