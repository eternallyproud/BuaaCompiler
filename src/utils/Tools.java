package utils;

import java.util.ArrayList;

public class Tools {
    public static void printStartMessage(String message) {
        System.out.println("\n***************" + message + "开始***************\n");
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
        return arrayListToString(list, "\n");
    }
}
