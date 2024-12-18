package optimize.ir;

import config.Configuration;
import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.global.GlobalVariable;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.memory.Load;
import frontend.ir.llvm.value.instruction.memory.Store;
import frontend.ir.llvm.value.instruction.other.Call;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryInstructionSimplify {
    public final static MemoryInstructionSimplify MEMORY_INSTRUCTION_SIMPLIFY = new MemoryInstructionSimplify();

    private Module module;
    private boolean hasChanged;

    private MemoryInstructionSimplify() {
    }

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("内存指令化简优化", Configuration.MEMORY_INSTRUCTION_SIMPLIFY_OPTIMIZATION);

        optimize();
    }

    public boolean optimize() {
        hasChanged = false;

        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                removeRedundantLoad(basicBlock);
                localizeGlobalVar(basicBlock);
            }
        }

        return hasChanged;
    }

    private void localizeGlobalVar(BasicBlock basicBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
        HashMap<GlobalVariable, Value> globalVariableMap = new HashMap<>();
        HashMap<GlobalVariable, Value> globalVariableUpdateMap = new HashMap<>();

        for (Instruction instruction : instructions) {
            if (instruction instanceof Load load) {
                Value pointer = load.getUsedValue(0);
                if (pointer instanceof GlobalVariable globalVariable) {
                    if (globalVariableMap.containsKey(globalVariable)) {
                        //already loaded
                        load.updateAllUsers(globalVariableMap.get(globalVariable));
                        load.removeAllUse();
                        basicBlock.removeInstruction(load);
                        hasChanged = true;
                    } else {
                        globalVariableMap.put(globalVariable, load);
                    }
                }
            } else if (instruction instanceof Store store) {
                Value value = store.getUsedValue(0);
                Value pointer = store.getUsedValue(1);
                if (pointer instanceof GlobalVariable globalVariable) {
                    globalVariableMap.put(globalVariable, value);
                    globalVariableUpdateMap.put(globalVariable, value);
                    store.removeAllUse();
                    basicBlock.removeInstruction(store);
                    //this can't be marked as changed
                }
            } else if (instruction instanceof Call call) {
                //global variable maybe changed in call, so we need to update all
                globalVariableMap.clear();
                globalVariableUpdateMap.forEach((globalVariable, value) -> {
                    Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(basicBlock.getFatherFunction()), value, globalVariable);
                    store.setFatherBasicBlock(basicBlock);
                    basicBlock.addBefore(call, store);
                });
                globalVariableUpdateMap.clear();
            }
        }
        //this is the end of this basic block, we need to update all
        globalVariableUpdateMap.forEach((globalVariable, value) -> {
            Store store = new Store(IRBuilder.IR_BUILDER.getLocalVarName(basicBlock.getFatherFunction()), value, globalVariable);
            store.setFatherBasicBlock(basicBlock);
            basicBlock.addBeforeLast(store);
        });
    }

    private void removeRedundantLoad(BasicBlock basicBlock) {
        ArrayList<Instruction> instructions = new ArrayList<>(basicBlock.getInstructions());
        HashMap<Value, Value> pointerValueMap = new HashMap<>();
        for (Instruction instruction : instructions) {
            if (instruction instanceof Load load) {
                Value pointer = load.getUsedValue(0);
                if (!pointerValueMap.containsKey(pointer)) {
                    pointerValueMap.put(pointer, load);
                } else {
                    load.updateAllUsers(pointerValueMap.get(pointer));
                    load.removeAllUse();
                    basicBlock.removeInstruction(load);
                    hasChanged = true;
                }
            } else if (instruction instanceof Store store) {
                //refresh pointer-value map
                pointerValueMap.clear();
                pointerValueMap.put(store.getUsedValue(1), store.getUsedValue(0));
            } else if (instruction instanceof Call) {
                //refresh pointer-value map
                pointerValueMap.clear();
            }
        }
    }
}
