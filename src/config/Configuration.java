package config;

public class Configuration {
    // 工作目录
    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    // 源码路径
    public static String TESTFILE_PATH = WORKING_DIRECTORY + "\\testfile.txt";
    // 词法分析结果路径
    public static String LEXER_RESULT_PATH = WORKING_DIRECTORY + "\\output.txt";
}
