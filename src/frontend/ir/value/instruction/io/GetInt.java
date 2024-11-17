package frontend.ir.value.instruction.io;

import frontend.ir.value.type.ScalarValueType;

public class GetInt extends IOInstruction {
    public GetInt(String name) {
        super(ScalarValueType.INT32, name);
    }

    public static String getDeclaration() {
        return "declare i32 @getint()";
    }

    @Override
    public String toString() {
        return super.toString() + name + " = call i32 @getint()";
    }
}
