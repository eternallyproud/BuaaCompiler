package frontend.node;

import java.util.Objects;

//<Decl> ::= <ConstDecl> | <VarDecl>
public class DeclNode extends Node {
    private final ConstDeclNode constDeclNode;
    private final VarDeclNode varDeclNode;

    public DeclNode(ConstDeclNode constDeclNode, VarDeclNode varDeclNode) {
        super(NodeType.DECL);
        this.constDeclNode = constDeclNode;
        this.varDeclNode = varDeclNode;
    }

    @Override
    public String toString() {
        return Objects.toString(constDeclNode, "") +
                Objects.toString(varDeclNode, "");
    }
}
