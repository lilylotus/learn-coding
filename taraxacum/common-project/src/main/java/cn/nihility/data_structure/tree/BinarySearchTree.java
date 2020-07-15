package cn.nihility.data_structure.tree;

/**
 * 搜索二叉树
 * 1. 左节点的值比根节点要小，左子树同样
 * 2. 右节点的值比根节点要大，右子树同样
 *
 * @author clover
 * @date 2020-02-25 11:21
 */
public class BinarySearchTree<T extends Comparable<T>> {

    /* BST 的根节点 */
    private Node<T> root;
    private int size;

    /* 构造函数 */
    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(T key) {
        root = insertRecursive(root, key);
    }

    public Node<T> search(T key) {
        return searchRecursive(root, key);
    }

    /* 查询 key 节点 */
    private Node<T> searchRecursive(Node<T> root, T key) {
        /* 如果 root 为 null， 或者 root.key 等于 key */
        if (root == null || root.key.compareTo(key) == 0) {
            return root;
        }

        /* key 小于 root 节点 */
        if (root.key.compareTo(key) > 0) {
            return searchRecursive(root.left, key);
        }

        return searchRecursive(root.right, key);
    }

    /* 采用递归的方式添加节点 */
    private Node<T> insertRecursive(Node<T> root, T key) {
        /* 若 root 节点为空，直接返回该节点 */
        if (root == null) {
            this.size += 1;
            return new Node<>(key);
        }

        /* 递归处理 key < root */
        if (root.key.compareTo(key) > 0) {
            root.left = insertRecursive(root.left, key);
        } else if (root.key.compareTo(key) < 0) {
            root.right = insertRecursive(root.right, key);
        }

        /* 没有改变 */
        return root;
    }

    public void inOrder() {
        inOrderRecursive(root);
        System.out.println();
        System.out.println("size = " + size);
    }

    private void inOrderRecursive(Node<T> root) {
        if (null != root) {
            inOrderRecursive(root.left);
            System.out.print(root.key + " : ");
            inOrderRecursive(root.right);
        }
    }

    public void delete(T key) {
       root = deleteRecursive(root, key);
       if (root != null) {
           size -= 1;
       }
    }

    /*
    * 删除节点：递归的方法
    * 1. 节点为叶子节点，直接删除
    * 2. 节点仅有一个子节点，删除该节点，把子节点移到删除节点除
    * 3. 节点有两个子节点，删除该节点，在把树中该节点有序后继节点放到该处
    * */
    private Node<T> deleteRecursive(Node<T> root, T key) {

        /* 当根节点为空 */
        if (root == null) {
            return null;
        }

        /* 递归处理树 */
        if (root.key.compareTo(key) > 0) {
            root.left = deleteRecursive(root.left, key);
        } else if (root.key.compareTo(key) < 0) {
            root.right = deleteRecursive(root.right, key);
        } else {
            /* node key 等于 key， 要删除 */
            /* 仅有一个节点 */
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                /* node 有两个子节点，获取该节点的有序继承节点 */
                root.key = minValue(root.right);

                /* 删除继承节点 */
                root.right = deleteRecursive(root.right, root.key);
            }
        }

        return root;
    }

    private T minValue(Node<T> node) {
        T key = node.key;

        while (node.left != null) {
            key = node.left.key;
            node = node.left;
        }

        return key;
    }

    /* BST 的节点，包含节点值和它的左右子节点 */
    static class Node<T extends Comparable<T>> {
        T key;
        Node<T> left;
        Node<T> right;

        Node(T item) {
            this.key = item;
            left = right = null;
        }

        @Override
        public String toString() {
            return "{"  + key + '}';
        }
    }


    public static void main(String[] args) {
         /* Let us create following BST
              50
           /     \
          30      70
         /  \    /  \
       20   40  60   80 */
        /*BinarySearchTree<Person> tree = new BinarySearchTree<>();
        tree.insert(new Person("50", 50));
        tree.insert(new Person("30", 30));
        tree.insert(new Person("20", 20));
        tree.insert(new Person("40", 40));
        tree.insert(new Person("70", 70));
        tree.insert(new Person("j60", 60));
        tree.insert(new Person("80", 80));*/

//        tree.inOrder();

//        System.out.println(tree.search(new Person("70", 70)));


        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(20);
        tree.insert(40);
        tree.insert(70);
        tree.insert(60);
        tree.insert(80);
        tree.inOrder();

        System.out.println(tree.search(70));
        System.out.println(tree.search(100));

        System.out.println("============================");
        tree.delete(40);
        tree.inOrder();

        tree.delete(30);
        tree.inOrder();

        tree.delete(70);
        tree.inOrder();


        tree.delete(50);
        tree.inOrder();



    }

}
