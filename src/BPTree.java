

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BPTree {

    // tree order
    private int m;

    // root node
    private Node root;

    // ‌tree height ascending
    private int height;

    private final class Node {
        // current number of children
        private int k;

        // current keys
        private Key[] children = new Key[m];

        // next sibling (only used for leaves)
        public Node next;

        private Node(int k) {
            this.k = k;
        }
    }

    private static class Key {
        // key value
        private double key;

        // value (only used for leaves)
        private final String val;

        // the node in next level
        private Node nextLevel;

        public Key(double key, Node nextLevel) {
            this.key  = key;
            this.val = null;
            this.nextLevel = nextLevel;
        }

        public Key(double key, String val) {

            this.key = key;
            this.val = val;
        }
    }


    public BPTree(int m) {
        // must be even and greater than 2
        if (m < 4)
            m = 4;
        this.m = m;
        root = new Node(0);
    }


    /**
     * @param key
     * @return tlisy of values for the key
     */
    public List<String> get(double key) {
        Node node = searchNode(root, key, height);
        if (node == null)
            return Arrays.asList("Null");

        List<String> result = new LinkedList<>();

        while(node != null) {
            for (int i = 0; i < node.k; i++) {
                if (node.children[i].key == key) {
                    result.add(node.children[i].val);
                }
            }

            // do not look further if the key value is bigger
            if (node.children[node.k - 1].key > key)
                break;

            node = node.next;
        }

        return result;
    }


    private Node searchNode(Node node, double key, int height) {
        Key[] children = node.children;

        if (height > 0) {
            for (int i = 0; i < node.k; i++) {
                if (i+1 == node.k || (key < children[i+1].key))
                    return searchNode(children[i].nextLevel, key, height-1);
            }
        }
        else {
            for (int j = 0; j < node.k; j++) {
                if (key == children[j].key) return node;
            }
        }
        return null;
    }


    private List<String> get(double key1, double key2) {
        Node node = searchNodeFLT(root, key1, height);

        if (node == null)
            return Arrays.asList("Null");

        List<String> result = new LinkedList<>();
        while(node != null) {
            for (int i = 0; i < node.k; i++) {
                double currentKey = node.children[i].key;
                if (currentKey >= key1 && currentKey <= key2) {
                    result.add("(" + currentKey + "," + node.children[i].val + ")");
                }
            }

            // do not look further if the key value is bigger
            if (node.children[node.k - 1].key > key2)
                break;

            node = node.next;
        }
        return result;
    }

    private Node searchNodeFLT(Node node, double key, int height) {
        Key[] children = node.children;
            for (int i = 0; i < node.k; i++) {
                if (i+1 == node.k || (key < children[i+1].key)) {
                    if (height > 0) {
                        return searchNodeFLT(children[i].nextLevel, key, height - 1);
                    } else {
                        return node;
                    }
            }
        }
        return null;
    }


    public void insert(double key, String val) {
        Node splitLeftover = insertInternal(root, key, val, height);
        if (splitLeftover == null) return;

        // need to split root
        Node tempRoot = new Node(2);
        tempRoot.children[0] = new Key(root.children[0].key, root);
        tempRoot.children[1] = new Key(splitLeftover.children[0].key, splitLeftover);
        root = tempRoot;

        // if splitting keeps propagating all the way up to root then have a new level
        height++;
    }

    private Node insertInternal(Node node, double key, String val, int height) {
        // the index where the key will be inserted (target index)
        int t;

        // a new Key is created no regardless of if the Key belongs to this level or not
        // if the Key belongs to the next level(s) the created Key acts as an internal Node
        Key newKey = new Key(key, val);

        // we have reached the leaf level just find the right index in the current node
        if (height == 0) {
            for (t = 0; t < node.k; t++) {
                if (key < node.children[t].key) {
                    break;
                }
            }
        }
        else {
            for (t = 0; t < node.k; t++) {

                // condition1: making sure t does not exceed node.children bound (or)
                // condition2: the key to be inserted is less than the next key
                if (t+1 == node.k || key < node.children[t+1].key) {

                    // key needs to be inserted at next level, decrease height by one
                    Node u = insertInternal(node.children[t].nextLevel, key, val, height-1);
                    if (u == null) {
                        // the node on the next level didn't need to be split and node has been inserted, just return
                        return null;
                    }

                    // if the node in the next level has been split the current node is internal
                    // internal key the it points
                    newKey.key = u.children[0].key;
                    newKey.nextLevel = u;
                    t++;
                    break;
                }
            }
        }

        // shift all the children on the right starting from t to right by one
        for (int i = node.k; i > t; i--)
            node.children[i] = node.children[i-1];

        // insert new key
        node.children[t] = newKey;
        node.k++;

        if (node.k < m)
            return null;
        else {
            int ceil = (int)Math.ceil(m / 2f);

            // the new node takes the bigger part in case of odd order
            Node newNode = new Node(ceil);

            // the old node (current node) takes the smaller part in case of odd order
            node.k = m /2;

            // establish a singly linked in the external level
            // 1. assign the next reference of current node to the new node next
            // 2. assign the newNode to next of current node
            newNode.next = node.next;
            node.next = newNode;

            // copy all numbers from m / 2 to ceil( m / 2.0)
            System.arraycopy(node.children, m / 2, newNode.children, 0, ceil);
            return newNode;
        }
    }

    public static void main(String[] args) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // default BPTree, just in case that order is not provided from the input
        BPTree bpt = new BPTree(4);

        for (String line : lines) {
            line = line.trim();

            // insert key
            if (line.startsWith("Insert")) {
                String insert = line.replace("Insert", "").replace("(", "").replace(")", "").trim();

                String[] split = insert.split(",");
                double key = Double.parseDouble(split[0].trim());
                String value = split[1].trim();
                bpt.insert(key, value);
            }

            // search key
            else if (line.startsWith("Search")) {
                String search = line.replace("Search", "").replace("(", "").replace(")", "").trim();
                if (search.contains(",")) { // range search
                    String[] split = search.split(",");
                    double key1 = Double.parseDouble(split[0].trim());
                    double key2 = Double.parseDouble(split[1].trim());
                    List<String> r = bpt.get(key1, key2);
                    System.out.println(String.join(",", r));
                } else {   // single search
                    double key = Double.parseDouble(search);
                    System.out.println("Search Key: " + key);
                    List<String> r = bpt.get(key);
                    System.out.println(String.join(",", r));
                }
            }
            else {
                // If the line does not start with "Search" or "Insert" then the line must be order number.
                int m = Integer.parseInt(line.trim());
                // then re-instantiate the B+Tree
                bpt = new BPTree(m - 1);
            }
        }
    }
}
