package midend.pass;

import midend.ir.types.ArrayType;
import midend.ir.types.PointerType;
import midend.ir.values.BasicBlock;
import midend.ir.values.Function;
import midend.ir.Module;
import midend.ir.values.User;
import midend.ir.values.Value;
import midend.ir.values.constants.ConstantInt;
import midend.ir.values.instructions.Instruction;
import midend.ir.values.instructions.memory.Load;
import midend.ir.values.instructions.memory.Phi;
import midend.ir.values.instructions.memory.Store;

import java.util.*;

public class Mem2Reg {
    private Module module;

    public Mem2Reg(Module module) {
        this.module = module;
    }

    public void run() {
        for (Function func : module.getFunctions()) {
            insertPhi(func);
        }
    }


    public void insertPhi(Function func) {
        for (BasicBlock bb : func.getBbs()) {
            boolean changed = true;
            while (changed) {
                changed = false;
                for (Instruction instr : bb.getInsts()) {
                    if (instr.isAlloca() && !(((PointerType) instr.getType()).getPointtoType() instanceof ArrayType)) {
                        changed = true;
                        ArrayList<Instruction> defs = new ArrayList<>();
                        ArrayList<BasicBlock> defBBs = new ArrayList<>();
                        ArrayList<Instruction> uses = new ArrayList<>();
                        ArrayList<BasicBlock> useBBs = new ArrayList<>();
                        for (User u : instr.getUsers()) {
                            if (!func.getBbs().contains(u.getParent())) {
                                continue;
                            }
                            if (u instanceof Store) {
                                defs.add((Store) u);
                                defBBs.add((BasicBlock) u.getParent());
                            }
                            else if (u instanceof Load) {
                                uses.add((Load) u);
                                useBBs.add((BasicBlock) u.getParent());
                            }
                        }
                        instr.remove();
                        /*
                        小优化
                        1. 移除没有 users 的 alloca
                        2. 如果一个 alloca 只有一个 defBB，那么 users 可以替换成 store 的值。这里需要保证下面两个条件（见函数 rewriteSingleStoreAlloca）
                        如果 load 和 store 在同一个基本块，则 store 应该在 load 前面
                        如果二者在不同基本块，则需要保证 load 被替换成最后的 storeVal
                        */
                        HashSet<BasicBlock> defBBSet = new HashSet<>();
                        defBBSet.addAll(defBBs);
                        if (useBBs.isEmpty()) {
                            for (Instruction def : defs) {
                                def.remove();
                            }
                        }
                        else if (defBBSet.size() == 1) {
                            BasicBlock defBB = defBBs.get(0);
                            Store nowDef = null;
                            for (Instruction cur : defBB.getInsts()) {
                                if (defs.contains(cur)) {
                                    nowDef = (Store) cur;
                                }
                                else if (uses.contains(cur)) {
                                    if (nowDef == null) {
                                        cur.replaceUseOf(new ConstantInt(32, 0));
                                    }
                                    else {
                                        cur.replaceUseOf(nowDef.getStoreVal());
                                    }
                                }
                            }
                            for (Instruction def : defs) {
                                def.remove();
                            }
                            for (Instruction use : uses) {
                                use.remove();
                                if (!use.getParent().equals(defBB)) {
                                    if (defBB.doms.contains(use.getParent())) {
                                        use.replaceUseOf(nowDef.getStoreVal());
                                    }
                                    else {
                                        use.replaceUseOf(new ConstantInt(32, 0));
                                    }
                                }
                            }
                        }
                        else {
                            ArrayList<BasicBlock> F = new ArrayList<>();
                            ArrayList<BasicBlock> W = new ArrayList<>(defBBs);

                            while (!W.isEmpty()) {
                                BasicBlock X = W.get(0);
                                W.remove(X);
                                if (X.domFrontier == null) {
                                    System.out.println(1);
                                }
                                for (BasicBlock Y : X.domFrontier) {
                                    if (!F.contains(Y)) {
                                        F.add(Y);
                                        if (!defBBs.contains(Y)) {
                                            W.add(Y);
                                        }
                                    }
                                }
                            }

                            for (BasicBlock block : F) {
                                Phi phi = new Phi(((PointerType) instr.getType()).getPointtoType(), block);
                                block.insertPhi(phi);
                                uses.add(phi);
                                defs.add(phi);
                            }

                            Stack<Value> stack = new Stack<>();
                            stack.push(new ConstantInt(32, 0));
                            Rename(stack, func.getEntry(), uses, defs);

                            for (Instruction def : defs) {
                                if (!def.isPhi()) {
                                    def.remove();
                                }
                            }
                            for (Instruction use : uses) {
                                if (!use.isPhi()) {
                                    use.remove();
                                }
                            }
                        }
                        break;
                    }
                }
            }

        }
    }

    public void Rename(Stack<Value> stack, BasicBlock bb, ArrayList<Instruction> uses, ArrayList<Instruction> defs) {
        int cnt = 0;
        for (Instruction instr : bb.getInsts()) {
            if (!(instr.isPhi()) && uses.contains(instr)) {
                instr.replaceUseOf(stack.peek());
            }
            if (defs.contains(instr)) {
                if (instr.isStore()) {
                    Value v = ((Store) instr).getStoreVal();
                    stack.push(v);
                }
                else if (instr.isPhi()) {
                    stack.push(instr);
                }
                cnt++;
            }
        }

        //更新 nxt phi
        for (BasicBlock block : bb.getNxtBBs()) {
            for (Instruction instr : block.getInsts()) {
                if (!(instr.isPhi())) {
                    break;
                }
                if (uses.contains(instr)) {
                    ((Phi) instr).updateUseValue(block.getPreBBs().indexOf(bb), stack.peek());
                }
            }
        }

        for (BasicBlock idomBB : bb.idoms) {
            Rename(stack, idomBB, uses, defs);
        }

        for (int i = 0; i < cnt; i++) {
            stack.pop();
        }
    }

}
