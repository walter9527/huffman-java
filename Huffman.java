package org.walter.symboltable;

import java.util.*;

public class Huffman {
    
    public static class Node implements Comparable<Node> {
        Byte ch = null;
        int weight = 0;
        Node left = null;
        Node right = null;

        public Node(Byte ch, int weight, Node left, Node right) {
            this.ch = ch;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "ch=" + ch +
                    ", weight=" + weight +
                    '}';
        }

        @Override
        public int compareTo(Node o) {
            int result = Integer.compare(this.weight, o.weight);
            if (result != 0) {
                return result;
            } else {
                if (this.ch == null && o.ch == null) {
                    return 0;
                } else if (this.ch == null) {
                    return -1;
                } else if (o.ch == null) {
                    return 1;
                } else {
                    return Byte.compare(this.ch, o.ch);
                }
                    
            }
        }
    }
  
    public Node root = null;
    public Map<Byte, String> huffmanCode = null;


    public Huffman(List<Node> list) {
        createHuffmanTree(list);
    }

    private void createHuffmanTree(List<Node> list) {
        while (list.size() > 1) {
            Collections.sort(list);
            
            Node left = list.get(0);
            Node right = list.get(1);

            Node parent = new Node(null, left.weight + right.weight, left, right);
            
            list.remove(left);
            list.remove(right);

            list.add(parent);
        }
        root = list.get(0);
    }

    public Map<Byte, String> getHuffmanCode() {
        if (root == null) {
            System.out.println("没有建立哈夫曼树");
            return huffmanCode;
        }
        
        if (huffmanCode == null) {
            huffmanCode = new HashMap<>();
            getCode(root, "");
        }
        return huffmanCode;
    } 
    
    private void getCode(Node node, String code) {
        if (node == null)
            return;
        
        if (node.ch != null) {
            huffmanCode.put(node.ch, code);
            return;
        }
        getCode(node.left, code + "0");
        getCode(node.right, code + "1");
    }
}
