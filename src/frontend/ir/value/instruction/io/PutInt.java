package frontend.ir.value.instruction.io;

import frontend.ir.value.Value;
import frontend.ir.value.type.ScalarValueType;

public class PutInt extends IOInstruction {
    public PutInt(String name, Value used) {
        super(ScalarValueType.VOID, name);
        addUsed(used);
    }

    public static String getDeclaration() {
        return "declare void @putint(i32)";
    }

    @Override
    public String toString() {
        return super.toString() + "call void @putint(i32 " + getUsed(0).getName() + ")";
    }
}
