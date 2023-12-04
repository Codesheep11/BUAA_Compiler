package backend;


import backend.Component.MipsBlock;
import backend.Component.MipsFunction;
import backend.Component.Program;
import backend.Instructions.Instr;
import backend.Instructions.InstrB;
import backend.Instructions.InstrJ;
import backend.Operand.VirReg;
import midend.ir.values.BasicBlock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static backend.Codegen.b2mb;

public class LiveAnalyse {
    public LiveAnalyse(Program program) {
        for (MipsFunction func : program.getFunctions()) {
            runBlockAnalysis(func);
            runInstrAnalysis(func);
            buildCG(func);
        }
    }

    private void runBlockAnalysis(MipsFunction func) {
        //获得所有bb的use，def set
        for (MipsBlock mb : func.getBbs()) {
            for (Instr ins : mb.getInstrs()) {
                if (ins.defRegs.size() != 0) {
                    mb.getDefVirRegs().addAll(ins.defRegs);
                }
                mb.getUseVirRegs().addAll(ins.useRegs);
            }
        }

        // 迭代计算 in, out

        boolean changed = true;
        while (changed) {
            changed = false;
            int nums = func.getBbs().size();
            for (int i = nums - 1; i >= 0; i--) {
                MipsBlock mb = func.getBbs().get(i);

                // out = ∪ (in of nxt)
                if (mb.getBb().getNxtBBs().size() != 0) {
                    BasicBlock bb = mb.getBb();
                    for (BasicBlock nb : bb.getNxtBBs()) {
                        MipsBlock nmb = b2mb.get(nb);
                        mb.getOutVirRegs().addAll(nmb.getInVirRegs());
                    }
                }

                // in = use ∪ (out - def)
                Set<VirReg> inAns = new HashSet<>(mb.getOutVirRegs());
                inAns.retainAll(mb.getDefVirRegs());
                inAns.addAll(mb.getUseVirRegs());
                if (!inAns.equals(mb.getInVirRegs())) {
                    changed = true;
                    mb.setInVirRegs(inAns);
                }
            }
        }
//        for (MipsBlock mb : func.getBbs()) {
//            System.out.println(mb.getBb().getName());
//            System.out.println("def:" + mb.getDefVirRegs());
//            System.out.println("Use:" + mb.getUseVirRegs());
//            System.out.println("in:" + mb.getInVirRegs());
//            System.out.println("out:" + mb.getOutVirRegs());
//            System.out.println();
//        }
    }

    private void runInstrAnalysis(MipsFunction func) {
        // 初始化 in, out
//        for (MipsBlock mb : func.getBbs()) {
//            mb.getFirstIns().inRegs.addAll();
//            mb.getFirstIns().inRegs.addAll();
//        }
        int cntBB = func.getBbs().size();
        for (int i = 0; i < cntBB; i++) {
            MipsBlock mb = func.getBbs().get(i);
            int cntIns = mb.getInstrs().size();
            for (int j = 0; j < cntIns; j++) {
                Instr ins = mb.getInstrs().get(j);
                if (ins instanceof InstrB) {
                    Instr tIns = null;
                    String bName = ((InstrB) ins).getLabel();
                    for (MipsBlock tb : func.getBbs()) {
                        if (bName.equals(tb.getBb().getName())) {
                            tIns = tb.getFirstIns();
                            break;
                        }
                    }
                    ins.nxtInstr.add(tIns);
                    tIns.preInstr.add(ins);
                }
                else if (ins instanceof InstrJ) {
                    if (((InstrJ) ins).getjType() == InstrJ.JType.j) {
                        Instr tIns = null;
                        String bName = ((InstrJ) ins).getTarget();
                        for (MipsBlock tb : func.getBbs()) {
                            if (bName.equals(tb.getBb().getName())) {
                                tIns = tb.getFirstIns();
                                break;
                            }
                        }
                        ins.nxtInstr.add(tIns);
                        tIns.preInstr.add(ins);
                    }
                }
                if (j < cntIns - 1) {
                    Instr nxt = mb.getInstrs().get(j + 1);
                    ins.nxtInstr.add(nxt);
                    nxt.preInstr.add(ins);
                }
            }
        }
        // 迭代计算 in, out
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = cntBB - 1; i >= 0; i--) {
                MipsBlock mb = func.getBbs().get(i);
                int cntIns = mb.getInstrs().size();
                for (int j = cntIns - 1; j >= 0; j--) {
                    // out = ∪ (in of nxt)
                    Instr ins = mb.getInstrs().get(j);
                    for (Instr nIns : ins.nxtInstr) {
                        ins.outRegs.addAll(nIns.inRegs);
                    }

                    // in = use ∪ (out - def)
                    Set<VirReg> inAns = new HashSet<>(ins.outRegs);
                    inAns.retainAll(ins.defRegs);
                    inAns.addAll(ins.useRegs);
                    if (!inAns.equals(ins.inRegs)) {
                        changed = true;
                        ins.inRegs = inAns;
                    }
                }
            }
        }
//        for (int i = 0; i < cntBB; i++) {
//            MipsBlock mb = func.getBbs().get(i);
//            int cntIns = mb.getInstrs().size();
//            for (int j = 0; j < cntIns; j++) {
//                Instr ins = mb.getInstrs().get(j);
//                System.out.println(ins);
//                System.out.println("def:" + ins.defRegs);
//                System.out.println("use:" + ins.useRegs);
//                System.out.println("in:" + ins.inRegs);
//                System.out.println("out:" + ins.outRegs);
//                System.out.println();
//            }
//        }
    }

    private HashMap<VirReg, HashSet<VirReg>> buildCG(MipsFunction func) {
        HashMap<VirReg, HashSet<VirReg>> graph = new HashMap<>();
        int cntBB = func.getBbs().size();
        for (int i = 0; i < cntBB; i++) {
            MipsBlock mb = func.getBbs().get(i);
            int cntIns = mb.getInstrs().size();
            for (int j = 0; j < cntIns; j++) {
                Instr ins = mb.getInstrs().get(j);
                for (VirReg o : ins.outRegs) {
                    if (!graph.containsKey(o)) {
                        graph.put(o, new HashSet<>());
                    }
                }
                for (VirReg d : ins.defRegs) {
                    if (!graph.containsKey(d)) {
                        graph.put(d, new HashSet<>());
                    }
                    for (VirReg o : ins.outRegs) {
                        graph.get(d).add(o);
                        graph.get(o).add(d);
                    }
                }
                for (VirReg o1 : ins.outRegs) {
                    for (VirReg o2 : ins.outRegs) {
                        graph.get(o1).add(o2);
                        graph.get(o2).add(o1);
                    }
                }
            }
        }
//        System.out.println(graph);
        return graph;

    }

}
