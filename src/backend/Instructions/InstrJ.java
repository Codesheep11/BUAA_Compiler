package backend.Instructions;

import backend.Component.MipsBlock;
import backend.Operand.VirReg;

import java.util.ArrayList;

public class InstrJ extends Instr {
    private JType jType;

    private String target;


    public enum JType {
        j,
        jal,
        jr;

        @Override
        public String toString() {
            switch (this) {
                case j: {
                    return "j";
                }
                case jr: {
                    return "jr";
                }
                case jal: {
                    return "jal";
                }
                default: {
                    return null;
                }
            }
        }
    }

    public InstrJ(JType jType, String target, MipsBlock mb) {
        this.jType = jType;
        this.target = target;
        this.mb = mb;
    }

    public JType getjType() {
        return jType;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(jType + " ");
        if (jType == JType.jr) {
            sb.append("$ra");
        }
        else {
            sb.append(target);
        }
        return sb.toString();
    }


}
