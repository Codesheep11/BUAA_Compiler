package backend;

import backend.Component.MipsBlock;
import backend.Component.MipsFunction;
import backend.Component.Program;
import backend.Component.StackFp;
import backend.Instructions.*;
import backend.Operand.Imm;
import backend.Operand.PhyReg;
import backend.Operand.VirReg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static backend.Codegen.f2mf;

public class Allocate {
    private Program program;

    private HashMap<VirReg, HashSet<VirReg>> curCG;
    private HashMap<VirReg, HashSet<VirReg>> CG;
    private ArrayList<VirReg> outNodes = new ArrayList<>();
    private ArrayList<VirReg> spillNodes = new ArrayList<>();

    private HashSet<VirReg> moveNodes = new HashSet<>();
    private HashSet<Move> moves = new HashSet<>();
    private ArrayList<Move> deletes = new ArrayList<>();//待删除的moves
    private HashMap<VirReg, HashSet<VirReg>> merges = new HashMap<>();
    private HashSet<VirReg> mergeNodes = new HashSet<>();

    private final HashSet<PhyReg> Regs;

    private int K = 21;
    // $5 - $25

    public Allocate(Program program) {
        this.program = program;
        Regs = new HashSet<>();
        for (int i = 0; i < K; i++) {
            Regs.add(new PhyReg(i + 5));
        }
    }


    public Program allocate() {
        for (MipsFunction f : program.getFunctions()) {
            AllocateFunc(f);
        }
        // 处理 函数调用 和 调用函数寄存器保护
        genCalls();
        genRets();
        return program;
    }

    private void AllocateFunc(MipsFunction func) {
        // 得到以虚拟寄存器为节点的冲突图

        BuildCG(func);

        boolean fail = true;
        while (fail) {
            while (!curCG.isEmpty()) {
                Simplify();
                Spill();
            }
            if (Select()) {
                fail = false;
            }
            else {
//                System.out.println("round");
                ReWrite(func);
                BuildCG(func);
                fail = true;
            }
        }
        for (Move move : deletes) {
            move.mb.getInstrs().remove(move);
        }
    }


    private void Simplify() {
        boolean delete = true;
        while (delete) {
            delete = false;
            //每次删除一个 低度数 、和传送无关 的节点
            for (VirReg node : curCG.keySet()) {
                if (curCG.get(node).size() < K && !moveNodes.contains(node)) {
                    delete = true;
                    DeleteNode(node);
                    outNodes.add(node);
                    break;
                }
            }
            //尝试对一个move指令进行合并
            if (Coalesce()) {
                //如果合并成功，下一轮继续
                delete = true;
            }
            //如果简化和合并都无法进行，冻结一个 move
            if (!delete) {
                Freeze();
            }
        }
    }

    private boolean Coalesce() {
        for (Move m : moves) {
            VirReg rs = (VirReg) m.getRs();
            VirReg rd = (VirReg) m.getRd();
            VirReg v1 = rs, v2 = rd;
            if (mergeNodes.contains(rs)) {
                if (!merges.containsKey(v1)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rs)) {
                            v1 = key;
                            break;
                        }
                    }
                }
            }
            if (mergeNodes.contains(rd)) {
                if (!merges.containsKey(v2)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rd)) {
                            v2 = key;
                            break;
                        }
                    }
                }
            }
            //v1,v2已转换
            //满足合并条件
            if (George(v1, v2)) {
                moves.remove(m);
                deletes.add(m);
                if (v1 != v2)
                    MergeNodes(v1, v2);
                return true;
            }
        }
        return false;
    }

    public boolean George(VirReg v1, VirReg v2) {
        if (!curCG.containsKey(v1)) {
            System.err.println();
        }
        if (curCG.get(v1).contains(v2)) {
            return false;
        }
        for (VirReg t : curCG.get(v1)) {
//            if(curCG.get(t) == null){
//                System.err.println();
//            }
            if (!(curCG.get(t).contains(v2) || curCG.get(t).size() < K)) {
                return false;
            }
        }
        return true;
    }

    public void Freeze() {
        int min = K;
        VirReg v = null;
        for (VirReg n : moveNodes) {
            if (curCG.get(n).size() < min) {
                v = n;
                min = curCG.get(n).size();
            }
        }
        HashSet<Move> fms = new HashSet<>();
        for (Move m : moves) {
            VirReg rs = (VirReg) m.getRs();
            VirReg rd = (VirReg) m.getRd();
            if (mergeNodes.contains(rs)) {
                if (!merges.containsKey(rs)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rs)) {
                            rs = key;
                            break;
                        }
                    }
                }
            }
            if (mergeNodes.contains(rd)) {
                if (!merges.containsKey(rd)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rd)) {
                            rd = key;
                            break;
                        }
                    }
                }
            }
            if (v == rs || v == rd) {
                fms.add(m);
            }
        }
        for (Move m : fms) {
            moves.remove(m);
        }

        moveNodes = new HashSet<>();
        for (Move m : moves) {
            VirReg rs = (VirReg) m.getRs();
            VirReg rd = (VirReg) m.getRd();
            if (mergeNodes.contains(rs)) {
                if (!merges.containsKey(rs)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rs)) {
                            rs = key;
                            break;
                        }
                    }
                }
            }
            if (mergeNodes.contains(rd)) {
                if (!merges.containsKey(rd)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rd)) {
                            rd = key;
                            break;
                        }
                    }
                }
            }
            moveNodes.add(rs);
            moveNodes.add(rd);
        }
    }

    //此时图中全为度>=K的节点
    public void Spill() {
        if (!curCG.isEmpty()) {
            for (VirReg node : curCG.keySet()) {
                DeleteNode(node);
                outNodes.add(node);
                spillNodes.add(node);
                break;
            }
        }
    }

    private void MergeNodes(VirReg v1, VirReg v2) {
        //本质是在冲突图中删除v2,保留v1
        //合并记录v1,v2
        mergeNodes.add(v1);
        mergeNodes.add(v2);

        HashSet<VirReg> mv1 = merges.get(v1) == null ? new HashSet<>() : merges.get(v1);
        mv1.add(v1);
        HashSet<VirReg> mv2 = merges.get(v2) == null ? new HashSet<>() : merges.get(v2);
        mv2.add(v2);

        mv1.addAll(mv2);
        merges.put(v1, mv1);
        merges.remove(v2);
        //更新moveNodes
        moveNodes = new HashSet<>();
        for (Move m : moves) {
            VirReg rs = (VirReg) m.getRs();
            VirReg rd = (VirReg) m.getRd();
            if (mergeNodes.contains(rs)) {
                if (!merges.containsKey(rs)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rs)) {
                            rs = key;
                            break;
                        }
                    }
                }
            }
            if (mergeNodes.contains(rd)) {
                if (!merges.containsKey(rd)) {
                    for (VirReg key : merges.keySet()) {
                        if (merges.get(key).contains(rd)) {
                            rd = key;
                            break;
                        }
                    }
                }
            }
            moveNodes.add(rs);
            moveNodes.add(rd);
        }

        //在冲突图中删除v2,保留v1
        for (VirReg t : curCG.get(v2)) {
//            if (!curCG.containsKey(t)) {
//                System.out.println();
//            }
            curCG.get(t).remove(v2);

            curCG.get(t).add(v1);
            curCG.get(v1).add(t);
        }
        curCG.remove(v2);
        for (VirReg t : CG.get(v2)) {
            CG.get(t).remove(v2);
            CG.get(t).add(v1);
            CG.get(v1).add(t);
        }
        CG.remove(v2);
    }

    private void DeleteNode(VirReg v) {
        for (VirReg t : curCG.get(v)) {
//            if (!curCG.containsKey(t)) {
//                System.out.println();
//            }
            curCG.get(t).remove(v);
        }
        curCG.remove(v);

        if (moveNodes.contains(v)) {
            HashSet<Move> fms = new HashSet<>();
            for (Move m : moves) {
                VirReg rs = (VirReg) m.getRs();
                VirReg rd = (VirReg) m.getRd();
                if (mergeNodes.contains(rs)) {
                    if (!merges.containsKey(rs)) {
                        for (VirReg key : merges.keySet()) {
                            if (merges.get(key).contains(rs)) {
                                rs = key;
                                break;
                            }
                        }
                    }
                }
                if (mergeNodes.contains(rd)) {
                    if (!merges.containsKey(rd)) {
                        for (VirReg key : merges.keySet()) {
                            if (merges.get(key).contains(rd)) {
                                rd = key;
                                break;
                            }
                        }
                    }
                }
                if (v == rs || v == rd) {
                    fms.add(m);
                }
            }
            for (Move m : fms) {
                moves.remove(m);
            }

            moveNodes = new HashSet<>();
            for (Move m : moves) {
                VirReg rs = (VirReg) m.getRs();
                VirReg rd = (VirReg) m.getRd();
                if (mergeNodes.contains(rs)) {
                    if (!merges.containsKey(rs)) {
                        for (VirReg key : merges.keySet()) {
                            if (merges.get(key).contains(rs)) {
                                rs = key;
                                break;
                            }
                        }
                    }
                }
                if (mergeNodes.contains(rd)) {
                    if (!merges.containsKey(rd)) {
                        for (VirReg key : merges.keySet()) {
                            if (merges.get(key).contains(rd)) {
                                rd = key;
                                break;
                            }
                        }
                    }
                }
                moveNodes.add(rs);
                moveNodes.add(rd);
            }
        }
    }

    private void AddNode(VirReg v) {
        curCG.put(v, new HashSet<>());
        for (VirReg t : curCG.keySet()) {
            if (t != v && CG.get(v).contains(t)) {
                curCG.get(v).add(t);
                curCG.get(t).add(v);
            }
        }
    }

    private boolean Select() {
        int outCnt = outNodes.size();
        curCG = new HashMap<>();
        for (int i = outCnt - 1; i >= 0; i--) {
            VirReg node = outNodes.get(i);
            AddNode(node);
            if (AssignPhy(node)) {
                if (spillNodes.contains(node)) {
                    spillNodes.remove(node);
                }
            }
            else {
                DeleteNode(node);
            }
        }
        if (spillNodes.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    private void BuildCG(MipsFunction func) {

        outNodes = new ArrayList<>();
        spillNodes = new ArrayList<>();
        moveNodes = new HashSet<>();
        moves = new HashSet<>();
        deletes = new ArrayList<>();
        merges = new HashMap<>();
        mergeNodes = new HashSet<>();

        int cntBB = func.getBbs().size();
        for (int i = 0; i < cntBB; i++) {
            MipsBlock mb = func.getBbs().get(i);
            int cntIns = mb.getInstrs().size();
            for (int j = 0; j < cntIns; j++) {
                Instr ins = mb.getInstrs().get(j);
                ins.inRegs = new HashSet<>();
                ins.outRegs = new HashSet<>();
                if (ins instanceof Move && ((Move) ins).isPhiMove()) {
                    VirReg v1 = (VirReg) ((Move) ins).getRd();
                    VirReg v2 = (VirReg) ((Move) ins).getRs();
                    moveNodes.add(v1);
                    moveNodes.add(v2);
                    moves.add((Move) ins);
                }
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
                    Set<VirReg> outAns = new HashSet<>();
                    for (Instr nIns : ins.nxtInstr) {
                        outAns.addAll(nIns.inRegs);
                    }
                    if (!ins.outRegs.equals(outAns)) {
                        changed = true;
                        ins.outRegs = outAns;
                    }

                    // in = use ∪ (out - def)
                    Set<VirReg> inAns = new HashSet<>();
                    inAns.addAll(ins.outRegs);
                    inAns.removeAll(ins.defRegs);
//                    inAns.retainAll(ins.defRegs);
                    inAns.addAll(ins.useRegs);
                    if (!inAns.equals(ins.inRegs)) {
                        changed = true;
                        ins.inRegs = inAns;
                    }
                }
            }
        }

        HashMap<VirReg, HashSet<VirReg>> graph = new HashMap<>();
        cntBB = func.getBbs().size();
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
                        if (o != d) {
                            graph.get(d).add(o);
                            graph.get(o).add(d);
                        }
                    }
                }
                for (VirReg o1 : ins.outRegs) {
                    for (VirReg o2 : ins.outRegs) {
                        if (o1 != o2) {
                            graph.get(o1).add(o2);
                            graph.get(o2).add(o1);
                        }
                    }
                }
            }
        }
        CG = graph;
        curCG = new HashMap<>();
        for (VirReg v : graph.keySet()) {
            curCG.put(v, new HashSet<>(graph.get(v)));
        }
//        for (int i = 0; i < cntBB; i++) {
//            MipsBlock mb = func.getBbs().get(i);
//            System.out.println(mb.label);
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
//        System.out.println(func);
//        System.out.println(CG);
    }

    private void ReWrite(MipsFunction func) {
        StackFp stackFp = func.getStackFp();
        for (VirReg v : spillNodes) {
            int cnt = v.instrs.size();
            for (int i = 0; i < cnt; i++) {
                Instr ins = v.instrs.get(i);
                MipsBlock mb = ins.mb;
                if (!stackFp.getSpillers().containsKey(v)) {
                    stackFp.spill(v);
                }
                if (ins.defRegs.contains(v)) {
                    Instr s = new InstrM(InstrM.MType.sw, v, new PhyReg(30), new Imm(stackFp.getSpillers().get(v)), mb);
                    mb.insetAfterInstr(s, ins);
                }
                if (ins.useRegs.contains(v)) {
                    Instr l = new InstrM(InstrM.MType.lw, v, new PhyReg(30), new Imm(stackFp.getSpillers().get(v)), mb);
                    mb.insetBeforeInstr(l, ins);
                }
            }
        }
    }


    private boolean AssignPhy(VirReg v) {
        HashSet<PhyReg> ps = new HashSet<>(Regs);
        for (VirReg u : curCG.get(v)) {
            ps.remove(u.getPhyReg());
        }
        if (ps.size() != 0) {
            for (PhyReg p : ps) {
                //v是合并节点
                if (merges.containsKey(v)) {
                    for (VirReg m : merges.get(v)) {
                        m.setPhyReg(p);
                    }
                }
                else {
                    v.setPhyReg(p);
                }
                return true;
            }
        }
        return false;
    }


    private void genCalls() {
        for (MipsFunction func : program.getFunctions()) {
            for (InstrJ call : func.getStackFp().calls) {
                MipsBlock mb = call.mb;
                // $ra 保护
                int save = func.getStackFp().save(4);
                mb.insetBeforeInstr(new InstrM(InstrM.MType.sw, new PhyReg(31), new PhyReg(30), new Imm(save), mb), call);
                mb.insetAfterInstr(new InstrM(InstrM.MType.lw, new PhyReg(31), new PhyReg(30), new Imm(save), mb), call);
                // 寄存器保护
//                HashSet<PhyReg> saves = getFunc(call.getTarget()).regs;//无脑保护
                //保护所有
                HashSet<PhyReg> saves = new HashSet<>();
                HashSet<VirReg> pass = new HashSet<>(call.outRegs);
                for (VirReg v : pass) {
//                    System.out.println("save: " + "vr" + v.getVirCnt() + " " + v.getPhyReg());
                    saves.add(v.getPhyReg());
                }
//                System.out.println();
                for (PhyReg p : saves) {
                    save = func.getStackFp().save(4);
                    mb.insetBeforeInstr(new InstrM(InstrM.MType.sw, p, new PhyReg(30), new Imm(save), mb), call);
                    mb.insetAfterInstr(new InstrM(InstrM.MType.lw, p, new PhyReg(30), new Imm(save), mb), call);
                }
            }
        }
        for (MipsFunction func : program.getFunctions()) {
            for (InstrJ call : func.getStackFp().calls) {
                // 移动栈帧
                MipsBlock mb = call.mb;
                mb.insetBeforeInstr(new InstrI(InstrI.IType.addi, new PhyReg(30), new PhyReg(30), new Imm(func.getStackFp().getAlignStack()), mb), call);
                mb.insetAfterInstr(new InstrI(InstrI.IType.addi, new PhyReg(30), new PhyReg(30), new Imm(-1 * func.getStackFp().getAlignStack()), mb), call);
            }
        }
    }

    private void genRets() {
        for (MipsFunction f : program.getFunctions()) {
            for (InstrJ ret : f.getStackFp().rets) {
                MipsBlock mb = ret.mb;
                mb.insetBeforeInstr(new InstrI(InstrI.IType.addi, new PhyReg(29), new PhyReg(29),
                        new Imm(-1 * f.getStackFp().getStack()), mb), ret);
            }
        }
    }
}
