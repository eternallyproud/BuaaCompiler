package frontend.parser.node;

public enum NodeType {
    COMP_UNIT("CompUnit"),
    DECL("Decl"),
    CONST_DECL("ConstDecl"),
    BType("BType"),
    CONST_DEF("ConstDef"),
    CONST_INIT_VAL("ConstInitVal"),
    VAR_DECL("VarDecl"),
    VAR_DEF("VarDef"),
    INIT_VAL("InitVal"),
    FUNC_DEF("FuncDef"),
    MAIN_FUNC_DEF("MainFuncDef"),
    FUNC_TYPE("FuncType"),
    FUNC_F_PARAMS("FuncFParams"),
    FUNC_F_PARAM("FuncFParam"),
    BLOCK("Block"),
    BLOCK_ITEM("BlockItem"),
    STMT("Stmt"),
    FOR_ASSIGN("ForStmt"),
    EXP("Exp"),
    COND("Cond"),
    L_VAL("LVal"),
    PRIMARY_EXP("PrimaryExp"),
    NUMBER("Number"),
    CHARACTER("Character"),
    UNARY_EXP("UnaryExp"),
    UNARY_OP("UnaryOp"),
    FUNC_R_PARAMS("FuncRParams"),
    MUL_EXP("MulExp"),
    ADD_EXP("AddExp"),
    REL_EXP("RelExp"),
    EQ_EXP("EqExp"),
    L_AND_EXP("LAndExp"),
    L_OR_EXP("LOrExp"),
    CONST_EXP("ConstExp");

    public final String typeName;

    NodeType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "<" + typeName + ">\n";
    }
}
