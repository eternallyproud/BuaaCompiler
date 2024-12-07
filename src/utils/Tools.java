package utils;

import java.util.ArrayList;

public class Tools {
    public static void printStartMessage(String message) {
        printMessage(message + "开始");
    }

    public static void printEndMessage(String message) {
        printMessage(message + "结束");
    }

    public static void printFailMessage(String message) {
        printMessage(message + "失败");
    }

    public static void printMessage(String message) {
        System.out.println("\n*************** " + message + " ***************");
    }

    public static void printOpenInfo(String info) {
        printInfo(info + "已开启");
    }

    public static void printCloseInfo(String info) {
        printInfo(info + "已关闭");
    }

    public static void printInfo(String info) {
        System.out.println("\n>>> " + info);
    }

    public static void printOptimizeInfo(String info, boolean open){
        if(open){
            printOpenInfo(info);
        }else{
            printCloseInfo(info);
        }
    }

    public static String arrayListToString(ArrayList<?> list, String delimiter) {
        // 如果列表为空，返回空字符串
        if (list == null || list.isEmpty()) {
            return "";
        }

        // 使用 StringBuilder 进行拼接
        StringBuilder sb = new StringBuilder();

        for (Object item : list) {
            sb.append(item.toString()).append(delimiter);
        }

        // 删除最后一个多余的分隔符
        sb.setLength(sb.length() - delimiter.length());

        return sb.toString();
    }

    public static String arrayListToString(ArrayList<?> list) {
        return arrayListToString(list, "");
    }

    public static String twoArrayListToString(ArrayList<?> list1, ArrayList<?> list2) {
        // 如果列表为空，返回空字符串
        if (list1 == null || list1.isEmpty()) {
            return "";
        }

        // 使用 StringBuilder 进行拼接
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list1.size(); i++) {
            sb.append(list1.get(i));
            if (i < list2.size()) {
                sb.append(list2.get(i));
            }
        }

        return sb.toString();

    }

    public static int findSubstringOccurrences(String str, String subStr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }
        return count;
    }

    public static ArrayList<Integer> stringToAscii(String inputString) {
        ArrayList<Integer> result = new ArrayList<>();

        String decodedString = inputString
                .replaceAll("\\\\a", "\u0007")
                .replaceAll("\\\\b", "\u0008")
                .replaceAll("\\\\t", "\t")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\v", "\u000B")
                .replaceAll("\\\\f", "\f")
                .replaceAll("\\\\'", "'")
                .replaceAll("\\\\\"", "\"")
                .replaceAll("\\\\0", "\0")
                .replaceAll("\\\\", "\\");

        for (char c : decodedString.toCharArray()) {
            result.add((int) c);
        }

        return result;
    }

    public static int characterToAscii(String inputString) {
        return stringToAscii(inputString).get(0);
    }
}
