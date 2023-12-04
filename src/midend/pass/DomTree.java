package midend.pass;

import midend.ir.Module;
import midend.ir.values.BasicBlock;
import midend.ir.values.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DomTree {
    private Module module;

    public DomTree(Module module) {
        this.module = module;
    }

    public void run() {
        removeUnreachableBB();
        buildCFG();
        buildDomTree();
    }

    public void removeUnreachableBB() {
        for (Function func : module.getFunctions()) {
            ArrayList<BasicBlock> visit = new ArrayList<>();
            HashSet<BasicBlock> blocks = new HashSet<>();
            blocks.addAll(func.getBbs());
            visit.add(func.getEntry());
            blocks.remove(func.getEntry());
            while (!visit.isEmpty()) {
                BasicBlock v = visit.get(0);
                visit.remove(v);
                for (BasicBlock nxt : v.getNxtBBs()) {
                    if (blocks.contains(nxt)) {
                        visit.add(nxt);
                        blocks.remove(nxt);
                    }
                }
            }
            for (BasicBlock bb : blocks) {
                bb.remove();
            }
        }
    }

    public void buildCFG() {
        for (Function func : module.getFunctions()) {
            func.CFG = new HashMap<>();
            for (BasicBlock bb : func.getBbs()) {
                HashSet<BasicBlock> bbs = new HashSet<>(bb.getNxtBBs());
                func.CFG.put(bb, bbs);
            }
        }
    }

    public void buildDomTree() {
        for (Function func : module.getFunctions()) {
            // 得到doms
            // 所有从 entry 到 dom 的路径中都包含 bb
            BasicBlock entry = func.getEntry();
            for (BasicBlock bb : func.getBbs()) {
                HashSet<BasicBlock> doms = new HashSet<>();
                HashSet<BasicBlock> visits = new HashSet<>();
                dfs(entry, bb, visits);
                for (BasicBlock block : func.getBbs()) {
                    if (!visits.contains(block)) {
                        doms.add(block);
                    }
                }
                bb.doms = doms;
            }
//            for (BasicBlock bb : func.getBbs()) {
//                System.out.println(bb.getName() + ":");
//                for (BasicBlock dom : bb.doms) {
//                    System.out.println(dom.getName());
//                }
//                System.out.println();
//            }
            // 得到idoms
            // 严格支配 n，且不严格支配任何严格支配 n 的节点的节点
            for (BasicBlock bb : func.getBbs()) {
                bb.idoms = new HashSet<>();
                for (BasicBlock dom : bb.doms) {
                    if (immDominate(bb, dom)) {
                        bb.idoms.add(dom);
                        dom.myIdom = bb;
                    }
                }
            }
//            for (BasicBlock bb : func.getBbs()) {
//                System.out.println(bb.getName() + ":");
//                for (BasicBlock idom : bb.idoms) {
//                    System.out.println(idom.getName());
//                }
//                System.out.println();
//            }
            // 得到 dom frontier
            // Y 是 X 的支配边界，当且仅当 X 支配 Y 的一个前驱结点（CFG）同时 X 并不严格支配 Y
            for (BasicBlock bb : func.getBbs()) {
                bb.domFrontier = new HashSet<>();
                for (BasicBlock dom : bb.doms) {
                    for (BasicBlock nxt : dom.getNxtBBs()) {
                        if (!(bb.doms.contains(nxt) && bb != nxt)) {
                            bb.domFrontier.add(nxt);
                        }
                    }
                }
            }
//            for (BasicBlock bb : func.getBbs()) {
//                System.out.println(bb.getName() + ":");
//                for (BasicBlock df : bb.domFrontier) {
//                    System.out.println(df.getName());
//                }
//                System.out.println();
//            }
        }
    }

    private boolean immDominate(BasicBlock b1, BasicBlock b2) {
        if (b1.equals(b2)) {
            return false;
        }
        for (BasicBlock dom : b1.doms) {
            if (dom.equals(b1) || dom.equals(b2)) {
                continue;
            }
            if (dom.doms.contains(b2)) {
                return false;
            }
        }
        return true;
    }


    //访问 start 到 end, 记录 visited
    public void dfs(BasicBlock start, BasicBlock end, HashSet<BasicBlock> visited) {
        if (start.equals(end)) {
            return;
        }
        if (visited.contains(start)) {
            return;
        }
        visited.add(start);
        for (BasicBlock basicBlock : start.getNxtBBs()) {
            if (!visited.contains(basicBlock) && !basicBlock.equals(end)) {
                dfs(basicBlock, end, visited);
            }
        }
    }

}
