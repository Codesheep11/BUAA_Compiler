package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class Syscall extends Instr {
    public Syscall(MipsBlock mb) {
        this.mb = mb;
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
