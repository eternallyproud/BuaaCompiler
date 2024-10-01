package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<FuncDef> ::= <FuncType> <Ident> '(' [ <FuncFParams> ] ')' <Block>
public class FuncDefNode extends Node {
    private final FuncTypeNode funcTypeNode;
    private final Token identToken;
    private final Token lparentToken;
    private final FuncFParamsNode funcFParamsNode;
    private final Token rparentToken;
    private final BlockNode blockNode;

    public FuncDefNode(FuncTypeNode funcTypeNode, Token identToken, Token lparentToken, FuncFParamsNode funcFParamsNode, Token rparentToken, BlockNode blockNode) {
        super(NodeType.FUNC_DEF);
        this.funcTypeNode = funcTypeNode;
        this.identToken = identToken;
        this.lparentToken = lparentToken;
        this.funcFParamsNode = funcFParamsNode;
        this.rparentToken = rparentToken;
        this.blockNode = blockNode;
    }

    @Override
    public String toString() {
        return "" + funcTypeNode + identToken + lparentToken + Objects.toString(funcFParamsNode, "") + rparentToken + blockNode + nodeType;
    }
}
