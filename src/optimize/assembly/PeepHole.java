package optimize.assembly;

import backend.AssemblyRecord;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.Label;
import backend.assembly.instruction.JumpInstruction;
import backend.assembly.instruction.MemoryInstruction;
import backend.assembly.instruction.MoveInstruction;
import config.Configuration;
import utils.Tools;

import java.util.ArrayList;
import java.util.Objects;

public class PeepHole {
    public final static PeepHole PEEP_HOLE = new PeepHole();

    private AssemblyRecord record;

    private PeepHole() {
    }

    public void optimize(AssemblyRecord record) {
        this.record = record;

        Tools.printOptimizeInfo("窥孔优化", Configuration.PEEP_HOLE_OPTIMIZATION);

        optimize();
    }

    private void optimize() {
        removeRedundantJumpInstruction();
        removeRedundantMemoryInstruction();
        removeRedundantMove();
        removeRedundantSw();
    }

    private void removeRedundantJumpInstruction() {
        // j label -> delete
        // label:
        ArrayList<Assembly> textSegment = record.getTextSegment();
        for (int i = 0; i < textSegment.size() - 1; i++) {
            Assembly assembly1 = textSegment.get(i);
            Assembly assembly2 = textSegment.get(i + 1);
            if (assembly1 instanceof JumpInstruction jumpInstruction && jumpInstruction.getOperator().equals("jal") && assembly2 instanceof Label label
                    && jumpInstruction.getTarget() != null && Objects.equals(jumpInstruction.getTarget(), label.getLabel())) {
                record.removeFromText(assembly1);
            }
        }
    }

    private void removeRedundantMemoryInstruction() {
        // sw $t0, 0($t1)
        // lw $t2, 0($t1) -> move $t2, $t0
        ArrayList<Assembly> textSegment = record.getTextSegment();
        for (int i = 0; i < textSegment.size() - 1; i++) {
            Assembly assembly1 = textSegment.get(i);
            Assembly assembly2 = textSegment.get(i + 1);
            if (assembly1 instanceof MemoryInstruction memoryInstruction1 && Objects.equals(memoryInstruction1.getOperator(), "sw")
                    && assembly2 instanceof MemoryInstruction memoryInstruction2 && Objects.equals(memoryInstruction2.getOperator(), "lw")
                    && memoryInstruction1.getBase() == memoryInstruction2.getBase() && memoryInstruction1.getBase() != null
                    && Objects.equals(memoryInstruction1.getOffset(), memoryInstruction2.getOffset())) {
                MoveInstruction moveInstruction = new MoveInstruction(memoryInstruction2.getRt(), memoryInstruction1.getRt());
                record.replaceInText(assembly2, moveInstruction);
            }
        }
    }

    private void removeRedundantMove() {
        // move $t0, $t0 -> delete
        ArrayList<Assembly> textSegment = record.getTextSegment();
        for (Assembly assembly : textSegment) {
            if (assembly instanceof MoveInstruction moveInstruction && moveInstruction.getRs() == moveInstruction.getRd()) {
                record.removeFromText(assembly);
            }
        }

        boolean hasChanged;

        do {
            hasChanged = false;

            // move $t0, $t1
            // move $t1, $t0 -> delete
            textSegment = record.getTextSegment();
            for (int i = 0; i < textSegment.size() - 1; i++) {
                Assembly assembly1 = textSegment.get(i);
                Assembly assembly2 = textSegment.get(i + 1);
                if (assembly1 instanceof MoveInstruction moveInstruction1 && assembly2 instanceof MoveInstruction moveInstruction2) {
                    if (moveInstruction1.getRs() == moveInstruction2.getRd() && moveInstruction2.getRs() == moveInstruction1.getRd()) {
                        record.removeFromText(assembly2);
                        hasChanged = true;
                    }
                }
            }

            // move $t0, $t1 -> delete
            // move $t0, $t2
            textSegment = record.getTextSegment();
            for (int i = 0; i < textSegment.size() - 1; i++) {
                Assembly assembly1 = textSegment.get(i);
                Assembly assembly2 = textSegment.get(i + 1);
                if (assembly1 instanceof MoveInstruction moveInstruction1 && assembly2 instanceof MoveInstruction moveInstruction2) {
                    if (moveInstruction1.getRd() == moveInstruction2.getRd()) {
                        record.removeFromText(assembly1);
                        hasChanged = true;
                    }
                }
            }
        } while (hasChanged);
    }

    private void removeRedundantSw() {
        ArrayList<Assembly> textSegment = new ArrayList<>(record.getTextSegment());
        for (Assembly assembly : textSegment) {
            if (assembly instanceof JumpInstruction jumpInstruction && jumpInstruction.getOperator().equals("jal")) {
                for (MemoryInstruction lw : jumpInstruction.getLwInstructions()) {
                    if (!textSegment.contains(lw)) {
                        Register rt = lw.getRt();
                        MemoryInstruction redundantSw = null;
                        for (MemoryInstruction sw : jumpInstruction.getSwInstructions()) {
                            if (sw.getRt() == rt) {
                                redundantSw = sw;
                                break;
                            }
                        }
                        if (redundantSw != null) {
                            record.removeFromText(redundantSw);
                        }
                    }
                }
            }
        }
    }
}
