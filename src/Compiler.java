import error.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import utils.InOut;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(InOut.readTestfile());
        lexer.doLexerAnalysis();
        lexer.writeTokens();
        Parser parser = new Parser(lexer);
        parser.doParserAnalysis();
        parser.writeCompUnitNode();
        ErrorHandler.ERROR_HANDLER.writeErrors();
    }
}
