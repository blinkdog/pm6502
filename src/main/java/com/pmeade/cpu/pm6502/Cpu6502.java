/*
 * Cpu6502.java
 * Copyright 2013 Patrick Meade.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pmeade.cpu.pm6502;

import static com.pmeade.cpu.pm6502.AddressMode.*;
import static com.pmeade.cpu.pm6502.Mnemonic.*;

/**
 * @author pmeade
 */
public interface Cpu6502
{
    public static final int NMI_LO   = 0xFFFA;
    public static final int NMI_HI   = 0xFFFB;
    public static final int RESET_LO = 0xFFFC;
    public static final int RESET_HI = 0xFFFD;
    public static final int IRQ_LO   = 0xFFFE;
    public static final int IRQ_HI   = 0xFFFF;

    public static final int FLAG_CARRY     = 0x01; // b00000001
    public static final int FLAG_ZERO      = 0x02; // b00000010
    public static final int FLAG_INTERRUPT = 0x04; // b00000100
    public static final int FLAG_DECIMAL   = 0x08; // b00001000
    public static final int FLAG_BREAK     = 0x10; // b00010000
    public static final int FLAG_RESERVED  = 0x20; // b00100000
    public static final int FLAG_OVERFLOW  = 0x40; // b01000000
    public static final int FLAG_NEGATIVE  = 0x80; // b10000000

    public static final int[] CYCLES = {
        7, 6, 0, 0, 0, 3, 5, 0, 3, 2, 2, 0, 0, 4, 6, 0, // 0
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0, // 1
        6, 6, 0, 0, 3, 3, 5, 0, 4, 2, 2, 0, 4, 4, 6, 0, // 2
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0, // 3
        4, 6, 0, 0, 0, 3, 5, 0, 3, 2, 2, 0, 3, 6, 6, 0, // 4
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0, // 5
        6, 6, 0, 0, 0, 3, 5, 0, 4, 2, 2, 0, 5, 4, 6, 0, // 6
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0, // 7
        0, 6, 0, 0, 3, 3, 3, 0, 2, 0, 2, 0, 4, 4, 4, 0, // 8
        2, 6, 0, 0, 4, 4, 4, 0, 2, 5, 2, 0, 0, 5, 0, 0, // 9
        2, 6, 2, 0, 3, 3, 3, 0, 2, 2, 2, 0, 4, 4, 4, 0, // a
        2, 5, 0, 0, 4, 4, 4, 0, 2, 4, 2, 0, 4, 4, 4, 0, // b
        2, 6, 0, 0, 3, 3, 5, 0, 2, 2, 2, 0, 4, 4, 6, 0, // c
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0, // d
        2, 6, 0, 0, 3, 3, 5, 0, 2, 2, 2, 0, 4, 4, 6, 0, // e
        2, 5, 0, 0, 0, 4, 6, 0, 2, 4, 0, 0, 0, 4, 7, 0  // f
    };

    public static final int[] LENGTH = {
        1, 2, 0, 0, 0, 2, 2, 0, 1, 2, 1, 0, 0, 3, 3, 0, // 0
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0, // 1
        3, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, // 2
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0, // 3
        3, 2, 0, 0, 0, 2, 2, 0, 1, 2, 1, 0, 3, 1, 3, 0, // 4
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0, // 5
        1, 2, 0, 0, 0, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, // 6
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0, // 7
        0, 2, 0, 0, 2, 2, 2, 0, 1, 0, 1, 0, 3, 3, 3, 0, // 8
        2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 0, 3, 0, 0, // 9
        2, 2, 2, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, // a
        2, 2, 0, 0, 2, 2, 2, 0, 1, 3, 1, 0, 3, 3, 3, 0, // b
        2, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, // c
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0, // d
        2, 2, 0, 0, 2, 2, 2, 0, 1, 2, 1, 0, 3, 3, 3, 0, // e
        2, 2, 0, 0, 0, 2, 2, 0, 1, 3, 0, 0, 0, 3, 3, 0  // f
    };

    public static final boolean[] EXTRA_CYCLES = {
        false, false, false, false, false, false, false, false, // 0
        false, false, false, false, false, false, false, false,
        true,  false, false, false, false, false, false, false, // 1
        false, true,  false, false, false, true,  false, false,
        false, false, false, false, false, false, false, false, // 2
        false, false, false, false, false, false, false, false,
        true,  false, false, false, false, false, false, false, // 3
        false, true,  false, false, false, true,  false, false,
        false, false, false, false, false, false, false, false, // 4
        false, false, false, false, false, false, false, false,
        true,  true,  false, false, false, false, false, false, // 5
        false, true,  false, false, false, true,  false, false,
        false, false, false, false, false, false, false, false, // 6
        false, false, false, false, false, false, false, false,
        true,  true,  false, false, false, false, false, false, // 7
        false, true,  false, false, false, true,  false, false,
        false, false, false, false, false, false, false, false, // 8
        false, false, false, false, false, false, false, false,
        true,  false, false, false, false, false, false, false, // 9
        false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, // a
        false, false, false, false, false, false, false, false,
        true,  true,  false, false, false, false, false, false, // b
        false, true,  false, false, true,  true,  true,  false,
        false, false, false, false, false, false, false, false, // c
        false, false, false, false, false, false, false, false,
        true,  true,  false, false, false, false, false, false, // d
        false, true,  false, false, false, true,  false, false,
        false, false, false, false, false, false, false, false, // e
        false, false, false, false, false, false, false, false,
        true,  false, false, false, false, false, false, false, // f
        false, true,  false, false, false, true,  false, false
    };

    public static final Mnemonic[] MNEMONIC = {
        BRK, ORA, HLT, HLT, HLT, ORA, ASL, HLT, // 0
        PHP, ORA, ASL, HLT, HLT, ORA, ASL, HLT,
        BPL, ORA, HLT, HLT, HLT, ORA, ASL, HLT, // 1
        CLC, ORA, HLT, HLT, HLT, ORA, ASL, HLT,
        JSR, AND, HLT, HLT, BIT, AND, ROL, HLT, // 2
        PLP, AND, ROL, HLT, BIT, AND, ROL, HLT,
        BMI, AND, HLT, HLT, HLT, AND, ROL, HLT, // 3
        SEC, AND, HLT, HLT, HLT, AND, ROL, HLT,
        EOR, EOR, HLT, HLT, HLT, EOR, LSR, HLT, // 4
        PHA, EOR, LSR, HLT, JMP, RTI, LSR, HLT,
        BVC, EOR, HLT, HLT, HLT, EOR, LSR, HLT, // 5
        CLI, EOR, HLT, HLT, HLT, EOR, LSR, HLT,
        RTS, ADC, HLT, HLT, HLT, ADC, ROR, HLT, // 6
        PLA, ADC, ROR, HLT, JMP, ADC, ROR, HLT,
        BVS, ADC, HLT, HLT, HLT, ADC, ROR, HLT, // 7
        SEI, ADC, HLT, HLT, HLT, ADC, ROR, HLT,
        HLT, STA, HLT, HLT, STY, STA, STX, HLT, // 8
        DEY, HLT, TXA, HLT, STY, STA, STX, HLT,
        BCC, STA, HLT, HLT, STY, STA, STX, HLT, // 9
        TYA, STA, TXS, HLT, HLT, STA, HLT, HLT,
        LDY, LDA, LDX, HLT, LDY, LDA, LDX, HLT, // a
        TAY, LDA, TAX, HLT, LDY, LDA, LDX, HLT,
        BCS, LDA, HLT, HLT, LDY, LDA, LDX, HLT, // b
        CLV, LDA, TSX, HLT, LDY, LDA, LDX, HLT,
        CPY, CMP, HLT, HLT, CPY, CMP, DEC, HLT, // c
        INY, CMP, DEX, HLT, CPY, CMP, DEC, HLT,
        BNE, CMP, HLT, HLT, HLT, CMP, DEC, HLT, // d
        CLD, CMP, HLT, HLT, HLT, CMP, DEC, HLT,
        CPX, SBC, HLT, HLT, CPX, SBC, INC, HLT, // e
        INX, SBC, NOP, HLT, CPX, SBC, INC, HLT,
        BEQ, SBC, HLT, HLT, HLT, SBC, INC, HLT, // f
        SED, SBC, HLT, HLT, HLT, SBC, INC, HLT
    };

    public static final AddressMode[] ADDRESS_MODES = {
        IMP, IDX, BAD, BAD, BAD, ZPG, ZPG, BAD, // 0
        IMP, IMM, ACC, BAD, BAD, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // 1
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD,
        ABS, IDX, BAD, BAD, ZPG, ZPG, ZPG, BAD, // 2
        IMP, IMM, ACC, BAD, ABS, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // 3
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD,
        ABS, IDX, BAD, BAD, BAD, ZPG, ZPG, BAD, // 4
        IMP, IMM, ACC, BAD, ABS, IMP, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // 5
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD,
        IMP, IDX, BAD, BAD, BAD, ZPG, ZPG, BAD, // 6
        IMP, IMM, ACC, BAD, IND, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // 7
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD,
        BAD, IDX, BAD, BAD, ZPG, ZPG, ZPG, BAD, // 8
        IMP, BAD, IMP, BAD, ABS, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, ZPX, ZPX, ZPY, BAD, // 9
        IMP, ABY, IMP, BAD, BAD, ABX, BAD, BAD,
        IMM, IDX, IMM, BAD, ZPG, ZPG, ZPG, BAD, // a
        IMP, IMM, IMP, BAD, ABS, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, ZPX, ZPX, ZPY, BAD, // b
        IMP, ABY, IMP, BAD, ABX, ABX, ABY, BAD,
        IMM, IDX, BAD, BAD, ZPG, ZPG, ZPG, BAD, // c
        IMP, IMM, IMP, BAD, ABS, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // d
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD,
        IMM, IDX, BAD, BAD, ZPG, ZPG, ZPG, BAD, // e
        IMP, IMM, IMP, BAD, ABS, ABS, ABS, BAD,
        REL, IDY, BAD, BAD, BAD, ZPX, ZPX, BAD, // f
        IMP, ABY, BAD, BAD, BAD, ABX, ABX, BAD
    };

    public int execute();
    public int getAC();
    public int getPC();
    public int getSP();
    public int getSR();
    public int getXR();
    public int getYR();
    public void reset();
    public void setMemoryIO(MemoryIO mem);
    public void setAC(int i);
    public void setSR(int i);
    public void setXR(int i);
    public void setYR(int i);
}
