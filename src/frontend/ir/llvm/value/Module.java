package frontend.ir.llvm.value;

import backend.AssemblyBuilder;
import backend.assembly.Label;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.global.StringLiteral;
import frontend.ir.llvm.value.instruction.io.GetChar;
import frontend.ir.llvm.value.instruction.io.GetInt;
import frontend.ir.llvm.value.instruction.io.PutCh;
import frontend.ir.llvm.value.instruction.io.PutInt;
import frontend.ir.llvm.value.instruction.io.PutStr;
import frontend.ir.llvm.value.type.OtherValueType;
import utils.Tools;

import java.util.ArrayList;

public class Module extends Value {
    private final ArrayList<String> declarations;
    private final ArrayList<GlobalVariable> globalVariables;
    private final ArrayList<StringLiteral> stringLiterals;
    private final ArrayList<Function> functions;

    public Module() {
        super(OtherValueType.MODULE, "");
        this.declarations = new ArrayList<>();
        this.globalVariables = new ArrayList<>();
        this.stringLiterals = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void initModule() {
        addDeclaration(GetInt.getDeclaration());
        addDeclaration(GetChar.getDeclaration());
        addDeclaration(PutInt.getDeclaration());
        addDeclaration(PutCh.getDeclaration());
        addDeclaration(PutStr.getDeclaration());
    }

    private void addDeclaration(String declaration) {
        this.declarations.add(declaration);
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        this.globalVariables.add(globalVariable);
    }

    public void addStringLiteral(StringLiteral stringLiteral) {
        this.stringLiterals.add(stringLiteral);
    }

    public void addFunction(Function function) {
        this.functions.add(function);
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    @Override
    public void buildAssembly() {
        for (GlobalVariable globalVariable : globalVariables) {
            globalVariable.buildAssembly();
        }

        for (StringLiteral stringLiteral : stringLiterals) {
            stringLiteral.buildAssembly();
        }

        functions.get(functions.size() - 1).buildAssembly();

        for (int i = functions.size() - 2; i >= 0; i--) {
            functions.get(i).buildAssembly();
        }

        Label label = new Label("exit");
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(label);
    }

    @Override
    public String toString() {
        return Tools.arrayListToString(declarations, "\n") + "\n\n" +
                Tools.arrayListToString(globalVariables, "\n") + "\n\n" +
                Tools.arrayListToString(stringLiterals, "\n") + "\n\n" +
                Tools.arrayListToString(functions, "\n");
    }
}
