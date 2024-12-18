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
    // 中间代码优化结果输出路径
    public static String IR_OPTIMIZATION_RESULT_PATH = "llvm_ir_optimized.txt";
    // 汇编代码输出路径
    public static String ASSEMBLY_RESULT_PATH = "mips.txt";
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

    /* 优化设置 */
    // 多轮次优化
    public static boolean MULTI_ROUND_OPTIMIZATION = true;
    // 不可达指令移除优化
    public static boolean REMOVE_UNREACHABLE_INSTRUCTION_OPTIMIZATION = true;
    // 不可达基本块移除优化
    public static boolean REMOVE_UNREACHABLE_BASIC_BLOCK_OPTIMIZATION = true;
    // Mem2Reg优化
    public static boolean MEM2REG_OPTIMIZATION = true;
    // 死代码删除优化
    public static boolean DELETE_DEAD_CODE_OPTIMIZATION = true;
    // 常量折叠优化
    public static boolean CONSTANT_FOLDING_OPTIMIZATION = true;
    // 全局变量编号优化
    public static boolean GLOBAL_VARIABLE_NUMBERING_OPTIMIZATION = true;
    // 全局代码移动优化
    public static boolean GLOBAL_CODE_MOTION_OPTIMIZATION = true;
    // 多余指令移除优化
    public static boolean REMOVE_REDUNDANT_INSTRUCTION_OPTIMIZATION = true;
    // 内存指令简化优化
    public static boolean MEMORY_INSTRUCTION_SIMPLIFY_OPTIMIZATION = true;
    // 移除全部类型转换指令优化
    public static boolean REMOVE_ALL_CONVERSION_INSTRUCTION_OPTIMIZATION = true;
    // 图着色寄存器分配优化
    public static boolean GRAPH_COLORING_REGISTER_ALLOCATION_OPTIMIZATION = true;
    // 窥孔优化
    public static boolean PEEP_HOLE_OPTIMIZATION = true;
}
