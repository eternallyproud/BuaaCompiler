package config;

public class Configuration {
    /* 路径设置 */
    // 源码输入路径
    public static String TESTFILE_PATH = "testfile.txt";
    // 错误信息输出路径
    public static String ERROR_PATH = "error.txt";
    // 词法分析结果输出路径
    public static String LEXER_RESULT_PATH = "lexer.txt";
    // 语法分析结果输出路径
    public static String PARSER_RESULT_PATH = "parser.txt";
    // 语义分析结果输出路径
    public static String SEMANTIC_RESULT_PATH = "symbol.txt";
    // 中间代码输出路径
    public static String IR_RESULT_PATH = "llvm_ir.txt";
    // 标准结果路径
    public static String ANSWER_PATH = "ans.txt";

    /* 中间代码设置 */
    // 全局变量前缀
    public static String GLOBAL_VAR_IR_PREFIX = "@global_";
    // 字符串前缀
    public static String STRING_LITERAL_IR_PREFIX = "@str_";
    // 函数前缀
    public static String FUNCTION_IR_PREFIX = "@func_";
    // 基本块前缀
    public static String BASIC_BLOCK_IR_PREFIX = "block_";
    // 形参前缀
    public static String PARAMETER_IR_PREFIX = "%param_";
    // 局部变量前缀
    public static String LOCAL_VAR_IR_PREFIX = "%local_";
}
