package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.other.ICmp;
import utils.Tools;

import java.util.ArrayList;

public class ConstantFolding {
    public final static ConstantFolding CONSTANT_FOLDING = new ConstantFolding();

    private Module module;
    private boolean hasChanged;

    private ConstantFolding() {
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("常量折叠优化", Configuration.CONSTANT_FOLDING_OPTIMIZATION);

        optimize();
    }

    public boolean optimize() {
        hasChanged = false;
        if (Configuration.CONSTANT_FOLDING_OPTIMIZATION) {
            for (Function function : module.getFunctions()) {
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    optimize(basicBlock);
                }
            }
        }
        return hasChanged;
    }

    private void optimize(BasicBlock basicBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());

        for (Instruction instruction : instructions) {
            if (instruction instanceof BinaryOperation binaryOperation) {
                handleBinaryOperation(binaryOperation);
            } else if (instruction instanceof ICmp icmp) {
                handleICmp(icmp);
            }else if(instruction instanceof ConversionOperation conversionOperation){
                handleConversionOperation(conversionOperation);
            }
        }
    }

    private void handleBinaryOperation(BinaryOperation binaryOperation) {
        String operator = binaryOperation.getOperator();
        Value operandValue1 = binaryOperation.getUsedValue(0);
        Value operandValue2 = binaryOperation.getUsedValue(1);

        if (operandValue1 instanceof Constant constant1 && operandValue2 instanceof Constant constant2) {
            int operand1 = Integer.parseInt(constant1.getName());
            int operand2 = Integer.parseInt(constant2.getName());
            int ans = switch (operator) {
                case "+" -> operand1 + operand2;
                case "-" -> operand1 - operand2;
                case "*" -> operand1 * operand2;
                case "/" -> operand1 / operand2;
                case "%" -> operand1 % operand2;
                default -> 0;
            };
            Constant constant = new Constant.Int(ans);

            binaryOperation.updateAllUsers(constant);
            binaryOperation.removeAllUse();
            binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
            hasChanged = true;

        } else if (operandValue1 instanceof Constant constant) {
            Value newValue = getNewValue(operator, Integer.parseInt(constant.getName()), operandValue2);
            if (newValue != null) {
                binaryOperation.updateAllUsers(newValue);
                binaryOperation.removeAllUse();
                binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
                hasChanged = true;
            }
        } else if (operandValue2 instanceof Constant constant) {
            Value newValue = getNewValue(operator, operandValue1, Integer.parseInt(constant.getName()));
            if (newValue != null) {
                binaryOperation.updateAllUsers(newValue);
                binaryOperation.removeAllUse();
                binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
                hasChanged = true;
            }
        }
    }

    private static Value getNewValue(String operator, int operand1, Value operandValue2) {
        Value newValue = null;
        if (operator.equals("+") && operand1 == 0) {
            newValue = operandValue2;
        }
        if (operator.equals("*") && operand1 == 0) {
            newValue = new Constant.Int(0);
        }
        if (operator.equals("*") && operand1 == 1) {
            newValue = operandValue2;
        }
        if (operator.equals("/") && operand1 == 0) {
            newValue = new Constant.Int(0);
        }
        if (operator.equals("%") && operand1 == 0) {
            newValue = new Constant.Int(0);
        }
        return newValue;
    }

    private static Value getNewValue(String operator, Value operandValue1, int operand2) {
        Value newValue = null;
        if (operator.equals("+") && operand2 == 0) {
            newValue = operandValue1;
        }
        if (operator.equals("-") && operand2 == 0) {
            newValue = operandValue1;
        }
        if (operator.equals("*") && operand2 == 0) {
            newValue = new Constant.Int(0);
        }
        if (operator.equals("*") && operand2 == 1) {
            newValue = operandValue1;
        }
        if (operator.equals("/") && operand2 == 1) {
            newValue = operandValue1;
        }
        if (operator.equals("%") && operand2 == 1) {
            newValue = new Constant.Int(0);
        }
        return newValue;
    }

    private void handleICmp(ICmp iCmp) {
        String operator = iCmp.getOperator();
        Value operandValue1 = iCmp.getUsedValue(0);
        Value operandValue2 = iCmp.getUsedValue(1);

        if (operandValue1 instanceof Constant constant1 && operandValue2 instanceof Constant constant2) {
            int operand1 = Integer.parseInt(constant1.getName());
            int operand2 = Integer.parseInt(constant2.getName());
            boolean ans = switch (operator) {
                case "==" -> operand1 == operand2;
                case "!=" -> operand1 != operand2;
                case ">" -> operand1 > operand2;
                case ">=" -> operand1 >= operand2;
                case "<" -> operand1 < operand2;
                case "<=" -> operand1 <= operand2;
                default -> false;
            };
            Constant constant = new Constant.Bool(ans);

            iCmp.updateAllUsers(constant);
            iCmp.removeAllUse();
            iCmp.getFatherBasicBlock().removeInstruction(iCmp);
            hasChanged = true;
        }
    }

    private void handleConversionOperation(ConversionOperation conversionOperation) {
        Value operandValue = conversionOperation.getUsedValue(0);
        if (operandValue instanceof Constant constant) {
            Constant newConstant = (Constant) constant.convertTo(conversionOperation.getValueType());

            conversionOperation.updateAllUsers(newConstant);
            conversionOperation.removeAllUse();
            conversionOperation.getFatherBasicBlock().removeInstruction(conversionOperation);
            hasChanged = true;
        }
    }
}
