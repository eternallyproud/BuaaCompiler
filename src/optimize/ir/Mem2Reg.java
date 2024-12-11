package optimize.ir;

import config.Configuration;
import frontend.ir.IRBuilder;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.User;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.optimize.Phi;
import frontend.ir.llvm.value.instruction.memory.Alloca;
import frontend.ir.llvm.value.instruction.memory.Load;
import frontend.ir.llvm.value.instruction.memory.Store;
import frontend.ir.llvm.value.type.ScalarValueType;
import utils.Tools;

import java.util.ArrayList;

public class Mem2Reg {
    public final static Mem2Reg MEM2REG = new Mem2Reg();
    private Module module;
    private ArrayList<BasicBlock> defBasicBlocks;
    private ArrayList<Instruction> defInstructions;
    private ArrayList<Instruction> useInstructions;

    public void optimize(Module module) {
        this.module = module;

        Tools.printOptimizeInfo("Mem2Reg优化", Configuration.MEM2REG_OPTIMIZATION);

        optimize();
    }

    public void optimize() {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                //if not newed, there might be ConcurrentModificationException exceptions
                for (Instruction instruction : new ArrayList<>(basicBlock.getInstructions())) {
                    if (instruction instanceof Alloca alloca && instruction.getValueType().getPointerReferenceValueType() instanceof ScalarValueType) {
                        init(alloca);
                        insertPhi(alloca);
                        rename(new ArrayList<>(), function.getBasicBlocks().get(0), alloca);
                    }
                }
            }
        }
    }

    private void init(Instruction instruction) {
        defBasicBlocks = new ArrayList<>();
        defInstructions = new ArrayList<>();
        useInstructions = new ArrayList<>();

        for (User user : instruction.getUsers()) {
            if (user instanceof Store store) {
                defInstructions.add(store);
                if (!defBasicBlocks.contains(store.getFatherBasicBlock())) {
                    defBasicBlocks.add(store.getFatherBasicBlock());
                }
            } else if (user instanceof Load load) {
                useInstructions.add(load);
            }
        }
    }

    private void insertPhi(Alloca alloca) {
        //basic blocks with phi inserted
        ArrayList<BasicBlock> insertedBasicBlocks = new ArrayList<>();

        //unhandled basic blocks
        ArrayList<BasicBlock> unhandledBasicBlocks = new ArrayList<>(defBasicBlocks);

        while (!unhandledBasicBlocks.isEmpty()) {
            BasicBlock basicBlock = unhandledBasicBlocks.remove(0);
            for (BasicBlock dominanceFrontierBasicBlock : basicBlock.getDominanceFrontier()) {
                if (!insertedBasicBlocks.contains(dominanceFrontierBasicBlock)) {

                    //phi
                    String phiName = IRBuilder.IR_BUILDER.getLocalVarName(dominanceFrontierBasicBlock.getFatherFunction());
                    Phi phi = new Phi(alloca.getValueType().getPointerReferenceValueType(), phiName);
                    phi.setFatherBasicBlock(dominanceFrontierBasicBlock);

                    //add phi to the head of dominance frontier basic block
                    dominanceFrontierBasicBlock.addPhi(phi);

                    //phi is both the user and the definer of alloc
                    defInstructions.add(phi);
                    useInstructions.add(phi);

                    //finished inserting phi
                    insertedBasicBlocks.add(dominanceFrontierBasicBlock);

                    //this dominance frontier basic block may still need to be handled
                    if (!defBasicBlocks.contains(dominanceFrontierBasicBlock)) {
                        unhandledBasicBlocks.add(dominanceFrontierBasicBlock);
                    }
                }
            }
        }
    }

    private void rename(ArrayList<Value> savedValues, BasicBlock root, Alloca alloca) {
        int saveCount = 0;

        ArrayList<Instruction> instructions = new ArrayList<>(root.getInstructions());
        for (Instruction instruction : instructions) {
            //load: update use and remove
            if (instruction instanceof Load load && useInstructions.contains(load)) {
                Value newUsed = savedValues.isEmpty() ? new Constant.Undefined((ScalarValueType) load.getValueType()) : savedValues.get(savedValues.size() - 1);
                instruction.updateAllUsers(newUsed);
                root.removeInstruction(instruction);
                instruction.removeAllUse();
            }
            //store: save and remove
            else if (instruction instanceof Store store && defInstructions.contains(store)) {
                savedValues.add(store.getUsedValue(0));
                saveCount++;
                root.removeInstruction(instruction);
                instruction.removeAllUse();
            }
            //phi: save
            else if (instruction instanceof Phi phi && defInstructions.contains(phi)) {
                savedValues.add(phi);
                saveCount++;
            }
            //alloca: remove
            else if (instruction.equals(alloca)) {
                root.removeInstruction(instruction);
                instruction.removeAllUse();
            }
        }

        //update phi
        for (BasicBlock successor : root.getSuccessors()) {
            //if not saved, there might be exceptions
            if (successor.getInstructions().get(0) instanceof Phi phi && defInstructions.contains(phi)) {
                if (savedValues.isEmpty()) {
                    phi.addUsedValueSet(new Constant.Undefined((ScalarValueType) phi.getValueType()), root);
                    continue;
                }
                phi.addUsedValueSet(savedValues.get(savedValues.size() - 1), root);
            }
        }

        //dfs
        for (BasicBlock basicBlock : root.getSon()) {
            rename(savedValues, basicBlock, alloca);
        }

        for (int i = 0; i < saveCount; i++) {
            savedValues.remove(savedValues.size() - 1);
        }
    }
}
