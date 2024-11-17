package frontend.ir.value.instruction.other;

import frontend.ir.value.Value;
import frontend.ir.value.type.ScalarValueType;

//<result> = icmp <cmp op> <ty> <op1>, <op2>
public class ICmp extends OtherOperation {
    private enum CompareOperator {
        EQ, NE, SGT, SGE, SLT, SLE, UNDEFINED;

        private static CompareOperator getBySymbol(String symbol) {
            return switch (symbol) {
                case "==" -> EQ;
                case "!=" -> NE;
                case ">" -> SGT;
                case ">=" -> SGE;
                case "<" -> SLT;
                case "<=" -> SLE;
                default -> UNDEFINED;
            };
        }
    }

    private final CompareOperator operator;

    public ICmp(String name, String symbol, Value operand1, Value operand2) {
        super(ScalarValueType.INT1,name);
        operator = CompareOperator.getBySymbol(symbol);
        addUsed(operand1);
        addUsed(operand2);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = icmp " + operator.toString().toLowerCase() + " i32 "
                + getUsed(0).getName() + ", " + getUsed(1).getName();
    }
}
