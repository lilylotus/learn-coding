package cn.nihility.data_structure.stack;

/**
 * ListStack
 *
 * @author clover
 * @date 2020-02-11 15:42
 */
public class ListStack<T> {

    private StackNode<T> root;
    private int nodeSize;

    public void push(T data) {
        root = new StackNode<>(data, root);
        nodeSize++;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getSize() {
        return nodeSize;
    }

    public T pop() {
        if (root == null) { return null; }

        StackNode<T> pop = root;
        root = root.next;
        pop.next = null;
        nodeSize--;

        return pop.data;
    }

    public void print() {

        System.out.println("size = " + nodeSize);
        StackNode<T> tmp = root;
        while (tmp != null) {
            System.out.print(tmp.data + " : ");
            tmp = tmp.next;
        }
        System.out.println("\n---------------------------");
    }

    public static void main(String[] args) {

        ListStack<Integer> stack = new ListStack<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.print();

        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());

        System.out.println(stack.getSize());

    }

    private static class StackNode<T> {
        T data;
        StackNode<T> next;

        StackNode(T data) {
            this.data = data;
        }

        StackNode(T data, StackNode<T> next) {
            this.data = data;
            this.next = next;
        }

    }
}
