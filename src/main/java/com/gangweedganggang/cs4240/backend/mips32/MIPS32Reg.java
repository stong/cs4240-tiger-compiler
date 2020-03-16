package com.gangweedganggang.cs4240.backend.mips32;

import com.gangweedganggang.cs4240.backend.RegType;
import com.gangweedganggang.cs4240.backend.TargetReg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.gangweedganggang.cs4240.backend.RegType.*;

public class MIPS32Reg extends TargetReg {
    private MIPS32Reg(int index, RegType type) {
        super(index, type);
    }

    public static final int REG_ZERO = 0;
    public static final MIPS32Reg ZERO = new MIPS32Reg(REG_ZERO, INT);
    public static final int REG_AT = 1;
    public static final MIPS32Reg AT = new MIPS32Reg(REG_AT, INT);
    public static final int REG_V0 = 2;
    public static final MIPS32Reg V0 = new MIPS32Reg(REG_V0, INT);
    public static final int REG_V1 = 3;
    public static final MIPS32Reg V1 = new MIPS32Reg(REG_V1, INT);
    public static final int REG_A0 = 4;
    public static final MIPS32Reg A0 = new MIPS32Reg(REG_A0, INT);
    public static final int REG_A1 = 5;
    public static final MIPS32Reg A1 = new MIPS32Reg(REG_A1, INT);
    public static final int REG_A2 = 6;
    public static final MIPS32Reg A2 = new MIPS32Reg(REG_A2, INT);
    public static final int REG_A3 = 7;
    public static final MIPS32Reg A3 = new MIPS32Reg(REG_A3, INT);
    public static final int REG_T0 = 8;
    public static final MIPS32Reg T0 = new MIPS32Reg(REG_T0, INT);
    public static final int REG_T1 = 9;
    public static final MIPS32Reg T1 = new MIPS32Reg(REG_T1, INT);
    public static final int REG_T2 = 10;
    public static final MIPS32Reg T2 = new MIPS32Reg(REG_T2, INT);
    public static final int REG_T3 = 11;
    public static final MIPS32Reg T3 = new MIPS32Reg(REG_T3, INT);
    public static final int REG_T4 = 12;
    public static final MIPS32Reg T4 = new MIPS32Reg(REG_T4, INT);
    public static final int REG_T5 = 13;
    public static final MIPS32Reg T5 = new MIPS32Reg(REG_T5, INT);
    public static final int REG_T6 = 14;
    public static final MIPS32Reg T6 = new MIPS32Reg(REG_T6, INT);
    public static final int REG_T7 = 15;
    public static final MIPS32Reg T7 = new MIPS32Reg(REG_T7, INT);
    public static final int REG_S0 = 16;
    public static final MIPS32Reg S0 = new MIPS32Reg(REG_S0, INT);
    public static final int REG_S1 = 17;
    public static final MIPS32Reg S1 = new MIPS32Reg(REG_S1, INT);
    public static final int REG_S2 = 18;
    public static final MIPS32Reg S2 = new MIPS32Reg(REG_S2, INT);
    public static final int REG_S3 = 19;
    public static final MIPS32Reg S3 = new MIPS32Reg(REG_S3, INT);
    public static final int REG_S4 = 20;
    public static final MIPS32Reg S4 = new MIPS32Reg(REG_S4, INT);
    public static final int REG_S5 = 21;
    public static final MIPS32Reg S5 = new MIPS32Reg(REG_S5, INT);
    public static final int REG_S6 = 22;
    public static final MIPS32Reg S6 = new MIPS32Reg(REG_S6, INT);
    public static final int REG_S7 = 23;
    public static final MIPS32Reg S7 = new MIPS32Reg(REG_S7, INT);
    public static final int REG_T8 = 24;
    public static final MIPS32Reg T8 = new MIPS32Reg(REG_T8, INT);
    public static final int REG_T9 = 25;
    public static final MIPS32Reg T9 = new MIPS32Reg(REG_T9, INT);
    public static final int REG_K0 = 26;
    public static final MIPS32Reg K0 = new MIPS32Reg(REG_K0, INT);
    public static final int REG_K1 = 27;
    public static final MIPS32Reg K1 = new MIPS32Reg(REG_K1, INT);
    public static final int REG_GP = 28;
    public static final MIPS32Reg GP = new MIPS32Reg(REG_GP, INT);
    public static final int REG_SP = 29;
    public static final MIPS32Reg SP = new MIPS32Reg(REG_SP, INT);
    public static final int REG_FP = 30;
    public static final MIPS32Reg FP = new MIPS32Reg(REG_FP, INT);
    public static final int REG_RA = 31;
    public static final MIPS32Reg RA = new MIPS32Reg(REG_RA, INT);
    private static final MIPS32Reg[] INT_REGS = new MIPS32Reg[] { ZERO, AT, V0, V1, A0, A1, A2, A3, T0, T1, T2, T3, T4, T5, T6, T7, S0, S1, S2, S3, S4, S5, S6, S7, T8, T9, K0, K1, GP, SP, FP, RA };

    private static final int REG_F0 = 32;
    private static final MIPS32Reg[] FLOAT_REGS = new MIPS32Reg[32];
    static {
        System.out.print("x");
         for (int i = 0; i < FLOAT_REGS.length; i++) {
             FLOAT_REGS[i] = new MIPS32Reg(REG_F0 + i, FLOAT);
         }
    }

    public static final int REG_LO = 64;
    public static final MIPS32Reg LO = new MIPS32Reg(REG_LO, SPECIAL);
    public static final int REG_HI = 65;
    public static final MIPS32Reg HI = new MIPS32Reg(REG_HI, SPECIAL);
    public static final int REG_C1F = 66; // floating-point coprocessor condition bit
    public static final MIPS32Reg C1F = new MIPS32Reg(REG_C1F, SPECIAL);

    public static final int MAX_REG = 100;

    public static MIPS32Reg newInt(int id) {
        if (id < 32) return INT_REGS[id];
        else return new MIPS32Reg(id, INT);
    }

    public static MIPS32Reg newFloat(int id) {
        if (id < 32) return FLOAT_REGS[id];
        else return new MIPS32Reg(id, FLOAT);
    }

    @Override
    public boolean isPhysicalRegister() {
        return getIndex() < MAX_REG;
    }

    @Override
    public boolean isZeroRegister() {
        return this.equals(ZERO);
    }

    // LOL SAVED REGS? NEVER HEARD OF THEM. EVERYTHING IS VOLATILE!
    private static Set<MIPS32Reg> calleeSavedRegs = new HashSet<>(Arrays.asList(GP, SP, FP, RA));

    @Override
    public boolean isCalleeSaved() {
        return calleeSavedRegs.contains(this);
    }

    @Override
	public String toString() {
        switch (getIndex()) {
        case REG_ZERO:
            return "$zero";
        case REG_AT:
            return "$at";
        case REG_V0:
            return "$v0";
        case REG_V1:
            return "$v1";
        case REG_A0:
        case REG_A1:
        case REG_A2:
        case REG_A3:
            return "$a" + (getIndex() - REG_A0);
        case REG_T0:
        case REG_T1:
        case REG_T2:
        case REG_T3:
        case REG_T4:
        case REG_T5:
        case REG_T6:
        case REG_T7:
            return "$t" + (getIndex() - REG_T0);
        case REG_T8:
        case REG_T9:
            return "$t" + (getIndex() - REG_T8);
        case REG_S0:
        case REG_S1:
        case REG_S2:
        case REG_S3:
        case REG_S4:
        case REG_S5:
        case REG_S6:
        case REG_S7:
            return "$s" + (getIndex() - REG_S0);
        case REG_K0:
            return "$k0";
        case REG_K1:
            return "$k1";
        case REG_GP:
            return "$gp";
        case REG_SP:
            return "$sp";
        case REG_FP:
            return "$fp";
        case REG_RA:
            return "$ra";
        default:
            if (getIndex() >= REG_F0 && getIndex() < REG_F0 + 32)
                return "$f" + (getIndex() - REG_F0);
		    return (getType().equals(INT) ? "$r" : "$f") + getIndex();
        }
	}
}

