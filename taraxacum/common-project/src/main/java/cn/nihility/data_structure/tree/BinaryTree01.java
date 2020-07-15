package cn.nihility.data_structure.tree;


/**
 * BinaryTree
 *
 * @author clover
 * @date 2020-02-18 22:14
 */
public class BinaryTree01 {

    private Node root;
    private int nodeSize;

    public static void main(String[] args) {
        String data = "A(B(D(,G)),C(E,F))";

        BinaryTree01 tree = new BinaryTree01();
        tree.createBinaryTree(data);
        System.out.println(tree.getNodeSize());

        Node g = tree.findNode('G');
        System.out.println(g);

        System.out.println(tree.treeHigh());

        tree.display();
    }

    public int getNodeSize() {
        return nodeSize;
    }

    /**
     * 创建二叉树依据括号表示法： A(B(D(,G)),C(E,F))
     *          A
     *      B       C
     *  D       E       F
     *      G
     *
     * @param data
     */
    public void createBinaryTree(String data) {

        int len = data.length();
        int stackTop = -1, k = 0;
        Node[] stack = new Node[len];
        Node node = null;
        char tmp;

        for (int i = 0; i < len; i++) {
            tmp = data.charAt(i);
            switch (tmp) {
                case '(' :
                    stackTop++; stack[stackTop] = node; k = 1; break;
                case ')' :
                    stack[stackTop--] = null; break;
                case ',':
                    k = 2; break;
                default:
                    node = new Node(tmp);
                    nodeSize++;

                    if (root == null) {
                        root = node;
                    } else {
                        // 处理左节点
                        if (1 == k) {
                            stack[stackTop].lChild = node;
                        } else if (2 == k) { // 处理右节点
                            stack[stackTop].rChild = node;
                        }
                    }
            }
        }

    }

    public Node findNode(char data) {
        return findNode0(root, data);
    }

    private Node findNode0(Node node, char data) {

        if (node == null) {
            return null;
        } else if (node.data == data){
            return node;
        } else {
            Node tmp = findNode0(node.lChild, data);
            if (tmp != null) {
                return tmp;
            } else {
                return findNode0(node.rChild, data);
            }
        }
    }

    public int treeHigh() {
        return treeHigh0(root);
    }

    private int treeHigh0(Node node) {
        if (node == null) {
            return 0;
        } else {
            int lHigh = treeHigh0(node.lChild);
            int rHigh = treeHigh0(node.rChild);
            return (lHigh > rHigh ? lHigh + 1 : rHigh + 1);
        }
    }

    public void display() {
        display0(root);
    }

    private void display0(Node node) {
        if (null != node) {
            System.out.print(node.data);
            if (node.lChild != null || node.rChild != null) {
                System.out.print("(");
                display0(node.lChild);

                if (node.rChild != null) {
                    System.out.print(",");
                }

                display0(node.rChild);
                System.out.print(")");
            }
        }
    }

    static class Node {
        char data;
        Node lChild;
        Node rChild;

        Node(char data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", lChild=" + lChild +
                    ", rChild=" + rChild +
                    '}';
        }
    }
}
