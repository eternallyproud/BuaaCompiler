package utils;

import config.Configuration;
import error.ErrorHandler;

public class Checker {
    private static void checkForDifference(String result, String answer) {
        // 将字符串按行分割
        String[] lines1 = result.split("\n");
        String[] lines2 = answer.split("\n");

        // 找到最短的数组长度
        int minLength = Math.min(lines1.length, lines2.length);

        // 比较每一行
        for (int i = 0; i < minLength; i++) {
            if (!lines1[i].equals(lines2[i])) {
                System.out.println("\n检查结果: Wrong Answer!");
                System.out.println("At line: " + (i + 1));
                System.out.println("Your result: " + lines1[i]);
                System.out.println("Correct answer: " + lines2[i]);
                return;
            }
        }

        // 如果前面的行都相同，检查是否有一个字符串有多余的行
        if (lines1.length != lines2.length) {
            System.out.println("\n检查结果: Wrong Answer!");
            System.out.println("At line: " + (minLength + 1));
            System.out.println("Your result: " + (lines1.length > minLength ? lines1[minLength] : "无"));
            System.out.println("Correct answer: " + (lines2.length > minLength ? lines2[minLength] : "无"));
        } else {
            System.out.println("\n检查结果: Accepted!");
        }
    }

    private static void check(String resultPath) {
        Tools.printStartMessage("结果检查");

        String result = InOut.readFile(resultPath);
        String answer = InOut.readFile(Configuration.ANSWER_PATH);

        if (result != null && answer != null) {
            checkForDifference(result, answer);
        }

        Tools.printEndMessage("结果检查");
    }

    public static void checkResult(String resultPath) {
        if (ErrorHandler.ERROR_HANDLER.isEmpty()) {
            check(resultPath);
        } else {
            check(Configuration.ERROR_PATH);
        }
    }
}
