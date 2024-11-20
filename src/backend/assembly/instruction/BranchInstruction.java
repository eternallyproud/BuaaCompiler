package backend.assembly.instruction;

import backend.Register;

public class BranchInstruction extends Instruction {
    private enum BranchOperator {
        BEQ, BNE, UNDEFINED;

        public static BranchOperator getByString(String str) {
            for (BranchOperator operator : values()) {
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

    private final BranchOperator operator;
    private final String label;

    public BranchInstruction(String operator, Register rs, Register rt, String label) {
        this.operator = BranchOperator.getByString(operator);
        this.rs = rs;
        this.rt = rt;
        this.label = label;
    }

    @Override
    public String toString() {
        return super.toString() + operator + " " + rs + ", " + rt + ", " + label;
    }
}
