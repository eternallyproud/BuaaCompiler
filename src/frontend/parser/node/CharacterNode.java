package frontend.parser.node;

import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.semantic.symbol.DataType;
import frontend.lexer.token.Token;
import utils.Tools;

//<Character> ::= <CharConst>
public class CharacterNode extends Node {
    private final Token charConstToken;

    public CharacterNode(Token charConstToken) {
        super(NodeType.CHARACTER);
        this.charConstToken = charConstToken;
    }

    public DataType getDataType() {
        return DataType.CHAR.getCharToInt();
    }

    public Integer tryCalculateValue() {
        String charConst = charConstToken.getContent();
        return Tools.characterToAscii(charConst.substring(1, charConst.length() - 1));
    }

    @Override
    public Value buildIR() {
        return new Constant.Char(Tools.characterToAscii(charConstToken.getContent().substring(1, charConstToken.getContent().length() - 1)));
    }

    @Override
    public String toString() {
        return "" + charConstToken + nodeType;
    }
}
