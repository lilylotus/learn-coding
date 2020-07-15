package cn.nihility.data_structure.tree;

import java.util.Stack;

/**
 * 实现要求：
 * 1、根据已有的代码片段，实现一个二叉排序树；
 * 2、用中序遍历输出排序结果，结果形如：0，1，2 ，3 ，4， 5， 6， 7， 8， 9，
 * 3、注意编写代码注释
 */
public class BinaryTree {

    /* 保存二叉树的根节点信息 */
    private static Node root;

    public static void main(String[] args) {
        final int[] values = { 1, 3, 4, 5, 2, 8, 6, 7, 9, 0 };
        for (int v : values) {
            createBinaryTree(new Node(v), v);
        }
        inOrderTraverse(root);
    }

    /**
     * 创建 Binary Tree
     * @param node 插入的节点
     * @param value 插入节点值
     * @return 插入节点信息
     */
    public static Node createBinaryTree(Node node, int value) {

        // 判断 root 节点值
        if (null == root) {
            root = node;
            return node;
        }

        /* 判断插入节点是在 root 节点的左子树 (比 root 节点小) 还是右子树 (比 root 节点大)
        *   递归找到插入节点的位置
        * */
        Node current = root;
        Node addRootNode = root; // 保存树子节点,插入节点的父节点
        while (current != null) {
            addRootNode = current;

            /* 当前节点值比插入值大，递归左子树 */
            if (current.getValue() >= value) {
                current = current.getLeft();
            } else if (current.getValue() < value) {
                /* 当前节点值比插入值大，递归右子树 */
                current = current.getRight();
            }
        }

        /* 处理插入节点与插入节点父节点的关系 */
        if (addRootNode.getValue() >= value) {
            addRootNode.setLeft(node);
        } else {
            addRootNode.setRight(node);
        }

        return node;
    }

    /**
     * 中顺遍历 binary tree
     * @param node root 节点
     */
    public static void inOrderTraverse(Node node) {
        if (null == node) {
            return;
        }

        Node innerNode = node; // 父亲节点，中节点，优先输出
        Stack<Node> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();

        while (innerNode != null || !stack.isEmpty()) {
            /* 先把 root 节点的所有左节点进栈 */
            while (innerNode != null) {
                stack.push(innerNode);
                innerNode = innerNode.getLeft();
            }

            /* 优先输入 root 节点 */
            innerNode = stack.pop();
            /* 中序遍历 */
            sb.append(innerNode.getValue()).append(" ");
            innerNode = innerNode.getRight();
        }

        System.out.println(sb.toString());
    }

}

/* 二叉排序树的节点类型 */
class Node {
    // 节点值
    private int value;
    // 左节点
    private Node left;
    // 右节点
    private Node right;

    Node(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "v:" + value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}