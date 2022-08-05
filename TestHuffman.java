package org.walter.symboltable;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class TestHuffman {
    public static void main(String[] args) {
        /*
        String s = "can you can a can as a canner can can a can.";
        byte[] bytes = s.getBytes();

        // 获取霍夫曼编码
        Map<Byte, String> huffmanCode = getHuffman(bytes);

        // 霍夫曼压缩
        byte[] huffman_bytes = huffmanZip(bytes, huffmanCode);
        
        // 霍夫曼解码
        byte[] re_bytes = huffmanUnzip(huffmanCode, huffman_bytes);
         */
//        zipFile("e:\\1.png", "e:\\2.zip");
//        unzipFile("e:\\2.zip", "e:\\3.png");
        zipFile("e:\\Harry.txt", "e:\\Harry.zip");
        unzipFile("e:\\Harry.zip", "e:\\Harry1.txt");
    }

    private static byte[] huffmanUnzip(Map<Byte, String> huffmanCode, byte[] huffman_bytes) {
        Map<String, Byte> huffmanDiscode = getDiscode(huffmanCode);
        return unzip(huffman_bytes, huffmanDiscode);
    }

    public static void unzipFile(String src, String dst) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(src)));
             OutputStream os = Files.newOutputStream(Paths.get(dst))) {

            byte[] b = (byte[]) ois.readObject();
            Map<Byte, String> huffmanCode = (Map<Byte, String>) ois.readObject();
            byte[] bytes = huffmanUnzip(huffmanCode, b);
            os.write(bytes, 0, bytes.length);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void zipFile(String src, String dst) {
        try (InputStream is = Files.newInputStream(Paths.get(src));
             ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(dst)))) {
           
            byte[] buf = new byte[is.available()];
            is.read(buf);
            Map<Byte, String> huffmanCode = getHuffman(buf);
            System.out.println(huffmanCode);
            byte[] encode_buf = huffmanZip(buf, huffmanCode);
            oos.writeObject(encode_buf);
            oos.writeObject(huffmanCode);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Map<Byte, String> getHuffman(byte[] bytes) {
        // 得到各字符出现的次数
        List<Huffman.Node> nodeList = getCountMap(bytes);

        // 获取霍夫曼编码
        Huffman h = new Huffman(nodeList);
        return h.getHuffmanCode();
    }
    

    private static byte[] unzip(byte[] huffman_bytes, Map<String, Byte> huffmanDiscode) {
        StringBuilder sb = new StringBuilder();
        int last_len = huffman_bytes[huffman_bytes.length - 1];
        for (int i = 0; i < huffman_bytes.length - 1; i++) {
            byte b = huffman_bytes[i];
            int tmp = b;
            tmp |= 256;
            String s = Integer.toBinaryString(tmp);
            sb.append(s.substring(s.length() - (i == huffman_bytes.length - 2 ? last_len : 8)));
        }
        
        int max = huffmanDiscode.keySet().stream().mapToInt(String::length).max().getAsInt();
        int min = huffmanDiscode.keySet().stream().mapToInt(String::length).min().getAsInt();
        
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < sb.length(); ) {
            for (int j = min; j <= max; j++) {
                String code = sb.substring(i, i + j);
                if (huffmanDiscode.containsKey(code)) {
                    list.add(huffmanDiscode.get(code));
                    i += j;
                    break;
                }
            }
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    private static Map<String, Byte> getDiscode(Map<Byte, String> huffmanCode) {
        Map<String, Byte> huffmanDiscode = new HashMap<>();

        for (Map.Entry<Byte, String> entry : huffmanCode.entrySet()) {
            huffmanDiscode.put(entry.getValue(), entry.getKey());
        }
        return huffmanDiscode;
    }

    private static byte[] huffmanZip(byte[] bytes, Map<Byte, String> huffmanCode) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(huffmanCode.get(b));
        }
        byte[] huffman_bytes = new byte[(int)ceil(sb.length() / 8.0) + 1];
        for (int i = 0; i < huffman_bytes.length - 1; i++) {
            huffman_bytes[i] = (byte)Short.parseShort(sb.substring(i * 8, min((i + 1) * 8, sb.length())), 2);
        }
        huffman_bytes[huffman_bytes.length - 1] = (byte) (sb.length() % 8);
        return huffman_bytes;
    }

    public static List<Huffman.Node> getCountMap(byte[] bytes) {
        List<Huffman.Node> list = new ArrayList<>();
        Map<Byte, Integer> map = new HashMap<>();
        for (byte b : bytes) {
            if (map.containsKey(b))
                map.put(b, map.get(b) + 1);
            else 
                map.put(b, 1);
        }
        for (Map.Entry<Byte, Integer> entry : map.entrySet()) {
            list.add(new Huffman.Node(entry.getKey(), entry.getValue(), null, null));
        }
        return list;
    }
}
