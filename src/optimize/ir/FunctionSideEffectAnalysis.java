package optimize.ir;

import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Parameter;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.io.IOInstruction;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.memory.Store;
import frontend.ir.llvm.value.instruction.other.Call;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionSideEffectAnalysis {
    public final static FunctionSideEffectAnalysis FUNCTION_SIDE_EFFECT_ANALYSIS = new FunctionSideEffectAnalysis();

    private Module module;
    private HashMap<Function, ArrayList<Function>> functionCalls;

    private FunctionSideEffectAnalysis() {
    }

    public void analyze(Module module) {
        Tools.printStartMessage("方程副作用分析");

        this.module = module;
        analyze();

        Tools.printEndMessage("方程副作用分析");
    }

    public void analyze() {
        functionCalls = new HashMap<>();

        for (Function function : module.getFunctions()) {
            analyze(function);
        }

        boolean hasChange;
        do {
            hasChange = false;
            for (Function function : module.getFunctions()) {
                for (Function call : functionCalls.get(function)) {
                    //if a function has side effect, then the caller of this function has side effect
                    if (call.hasSideEffect() && !function.hasSideEffect()) {
                        function.setHasSideEffect(true);
                        hasChange = true;
                        break;
                    }
                }
            }
        } while (hasChange);
    }

    public void analyze(Function function) {
        boolean hasSideEffect = false;
        functionCalls.put(function, new ArrayList<>());

        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            if(hasSideEffect){
                break;
            }

            for (Instruction instruction : basicBlock.getInstructions()) {
                if (instruction instanceof Call call) {
                    Function target = call.getFunction();
                    functionCalls.get(function).add(target);
                }

                //io instruction must has side effect
                if (instruction instanceof IOInstruction) {
                    hasSideEffect = true;
                }

                if (instruction instanceof Store) {
                    Value pointer = instruction.getUsedValue(1);

                    //alters the value of a global variable
                    if (pointer instanceof GlobalVariable) {
                        hasSideEffect = true;
                    }

                    //alters the value of an element of an array (parameter or global variable)
                    if (pointer instanceof GetElementPtr getElementPtr) {
                        if (getElementPtr.getUsedValue(0) instanceof Parameter || getElementPtr.getUsedValue(0) instanceof GlobalVariable) {
                            hasSideEffect = true;
                        }
                    }
                }
            }
            if (hasSideEffect) {
                break;
            }
        }

        function.setHasSideEffect(hasSideEffect);
    }
}
