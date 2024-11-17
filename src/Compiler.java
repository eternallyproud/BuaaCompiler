import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.Semantic;
import frontend.IRBuilder;
import frontend.symbol.SymbolTable;
import utils.InOut;

public class Compiler {
    public static void main(String[] args) {
        // lexer
        Lexer lexer = new Lexer(InOut.readTestfile());
        lexer.doLexerAnalysis();
        lexer.writeTokens();

        // parser
        Parser parser = new Parser(lexer);
        parser.doParserAnalysis();
        parser.writeCompUnitNode();

        // semantic
        Semantic semantic = new Semantic(parser.getCompUnitNode());
        semantic.doSemanticAnalysis();
        SymbolTable.SYMBOL_TABLE.writeSymbolTable();

        // error
        ErrorHandler.ERROR_HANDLER.writeErrors();

        // ir
        IRBuilder.IR_BUILDER.init(parser.getCompUnitNode());
        IRBuilder.IR_BUILDER.buildIR();
        IRBuilder.IR_BUILDER.writeIR();

        // checker
        // Checker.checkResult(Configuration.SEMANTIC_RESULT_PATH);
    }
}
