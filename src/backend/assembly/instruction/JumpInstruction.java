package backend.assembly.instruction;

import backend.Register;

public class JumpInstruction extends Instruction {
    private enum JumpOperator {
        J, JR, JAL, UNDEFINED;

        public static JumpOperator getByString(String str) {
            for (JumpOperator operator : values()) {
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

    private final JumpOperator jumpOperator;
    private final String target;

    public JumpInstruction(String operator, String target, Register rs) {
        this.jumpOperator = JumpOperator.getByString(operator);
        this.target = target;
        this.rs = rs;
    }

    @Override
    public String toString() {
        if (target != null) {
            return super.toString() + jumpOperator + " " + target;
        } else {
            return super.toString() + jumpOperator + " " + rs;
        }
    }
}
