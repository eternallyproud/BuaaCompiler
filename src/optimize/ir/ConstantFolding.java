package optimize.ir;

import config.Configuration;
import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.User;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.memory.Alloca;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.memory.Load;
import frontend.ir.llvm.value.instruction.other.ICmp;
import frontend.ir.llvm.value.type.ScalarValueType;
import utils.Tools;

import java.util.ArrayList;
import java.util.Iterator;

public class ConstantFolding {
    public final static ConstantFolding CONSTANT_FOLDING = new ConstantFolding();

    private Module module;
    private boolean hasChanged;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;

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
                currentFunction = function;
                for (BasicBlock basicBlock : function.getBasicBlocks()) {
                    currentBasicBlock = basicBlock;
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
            } else if (instruction instanceof ConversionOperation conversionOperation) {
                handleConversionOperation(conversionOperation);
            } else if (instruction instanceof GetElementPtr getElementPtr) {
                handleGetElementPtr(getElementPtr);
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
            if (cyclicConstantMerge(binaryOperation, operator, operandValue2, Integer.parseInt(constant.getName()))) {
                return;
            }
            Value newValue = getNewValue(operator, Integer.parseInt(constant.getName()), operandValue2);
            if (newValue != null) {
                binaryOperation.updateAllUsers(newValue);
                binaryOperation.removeAllUse();
                binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
                hasChanged = true;
            } else {
                Instruction newInstruction = getNewInstruction(operator, Integer.parseInt(constant.getName()), operandValue2);
                if (newInstruction != null) {
                    binaryOperation.updateAllUsers(newInstruction);
                    binaryOperation.removeAllUse();
                    binaryOperation.getFatherBasicBlock().updateInstruction(binaryOperation, newInstruction);
                    newInstruction.setFatherBasicBlock(binaryOperation.getFatherBasicBlock());
                    hasChanged = true;
                }
            }
        } else if (operandValue2 instanceof Constant constant) {
            if (cyclicConstantMerge(binaryOperation, operator, operandValue1, Integer.parseInt(constant.getName()))) {
                return;
            }
            Value newValue = getNewValue(operator, operandValue1, Integer.parseInt(constant.getName()));
            if (newValue != null) {
                binaryOperation.updateAllUsers(newValue);
                binaryOperation.removeAllUse();
                binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
                hasChanged = true;
            } else {
                Instruction newInstruction = getNewInstruction(operator, operandValue1, Integer.parseInt(constant.getName()));
                if (newInstruction != null) {
                    binaryOperation.updateAllUsers(newInstruction);
                    binaryOperation.removeAllUse();
                    binaryOperation.getFatherBasicBlock().updateInstruction(binaryOperation, newInstruction);
                    newInstruction.setFatherBasicBlock(binaryOperation.getFatherBasicBlock());
                    hasChanged = true;
                }
            }
        } else {
            Value newValue = getNewValue(operator, operandValue1, operandValue2);
            if (newValue != null) {
                binaryOperation.updateAllUsers(newValue);
                binaryOperation.removeAllUse();
                binaryOperation.getFatherBasicBlock().removeInstruction(binaryOperation);
                hasChanged = true;
            }
        }
    }

    private boolean cyclicConstantMerge(BinaryOperation binaryOperation, String operator, Value valueOperand, int constantOperand) {
        if (valueOperand instanceof BinaryOperation otherBinaryOperation && otherBinaryOperation.getOperator().equals(operator)) {
            if ((operator.equals("*") || operator.equals("+"))) {
                Integer otherConstantOperand = null;
                Value otherValueOperand = null;
                if (otherBinaryOperation.getUsedValue(0) instanceof Constant constant) {
                    otherConstantOperand = Integer.parseInt(constant.getName());
                    otherValueOperand = otherBinaryOperation.getUsedValue(1);
                } else if (otherBinaryOperation.getUsedValue(1) instanceof Constant constant) {
                    otherConstantOperand = Integer.parseInt(constant.getName());
                    otherValueOperand = otherBinaryOperation.getUsedValue(0);
                }
                if (otherConstantOperand != null) {
                    if (operator.equals("+")) {
                        constantOperand = otherConstantOperand + constantOperand;
                    } else {
                        constantOperand = otherConstantOperand * constantOperand;
                    }
                    //create new one
                    Constant newConstant = new Constant.Int(constantOperand);
                    BinaryOperation newBinaryOperation = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(currentFunction), operator, otherValueOperand, newConstant);
                    newBinaryOperation.setFatherBasicBlock(currentBasicBlock);
                    currentBasicBlock.updateInstruction(binaryOperation, newBinaryOperation);

                    //remove old one
                    binaryOperation.updateAllUsers(newBinaryOperation);
                    binaryOperation.removeAllUse();
                    return true;
                }
            }
        }
        return false;
    }

    private Value getNewValue(String operator, int operand1, Value operandValue2) {
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

    private Instruction getNewInstruction(String operator, int operand1, Value operandValue2) {
        Instruction newInstruction = null;
        if (operator.equals("*") && operand1 == -1) {
            newInstruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(currentFunction), "-", new Constant.Int(0), operandValue2);
        }
        return newInstruction;
    }

    private Value getNewValue(String operator, Value operandValue1, int operand2) {
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
        if (operator.equals("%") && (operand2 == 1 || operand2 == -1)) {
            newValue = new Constant.Int(0);
        }
        return newValue;
    }

    private Instruction getNewInstruction(String operator, Value operandValue1, int operand2) {
        Instruction newInstruction = null;
        if (operator.equals("*") && operand2 == -1) {
            newInstruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(currentFunction), "-", new Constant.Int(0), operandValue1);
        }
        if (operator.equals("/") && operand2 == -1) {
            newInstruction = new BinaryOperation(IRBuilder.IR_BUILDER.getLocalVarName(currentFunction), "-", new Constant.Int(0), operandValue1);
        }
        return newInstruction;
    }

    private Value getNewValue(String operator, Value operandValue1, Value operandValue2) {
        Value newValue = null;
        if (operandValue1.equals(operandValue2)) {
            if (operator.equals("-")) {
                newValue = new Constant.Int(0);
            }
            if (operator.equals("/")) {
                newValue = new Constant.Int(1);
            }
            if (operator.equals("%")) {
                newValue = new Constant.Int(0);
            }
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

    private void handleGetElementPtr(GetElementPtr getElementPtr) {
        removeGetElementPtrOfGlobalConstArray(getElementPtr);
        removeGetElementPtrOfLocalConstArray(getElementPtr);
    }

    private void removeGetElementPtrOfGlobalConstArray(GetElementPtr getElementPtr) {
        Value pointer = getElementPtr.getUsedValue(0);
        Value offset = getElementPtr.getUsedValue(1);

        if (pointer instanceof GlobalVariable globalVariable && globalVariable.isConstant()) {
            if (offset instanceof Constant constant) {
                Constant newConstant = new Constant((ScalarValueType) getElementPtr.getValueType(), globalVariable.getInitialValue(Integer.parseInt(constant.getName())));
                Iterator<User> iterator = getElementPtr.getUsers().iterator();
                boolean allUserIsLoad = true;
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    if (user instanceof Load load) {
                        iterator.remove();
                        user.updateAllUsers(newConstant);
                        load.removeAllUse();
                        load.getFatherBasicBlock().removeInstruction(load);
                    } else {
                        allUserIsLoad = false;
                    }
                }
                if (allUserIsLoad) {
                    getElementPtr.removeAllUse();
                    currentBasicBlock.removeInstruction(getElementPtr);
                    hasChanged = true;
                }
            }
        }
    }

    private void removeGetElementPtrOfLocalConstArray(GetElementPtr getElementPtr) {
        Value pointer = getElementPtr.getUsedValue(0);
        Value offset = getElementPtr.getUsedValue(1);
        if (pointer instanceof GetElementPtr getElementPtr1) {
            Value addr = getElementPtr1.getUsedValue(0);
            if (addr instanceof Alloca alloca && alloca.isConstant() && offset instanceof Constant constant) {
                Constant newConstant = new Constant((ScalarValueType) getElementPtr.getValueType(), alloca.getInitialValue(Integer.parseInt(constant.getName())));
                Iterator<User> iterator = getElementPtr.getUsers().iterator();
                boolean allUserIsLoad = true;
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    if (user instanceof Load load) {
                        iterator.remove();
                        user.updateAllUsers(newConstant);
                        load.removeAllUse();
                        load.getFatherBasicBlock().removeInstruction(load);
                    } else {
                        allUserIsLoad = false;
                    }
                }
                if (allUserIsLoad) {
                    getElementPtr.removeAllUse();
                    currentBasicBlock.removeInstruction(getElementPtr);
                    hasChanged = true;
                }
            }
        }
    }
}
