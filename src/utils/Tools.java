package utils;

import java.util.ArrayList;

public class Tools {
    public static void printStartMessage(String message) {
        System.out.println("\n***************" + message + "开始***************");
    }

    public static void printEndMessage(String message) {
        System.out.println("\n***************" + message + "结束***************");
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
}
