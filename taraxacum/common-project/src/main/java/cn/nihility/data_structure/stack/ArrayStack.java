package cn.nihility.data_structure.stack;

/**
 * ArrayStack
 *
 * @author clover
 * @date 2020-02-11 15:57
 */
public class ArrayStack<T> {

    private T[] data;
    private int length;
    private int size;

    public ArrayStack() {
        length = 10;
        size = 0;
        data = (T[]) new Object[length];
    }

    public ArrayStack(int length) {
        size = 0;
        this.length = length;
        data = (T[]) new Object[length];
    }


    public void push(T data) {
        if (size >= length) { throw new ArrayIndexOutOfBoundsException("stack array index out"); }
        this.data[size++] = data;
    }

    public T pop () {
        if (size == 0) { return null; }
        return data[--size];
    }

    public int size() {
        return this.size;
    }

    public void print() {
        System.out.println("size = " + size);
        for (int i = 0; i < size; i++) {
            System.out.print(data[i] + " : ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayStack<Integer> stack = new ArrayStack<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.print();

        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());
        System.out.println("pop " + stack.pop());
        stack.print();

    }

}
