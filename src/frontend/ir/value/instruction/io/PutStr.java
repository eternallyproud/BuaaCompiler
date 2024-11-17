package frontend.ir.value.instruction.io;

import frontend.ir.value.global.StringLiteral;
import frontend.ir.value.type.ScalarValueType;

public class PutStr extends IOInstruction {
    private final StringLiteral stringLiteral;

    public PutStr(String name, StringLiteral stringLiteral) {
        super(ScalarValueType.VOID, name);
        this.stringLiteral = stringLiteral;
    }

    public static String getDeclaration() {
        return "declare void @putstr(i8*)";
    }

    @Override
    public String toString() {
        return super.toString() + "call void @putstr(i8* getelementptr inbounds (" +
                stringLiteral.getValueType() + ", " +
                stringLiteral.evaluate() + " " +
                stringLiteral.getName() + ", i64 0, i64 0))";
    }
}
