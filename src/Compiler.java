import backend.AssemblyBuilder;
import error.ErrorHandler;
import frontend.lexer.Lexer;
import frontend.parser.Parser;
import frontend.ir.IRBuilder;
import frontend.semantic.SymbolTable;
import optimize.OptimizeManager;
import utils.InOut;

public class Compiler {
    public static void main(String[] args) {
        // lexer
        Lexer lexer = new Lexer(InOut.readTestfile());
        lexer.doLexerAnalysis();
        lexer.writeTokens();

        // parser
        Parser parser = new Parser(lexer.getTokenList());
        parser.doParserAnalysis();
        parser.writeCompUnitNode();

        // semantic
        SymbolTable.SYMBOL_TABLE.init(parser.getCompUnitNode());
        SymbolTable.SYMBOL_TABLE.doSemanticAnalysis();
        SymbolTable.SYMBOL_TABLE.writeSymbolTable();

        // error
        ErrorHandler.ERROR_HANDLER.writeErrors();

        // ir
        IRBuilder.IR_BUILDER.init(parser.getCompUnitNode());
        IRBuilder.IR_BUILDER.buildIR();
        IRBuilder.IR_BUILDER.writeIR();

        // optimize
        OptimizeManager.OPTIMIZE_MANAGER.init(IRBuilder.IR_BUILDER.getModule());
        OptimizeManager.OPTIMIZE_MANAGER.optimizeIR();
        OptimizeManager.OPTIMIZE_MANAGER.writeOptimizedIR();
        OptimizeManager.OPTIMIZE_MANAGER.optimizeAssembly();

        // assembly
        AssemblyBuilder.ASSEMBLY_BUILDER.init(IRBuilder.IR_BUILDER.getModule());
        AssemblyBuilder.ASSEMBLY_BUILDER.buildAssembly();
        AssemblyBuilder.ASSEMBLY_BUILDER.writeAssembly();

        // checker
        // Checker.checkResult(Configuration.SEMANTIC_RESULT_PATH);
    }
}
