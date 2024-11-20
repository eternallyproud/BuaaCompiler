package frontend.parser.node.stmt;

import frontend.parser.node.Node;
import frontend.parser.node.NodeType;

public class StmtNode extends Node {
    public StmtNode() {
        super(NodeType.STMT);
    }

    public void checkReturnVoid(){
    }

    @Override
    public String toString() {
        return nodeType.toString();
    }
}
