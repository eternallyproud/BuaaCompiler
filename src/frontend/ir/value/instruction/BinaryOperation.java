package frontend.ir.value.instruction;

import frontend.ir.value.Value;
import frontend.ir.value.type.ScalarValueType;

public class BinaryOperation extends Instruction {
    private enum BinaryOperator {
        ADD, SUB, MUL, SDIV, SREM, UNDEFINED;

        private static BinaryOperator getBySymbol(String symbol) {
            return switch (symbol) {
                case "+" -> ADD;
                case "-" -> SUB;
                case "*" -> MUL;
                case "/" -> SDIV;
                case "%" -> SREM;
                default -> UNDEFINED;
            };
        }
    }

    private final BinaryOperator operator;

    public BinaryOperation(String name, String symbol, Value operand1, Value operand2) {
        super(ScalarValueType.INT32, name);
        this.operator = BinaryOperator.getBySymbol(symbol);
        addUsed(operand1);
        addUsed(operand2);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = " + operator.toString().toLowerCase() + " i32 " + getUsed(0).getName() + ", " + getUsed(1).getName();
    }
}
