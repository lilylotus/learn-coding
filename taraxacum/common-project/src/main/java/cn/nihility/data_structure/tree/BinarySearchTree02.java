package cn.nihility.data_structure.tree;

import java.util.Stack;

/**
 * BinarySearchTree02
 *
 * @author clover
 * @date 2020-02-25 15:47
 */
public class BinarySearchTree02<E extends Comparable<E>> {

    static class Node<E> {
        Node<E> left;
        Node<E> right;
        E data;

        Node(E data) {
            this.data = data;
        }

        public Node(Node<E> left, Node<E> right, E data) {
            this.left = left;
            this.right = right;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node{" + data + '}';
        }
    }

    private Node<E> root;
    private int size;

    public BinarySearchTree02() {
        this.root = null;
        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    public Node<E> getRoot() {
        return root;
    }

    public boolean add(E data) {
        if (root == null) {
            this.root = new Node<>(data);
            size++;
            return true;
        }

        Node<E> current = root;
        Node<E> tmp = root;
        while (current != null) {
            tmp = current; // 保存叶子节点
            /* root key 大于/等于 data， 添加到左子树 */
            if (current.data.compareTo(data) >= 0) {
                current = current.left;
            } else if (current.data.compareTo(data) < 0) {
                current = current.right;
            }
        }

        /* 比叶子节点值大/等于，放在左子树 */
        if (tmp.data.compareTo(data) >= 0) {
            tmp.left = new Node<>(data);
        } else {
            tmp.right = new Node<>(data);
        }

        size++;
        return true;
    }

    public Node<E> delete(E key) {
        return deleteNode0(root, key);
    }

    /* 删除节点  */
    private Node<E> deleteNode0(Node<E> root, E key) {
        if (root == null) {
            return null;
        }

        Node<E> result;
        Node<E> current = root; // 记录要删除的节点
        Node<E> parent = root;

        while (current != null) {
            /* 左子树 */
            if (current.data.compareTo(key) > 0) {
                parent = current;
                current = current.left;
            } else if (current.data.compareTo(key) < 0) {
                parent = current;
                current = current.right;
            } else { // current 为待删除节点， parent 为待删除节点的父亲节点
                /* 当前节点为要删除节点 */
                if (current == root) { // 要删除的节点为 root 根节点
                    result = current;
                    if (current.right != null) { // 右节点不为空的情况，查找根节点的替代节点 min
                        Node<E> minNode = findMinNode(current.right); // 查找最小节点
                        Node<E> newNode = new Node<>(minNode.data);

                        newNode.left = current.left;
                        Node<E> removeMinNode = removeMinNode(current.right); // 移除右子树最小节点
                        root.right = null;  // 把根节点的右链断掉
                        newNode.right = removeMinNode;

                        this.root = newNode;

                    } else if (current.left != null) {
                        Node<E> newNode = current.left;
                        this.root.left = null; // 断掉左链条
                        this.root = newNode;
                    } else { // 仅还剩下根节点
                        this.root = null;
                    }
                    this.size--;
                }
                /* 要删除的节点为叶子节点 */
                else if (current.right == null && current.left == null) {
                    result = current;

                    if (parent.left == current) { // 要删除的叶子节点为其父亲节点的左节点，直接置为空
                        parent.left = null;
                    } else {
                        parent.right = null;
                    }

                    this.size--;
                } else if (current.right != null && current.left == null) { // 当子节点仅有右节点
                    result = current;
                    if (parent.left == current) {
                        parent.left = current.right;
                    } else {
                        parent.right = current.right;
                    }
                    current.right = null; // 断掉删除节点的所有链接
                    this.size--;
                } else if (current.right == null && current.left != null) { // 当子节点仅有左节点
                    result = current;
                    if (parent.left == current) {
                        parent.left = current.left;
                    } else {
                        parent.right = current.left;
                    }
                    current.left = null; // 断掉删除节点的所有链接
                    this.size--;
                } else { // 有两个子节点
                    Node<E> minNode = findMinNode(current.right); // 查找右子树最小节点做根节点
                    Node<E> newNode = new Node<>(minNode.data);

                    newNode.left = current.left;
                    newNode.right = removeMinNode(current.right);
                    current.right = null;

                    if (parent.data.compareTo(newNode.data) >= 0) {
                        parent.left = newNode;
                    } else {
                        parent.right = newNode;
                    }

                    result = current;
                    this.size--;
                }

                return result;
            }
        }

        return null;
    }

    public boolean contain(E key) {
        return null == findNode0(root, key) ? false : true;
    }

    public Node<E> findNode(E key) {
        return findNode0(this.root, key);
    }

    private Node<E> findNode0(Node<E> root, E key) {
        if (root == null) { return null; }

        Node<E> tmp = root;
        while (tmp != null) {
            if (tmp.data.compareTo(key) == 0) {
                break;
            } else if (tmp.data.compareTo(key) > 0) {
                tmp = tmp.left;
            } else {
                tmp = tmp.right;
            }
        }

        return tmp;
    }

    /* 删除子树当中最小的节点，并返回要做子树新根节点 */
    private Node<E> removeMinNode(Node<E> node) {
        Node<E> parent = node; // 保存父亲节点
        Node<E> current = node; // 要删除的节点

        if (node.left == null) { // 没有左节点，将自己删除
            current = node.right; // 记录要删除节点的右节点
            node.right = null; // 把要删除节点的父节点右节点置为空，断链
            return current;
        }

        /*
        *           60 (parent - 60)
        *         /
        *        50 (current - 50)   (parent - 50)
        *       /  \
        *     40   55           (current - 40 -> 要删除节点)
        *       \
        *       45
        * */
        while (current.left != null) {
            parent = current;
            current = current.left;
        }

        parent.left = current.right;
        current.right = null;
        return parent;
    }

    /* 查找子树当中最小的节点并返回该最小节点 */
    private Node<E> findMinNode(Node<E> node) {
        Node<E> tmp = node;
        Node<E> min = node;
        while (tmp != null) {
            min = tmp;
            tmp = tmp.left;
        }
        return min;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        traverse(sb, root);
        return "size=" + size + " / root = " + root + "\n" + sb.toString();
    }

    private void traverse(StringBuilder sb, Node<E> root) {
        if (root == null) {
            return;
        }

        Stack<Node<E>> stack = new Stack<>();

        while (root != null || !stack.isEmpty()) {

            /* 先把该节点的所有左节点进栈 */
            while (root != null) {
                // 前序遍历
                //sb.append(root.data + " ");
                stack.push(root);
                root = root.left;
            }

            root = stack.pop();
            // 中序遍历
            sb.append(root.data + " : ");
            root = root.right;
        }

    }

    public static void main1(String[] args) {
        BinarySearchTree02<Person> t = new BinarySearchTree02<>();
        t.add(new Person("100", 100, 1));
        t.add(new Person("200", 200, 2));
        t.add(new Person("150", 150, 3));
        t.add(new Person("50", 50, 4));
        System.out.println(t);

        t.add(new Person("50", 50, 5));
        System.out.println(t);

    }

    /* Let us create following BST
             50
          /     \
         30      70
        /  \    /  \
      20   40  60   80 */
    public static void main(String[] args) {

        int[] values = new int[] {1, 3, 4, 5, 2, 8, 6, 7, 9, 0};

        BinarySearchTree02<Integer> tree = new BinarySearchTree02<>();
        for (int v : values) {
            tree.add(v);
        }
        System.out.println(tree);

       /* tree.add(50);
        tree.add(30);
        tree.add(20);
        tree.add(40);
        tree.add(70);
        tree.add(60);
        tree.add(80);
        tree.add(10);
        tree.add(25);
        tree.add(40);
        tree.add(41);
        System.out.println(tree);*/

     /*   tree.add(100);
        tree.add(90);
        tree.add(-1);
        System.out.println(tree);*/

        /*System.out.println("===============");
        System.out.println(tree.delete(20));
        System.out.println(tree);*/

        /*System.out.println("===============");
        System.out.println(tree.delete(30));
        System.out.println(tree);*/

        /*delete(tree, 20);

        System.out.println(tree.findNode(40));
        System.out.println(tree.findNode(100));

        System.out.println(tree.contain(40));
        System.out.println(tree.contain(100));*/

    }

    public static void delete(BinarySearchTree02<Integer> tree, Integer key) {
        System.out.println("===============");
        System.out.println(tree.delete(key));
        System.out.println(tree);
    }

}
