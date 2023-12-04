package backend.Operand;

public class PhyReg extends Reg {

    private int id;

    public PhyReg(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /*
        常量 0
        $1	        $at	保留给汇编器使用
        $2 ~ $3 	$v0 ~ $v1	函数调用返回值 $v1用于临时寄存器
        $4          $a0         系统调用
        着色分配的寄存器:
        //
        $4 ~ $7	    $a0 ~ $a3	函数调用参数 //参数全部入栈 //
        $8 ~ $15	$t0 ~ $t7	临时变量
        $16 ~ $23	$s0 ~ $s7	需要保存的变量
        $24 ~ $25	$t8 ~ $t9	临时变量
        //
        $26 ~ $27	$k0 ~ $k1	给操作系统使用
        $28	        $gp	全局指针
        $29     	$sp	堆栈指针
        $30     	$fp	帧指针
        $31     	$ra	返回地址
         */
    public String toString() {
        String str = "$";
        if (id == 0) {
            str += "zero";
        }
        else if (id == 1) {
            str += "at";
        }
        else if (id >= 2 && id <= 3) {
            str += "v" + (id - 2);
        }
        else if (id >= 4 && id <= 7) {
            str += "a" + (id - 4);
        }
        else if (id >= 8 && id <= 15) {
            str += "t" + (id - 8);
        }
        else if (id >= 16 && id <= 23) {
            str += "s" + (id - 16);
        }
        else if (id >= 24 && id <= 25) {
            str += "t" + (id - 24 + 8);
        }
        else if (id >= 26 && id <= 27) {
            str += "k" + (id - 26);
        }
        else if (id == 28) {
            str += "gp";
        }
        else if (id == 29) {
            str += "sp";
        }
        else if (id == 30) {
            str += "fp";
        }
        else {
            str += "ra";
        }
        return str;
    }

}
