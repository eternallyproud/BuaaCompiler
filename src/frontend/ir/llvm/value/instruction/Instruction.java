package frontend.ir.llvm.value.instruction;

import backend.AssemblyBuilder;
import backend.assembly.Comment;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Use;
import frontend.ir.llvm.value.User;
import frontend.ir.llvm.value.instruction.io.IOInstruction;
import frontend.ir.llvm.value.instruction.other.Call;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;

public class Instruction extends User {
    private BasicBlock fatherBasicBlock;
    private int score = 0;

    public Instruction(ValueType valueType, String name) {
        super(valueType, name);
    }

    public void setFatherBasicBlock(BasicBlock fatherBasicBlock) {
        this.fatherBasicBlock = fatherBasicBlock;
    }

    public BasicBlock getFatherBasicBlock() {
        return fatherBasicBlock;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void removeAllUse(){
        for(Use use : userList){
            use.getUser().removeUsedValue(this);
        }
        for(Use use : usedList){
            use.getUsed().removeUser(this);
        }
    }

    public boolean disposable(){
        //call and io instruction is not disposable
        return usable() && !(this instanceof Call) && !(this instanceof IOInstruction);
    }

    public boolean usable(){
        return valueType != ScalarValueType.VOID;
    }

    public String hash(){
        return "";
    }

    @Override
    public void buildAssembly() {
        //comment
        Comment comment = new Comment(this.toString().substring(1));
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(comment);
    }

    @Override
    public String toString() {
        return "\t";
    }
}
