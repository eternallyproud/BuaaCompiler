package optimize.ir;

import config.Configuration;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Module;
import frontend.ir.llvm.value.global.Function;
import frontend.ir.llvm.value.instruction.BinaryOperation;
import frontend.ir.llvm.value.instruction.ConversionOperation;
import frontend.ir.llvm.value.instruction.Instruction;
import frontend.ir.llvm.value.instruction.memory.GetElementPtr;
import frontend.ir.llvm.value.instruction.other.Call;
import frontend.ir.llvm.value.instruction.other.ICmp;
import utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GlobalVariableNumbering {
    public final static GlobalVariableNumbering GLOBAL_VARIABLE_NUMBERING = new GlobalVariableNumbering();
    private Module module;

    private GlobalVariableNumbering() {
    }

    public void init(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (Configuration.GLOBAL_VARIABLE_NUMBERING_OPTIMIZATION) {
            Tools.printOpenInfo("全局变量编号优化");

            for (Function function : module.getFunctions()) {
                optimize(function);
            }
        } else {
            Tools.printCloseInfo("全局变量编号优化");
        }
    }

    private void optimize(Function function) {
        HashMap<String, Instruction> hashMap = new HashMap<>();

        doNumbering(hashMap, function.getBasicBlocks().get(0));
    }

    private void doNumbering(HashMap<String, Instruction> hashMap, BasicBlock root) {
        ArrayList<Instruction> temp = new ArrayList<>();

        Iterator<Instruction> iterator = root.getInstructions().iterator();
        while (iterator.hasNext()) {
            Instruction instruction = iterator.next();

            if (instruction instanceof BinaryOperation || instruction instanceof GetElementPtr ||
                    instruction instanceof ICmp || instruction instanceof ConversionOperation ||
                    instruction instanceof Call call && call.getFunction().canBeNumbered()) {
                String hash = instruction.hash();
                if (hashMap.containsKey(hash)) {
                    instruction.updateAllUsers(hashMap.get(hash));
                    instruction.removeAllUse();
                    iterator.remove();
                } else {
                    hashMap.put(hash, instruction);
                    temp.add(instruction);
                }
            }
        }

        for (BasicBlock sonBasicBlock : root.getSon()) {
            doNumbering(hashMap, sonBasicBlock);
        }

        for (Instruction instruction : temp) {
            hashMap.remove(instruction.hash());
        }
    }
}
