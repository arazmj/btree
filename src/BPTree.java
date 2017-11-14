

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        public Key(double key, String val, Node nextLevel) {
            this.key  = key;
            this.val  = val;
            this.nextLevel = nextLevel;
        }

        public Key(double key, Node nextLevel) {
            this.key  = key;
            this.val = null;
            this.nextLevel = nextLevel;
        }
    }


    public BPTree(int m) {
        // must be even and greater than 2
        if (m < 4)
            m = 4;
        if (m % 2 == 1)
            m++;
        this.m = m;
        root = new Node(0);
    }


    /**
     * @param key
     * @return the node that contains the exact match for the key
     */
    public Node getNode(double key) {
        return searchNode(root, key, height);
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

    private Node getNodeFLT(double key1) {
        return searchNodeFLT(root, key1, height);
    }

    private Node searchNodeFLT(Node node, double key, int height) {
        Key[] children = node.children;
            for (int i = 0; i < node.k; i++) {
                if (i+1 == node.k || (key < children[i+1].key)) {
                    if (height > 0) {
                        return searchNode(children[i].nextLevel, key, height - 1);
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
        height++;
    }

    private Node insertInternal(Node node, double key, String val, int height) {
        int j;
        Key newKey = new Key(key, val, null);

        if (height == 0) {
            for (j = 0; j < node.k; j++) {
                if (key < node.children[j].key) {
                    break;
                }
            }
        }
        else {
            for (j = 0; j < node.k; j++) {
                if (j+1 == node.k || key < node.children[j+1].key) {
                    Node u = insertInternal(node.children[j].nextLevel, key, val, height-1);
                    if (u == null) return null;
                    newKey.key = u.children[0].key;
                    newKey.nextLevel = u;
                    j++;
                    break;
                }
            }
        }

        for (int i = node.k; i > j; i--)
            node.children[i] = node.children[i-1];

        node.children[j] = newKey;
        node.k++;
        if (node.k < m)
            return null;
        else {
            Node t = new Node(m /2);
            node.k = m /2;
            t.next = node.next;
            node.next = t;
            System.arraycopy(node.children, m / 2, t.children, 0, m / 2);
            return t;
        }
    }

    public static void main(String[] args) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("input1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BPTree bpt = new BPTree(4);

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Insert")) {
                String insert = line.replace("Insert", "").replace("(", "").replace(")", "").trim();

                String[] split = insert.split(",");
                double key = Double.parseDouble(split[0].trim());
                String value = split[1].trim();
                bpt.insert(key, value);

            }
            else if (line.startsWith("Search")) {
                String search = line.replace("Search", "").replace("(", "").replace(")", "").trim();
                if (search.contains(",")) {
                    String[] split = search.split(",");
                    double key1 = Double.parseDouble(split[0].trim());
                    double key2 = Double.parseDouble(split[1].trim());
                    Node r = bpt.getNodeFLT(key1);

                    boolean first = true;
                    while(r != null) {
                        for (int i = 0; i < r.k; i++) {
                            double currentKey = r.children[i].key;
                            if (currentKey >= key1 && currentKey <= key2) {
                                if (!first) {
                                    System.out.print(",");
                                }
                                first = false;
                                System.out.print("(" + currentKey + "," + r.children[i].val + ")");
                            }
                        }

                        // do not look further if the key value is bigger
                        if (r.children[r.k - 1].key > key2)
                            break;

                        r = r.next;
                    }
                    System.out.println();
                } else {
                    double key = Double.parseDouble(search);
                    System.out.println("Search Key1: " + key);
                    Node r = bpt.getNode(key);

                    if (r == null) {
                        System.out.println("Null");
                    }

                    boolean first = true;
                    while(r != null) {
                        for (int i = 0; i < r.k; i++) {
                            if (r.children[i].key == key) {
                                if (!first) {
                                    System.out.print(",");
                                }
                                first = false;
                                System.out.print(r.children[i].val);
                            }
                        }

                        // do not look further if the key value is bigger
                        if (r.children[r.k - 1].key > key)
                            break;

                        r = r.next;
                    }
                    System.out.println();

                }
            }
            else {
                int m = Integer.parseInt(line.trim());
                bpt = new BPTree(m);
            }
        }
    }


}
