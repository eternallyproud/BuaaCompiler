package backend.assembly.instruction;

import backend.Register;

public class MoveInstruction extends Instruction {
    private enum MoveOperator {
        MOVE, MFLO, MFHI, UNDEFINED;

        public static MoveOperator getByString(String str) {
            for (MoveOperator operator : values()) {
                if (operator.name().toLowerCase().equals(str)) {
                    return operator;
                }
            }
            return UNDEFINED;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private final MoveOperator operator;

    public MoveInstruction(Register rd, Register rs) {
        this.operator = MoveOperator.MOVE;
        this.rs = rs;
        this.rd = rd;
    }

    public MoveInstruction(String operator, Register rd) {
        this.operator = MoveOperator.getByString(operator);
        this.rs = null;
        this.rd = rd;
    }

    @Override
    public String toString() {
        if (rs == null) {
            return super.toString() + operator + " " + rd;
        } else {
            return super.toString() + operator + " " + rd + ", " + rs;
        }
    }
}
