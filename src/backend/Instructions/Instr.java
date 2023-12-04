package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Component.MipsFunction;
import backend.Operand.VirReg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class Instr {

    public MipsBlock mb;
    public Set<VirReg> inRegs = new HashSet<>();
    public Set<VirReg> outRegs = new HashSet<>();
    public Set<VirReg> defRegs = new HashSet<>();
    public Set<VirReg> useRegs = new HashSet<>();

    public HashSet<Instr> nxtInstr = new HashSet<>();
    public HashSet<Instr> preInstr = new HashSet<>();

}
