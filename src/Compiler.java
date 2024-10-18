import config.Configuration;
import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.Semantic;
import frontend.symbol.SymbolTable;
import utils.Checker;
import utils.InOut;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(InOut.readTestfile());
        lexer.doLexerAnalysis();
        lexer.writeTokens();
        Parser parser = new Parser(lexer);
        parser.doParserAnalysis();
        parser.writeCompUnitNode();
        Semantic semantic = new Semantic(parser);
        semantic.doSemanticAnalysis();
        SymbolTable.SYMBOL_TABLE.writeSymbolTable();
        ErrorHandler.ERROR_HANDLER.writeErrors();

        Checker.checkResult(Configuration.SEMANTIC_RESULT_PATH);
    }
}
