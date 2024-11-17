package frontend.ir.value.instruction.io;

import frontend.ir.value.type.ScalarValueType;

public class GetChar extends IOInstruction {
    public GetChar(String name) {
        super(ScalarValueType.INT32, name);
    }

    public static String getDeclaration() {
        return "declare i32 @getchar()";
    }

    @Override
    public String toString() {
        return super.toString() + name + " = call i32 @getchar()";
    }
}
