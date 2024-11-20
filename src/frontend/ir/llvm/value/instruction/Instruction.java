package frontend.ir.llvm.value.instruction;

import backend.AssemblyBuilder;
import backend.assembly.Comment;
import frontend.ir.llvm.value.User;
import frontend.ir.llvm.value.type.ValueType;

public class Instruction extends User {
    public Instruction(ValueType valueType, String name) {
        super(valueType, name);
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
