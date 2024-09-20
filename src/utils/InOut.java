package utils;

import config.Configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InOut {
    // 读取文件内容
    private static String readFile(String filePath) {
        System.out.println("开始读入: " + filePath);
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            System.out.println("读入成功\n");
            return new String(fileBytes, StandardCharsets.UTF_8).replaceAll("\r", ""); // 将字节数组转换为字符串
        } catch (IOException e) {
            System.out.println("读入失败: " + e.getMessage());
        }
        return null;
    }

    public static String readFile() {
        return readFile(Configuration.TESTFILE_PATH);
    }

    // 写入内容到文件
    private static void writeFile(String filePath, String content) {
        System.out.println("开始写入: " + filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("写入成功\n");
        } catch (IOException e) {
            System.out.println("写入失败: " + e.getMessage());
        }
    }

    public static void writeFile(String content) {
        writeFile(Configuration.LEXER_RESULT_PATH, content);
    }
}
