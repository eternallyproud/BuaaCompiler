package frontend.ir;

import config.Configuration;
import error.ErrorHandler;
import frontend.ir.llvm.value.*;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.global.StringLiteral;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.terminator.Ret;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.parser.node.CompUnitNode;
import utils.InOut;
import utils.Tools;

import java.util.HashMap;

public class IRBuilder {
    public static final IRBuilder IR_BUILDER = new IRBuilder();
    private CompUnitNode compUnitNode;
    private int globalVarCount = 0;
    private int stringLiteralCount = 0;
    private int basicBlockCount = 0;
    private int ParameterCount = 0;
    private final HashMap<Function, Integer> localVarCountMap = new HashMap<>();
    private Module module;
    private Function currentFunction;
    private BasicBlock currentBasicBlock;

    public void init(CompUnitNode compUnitNode) {
        this.compUnitNode = compUnitNode;
        this.module = new Module();
    }

    public void buildIR() {
        if (!ErrorHandler.ERROR_HANDLER.isEmpty()) {
            Tools.printFailMessage("中间代码生成");
        } else {
            Tools.printStartMessage("中间代码生成");
            module.initModule();
            compUnitNode.buildIR();
            Tools.printEndMessage("中间代码生成");
        }
    }

    public String getGlobalVarName() {
        return Configuration.GLOBAL_VAR_IR_PREFIX + globalVarCount++;
    }

    public String getStringLiteralName() {
        return Configuration.STRING_LITERAL_IR_PREFIX + stringLiteralCount++;
    }

    public String getFunctionName(String functionName) {
        return Configuration.FUNCTION_IR_PREFIX + functionName;
    }

    public String getFunctionName() {
        return "@main";
    }

    public String getBasicBlockName() {
        return Configuration.BASIC_BLOCK_IR_PREFIX + basicBlockCount++;
    }

    public String getParameterName() {
        return Configuration.PARAMETER_IR_PREFIX + ParameterCount++;
    }

    public String getLocalVarName() {
        localVarCountMap.put(currentFunction, localVarCountMap.getOrDefault(currentFunction, 0) + 1);
        return Configuration.LOCAL_VAR_IR_PREFIX + (localVarCountMap.get(currentFunction) - 1);
    }

    public String getLocalVarName(Function function) {
        localVarCountMap.put(function, localVarCountMap.get(function) + 1);
        return Configuration.LOCAL_VAR_IR_PREFIX + (localVarCountMap.get(function) - 1);
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        module.addGlobalVariable(globalVariable);
    }

    public void addStringLiteral(StringLiteral stringLiteral) {
        module.addStringLiteral(stringLiteral);
    }

    public void addFunction(Function function) {
        module.addFunction(function);
        currentFunction = function;
        ParameterCount = 0;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        currentFunction.addBasicBlock(basicBlock);
        basicBlock.setFatherFunction(currentFunction);
    }

    public void addParameter(Parameter parameter) {
        currentFunction.addParameter(parameter);
    }

    public void addInstruction(Instruction instruction) {
        currentBasicBlock.addInstruction(instruction);
        instruction.setFatherBasicBlock(currentBasicBlock);
    }

    public void addInstructionToBasicBlock(Instruction instruction, BasicBlock basicBlock) {
        basicBlock.addInstruction(instruction);
        instruction.setFatherBasicBlock(basicBlock);
    }

    public Function getCurrentFunction() {
        return currentFunction;
    }

    public void setCurrentBasicBlock(BasicBlock basicBlock) {
        currentBasicBlock = basicBlock;
    }

    public void ensureRetExist() {
        if (currentBasicBlock.isEmpty() || !(currentBasicBlock.getLastInstruction() instanceof Ret)) {
            if (currentFunction.getValueType() == ScalarValueType.VOID) {
                Ret ret = new Ret(getLocalVarName(), null);
                addInstruction(ret);
            } else {
                Tools.printMessage("缺少 return 语句");
            }
        }
    }

    public void markRetIsMain() {
        if (!currentBasicBlock.isEmpty() && currentBasicBlock.getLastInstruction() instanceof Ret ret) {
            ret.markAsMain();
        }
    }

    public void writeIR() {
        InOut.writeIRResult(module.toString());
    }

    public Module getModule() {
        return module;
    }
}
