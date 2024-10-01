package frontend.node;

import frontend.token.Token;

import java.util.Objects;

//<UnaryExp> ::= <PrimaryExp> | <Ident> '(' [ <FuncRParams> ] ')' | <UnaryOp> <UnaryExp>
public class UnaryExpNode extends Node {
    private final PrimaryExpNode primaryExpNode;
    private final Token identToken;
    private final Token lparenToken;
    private final FuncRParamsNode funcRParamsNode;
    private final Token rparenToken;
    private final UnaryOpNode unaryOpNode;
    private final UnaryExpNode unaryExpNode;

    public UnaryExpNode(PrimaryExpNode primaryExpNode, Token identToken, Token lparenToken, FuncRParamsNode funcRParamsNode, Token rparenToken, UnaryOpNode unaryOpNode, UnaryExpNode unaryExpNode) {
        super(NodeType.UNARY_EXP);
        this.primaryExpNode = primaryExpNode;
        this.identToken = identToken;
        this.lparenToken = lparenToken;
        this.funcRParamsNode = funcRParamsNode;
        this.rparenToken = rparenToken;
        this.unaryOpNode = unaryOpNode;
        this.unaryExpNode = unaryExpNode;
    }

    @Override
    public String toString() {
        return Objects.toString(primaryExpNode, "") +
                Objects.toString(identToken, "") +
                Objects.toString(lparenToken, "") +
                Objects.toString(funcRParamsNode, "") +
                Objects.toString(rparenToken, "") +
                Objects.toString(unaryOpNode, "") +
                Objects.toString(unaryExpNode, "") + nodeType;
    }
}
