# B+ Tree Implementation in Java

This repository contains a Java implementation of a B+ Tree (`BPTree`). The B+ Tree is a self-balancing tree data structure that maintains sorted data and allows for efficient insertion, deletion, and range-based search operations. This implementation is parameterized by the tree's order (`m`).

## Features

- **Dynamic Tree Order**: Users can specify the order of the B+ Tree. Defaults to 4 if an invalid order is provided.
- **Efficient Search**:
    - Single-key search.
    - Range-based search for keys within a given range.
- **Insertion**: Handles key-value pair insertions and maintains balance using node splitting.
- **Range Queries**: Supports retrieving all key-value pairs within a specific range.
- **Singly Linked Leaves**: Leaves are linked for efficient range queries.

## Class Overview

### `BPTree`
- **Fields**:
    - `int m`: The order of the B+ Tree (minimum number of children for internal nodes).
    - `Node root`: The root node of the tree.
    - `int height`: The current height of the tree.
- **Methods**:
    - `void insert(double key, String val)`: Inserts a key-value pair into the tree.
    - `Set<String> get(double key)`: Retrieves values associated with a single key.
    - `Set<String> get(double key1, double key2)`: Retrieves values within a range of keys.
    - `Node searchNode(Node node, double key, int height)`: Searches for a node containing the given key.
    - `Node insertInternal(Node node, double key, String val, int height)`: Handles internal node insertions and splitting.

### `Node`
- Represents a node in the B+ Tree.
- Fields:
    - `int k`: The current number of keys in the node.
    - `Key[] children`: Array of child nodes or keys.
    - `Node next`: Points to the next leaf node (for leaf nodes only).

### `Key`
- Represents a key in the B+ Tree.
- Fields:
    - `double key`: The key value.
    - `String val`: The associated value (for leaf nodes).
    - `Node nextLevel`: The pointer to the next level (for internal nodes).

## Usage

### Compilation and Execution
1. Compile the Java file:
   ```bash
   javac BPTree.java
   ```
2. Run the program with a test file:
```
java BPTree input_file.txt
```
Replace input_file.txt with the path to your test input file.

### Input Format
The input file should contain commands in the following formats:
- **Set tree order**: Specify the order of the B+ Tree. Example: `4`
- **Insert a key-value pair**: `Insert(key, value)` Example: `Insert(3.5, "Value 3.5")`
- **Search for a single key**: `Search(key)` Example: `Search(3.5)`
- **Range-based search**: `Search(key1, key2)` Example: `Search(2.0, 4.0)`

### Output
The program outputs the search results to the standard output. For range-based searches, it outputs all key-value pairs in the specified range.

### Example
Input file:
```java
4
Insert(1.5, "Value 1.5")
Insert(2.5, "Value 2.5")
Search(1.5)
Search(1.0, 2.0)
```
Output:
```java
Value 1.5
(1.5, Value 1.5)
```

## How It Works

1. **Insertions**:
    - Keys are inserted into the appropriate leaf node.
    - If a node overflows (i.e., exceeds `m` children), it splits into two nodes.
    - Splits propagate upward if necessary, potentially creating a new root.

2. **Search**:
    - **Single-key Search**: Traverses the tree from the root to locate the relevant leaf node and retrieve associated values.
    - **Range-based Search**: Uses the linked structure of leaf nodes to efficiently retrieve all key-value pairs within the specified range.

3. **Tree Order**:
    - The order (`m`) of the B+ Tree determines:
        - The minimum number of children for internal nodes (except the root).
        - The maximum number of children any node can have.
    - Larger values of `m` result in shallower trees, improving search performance for large datasets.
