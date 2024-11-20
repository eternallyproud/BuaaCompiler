package frontend.ir.llvm.value.global;

import backend.AssemblyBuilder;
import backend.assembly.directive.GlobalDeclaration;
import frontend.ir.llvm.value.type.ArrayValueType;
import frontend.ir.llvm.value.type.PointerValueType;
import frontend.ir.llvm.value.type.ScalarValueType;
import utils.Tools;

public class StringLiteral extends GlobalValue {
    private final String objectValue;

    public StringLiteral(String name, String objectValue) {
        super(new ArrayValueType(ScalarValueType.INT8, getObjectSize(objectValue)), name);
        this.objectValue = objectValue;
    }

    public PointerValueType evaluate() {
        return new PointerValueType(valueType);
    }

    private static int getObjectSize(String str) {
        return str.length()
                - 2 * Tools.findSubstringOccurrences(str, "\\0A")
                - 2 * Tools.findSubstringOccurrences(str, "\\00");
    }

    @Override
    public void buildAssembly() {
        AssemblyBuilder.ASSEMBLY_BUILDER.addToData(new GlobalDeclaration.Asciiz(name.substring(1), objectValue));
    }

    @Override
    public String toString() {
        return name + " = constant " + valueType + " c\"" + objectValue + "\"";
    }
}
