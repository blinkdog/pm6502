/*
 * PM6502.java
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

/**
 * @author pmeade
 */
public class PM6502 implements Cpu6502
{
    public int execute() {
        int opcode = mem.read(pc);
        nextPC();
        
        cycles = CYCLES[opcode];
        
        calculateAddress(ADDRESS_MODES[opcode], EXTRA_CYCLES[opcode]);
        
        switch(MNEMONIC[opcode]) {
            case BEQ:
                if((sr & FLAG_ZERO) == FLAG_ZERO) {
                    branch();
                }
                break;
            case DEX:
                xr--; xr &= 0xff;
                updateNZ(xr);
                break;
            case DEY:
                yr--; yr &= 0xff;
                updateNZ(yr);
                break;
            case LDA:
                read(ADDRESS_MODES[opcode]);
                ac = s1;
                updateNZ(ac);
                break;
            case LDX:
                read(ADDRESS_MODES[opcode]);
                xr = s1;
                updateNZ(xr);
                break;
            case JMP:
                pc = s2;
                break;
            case SEI:
                sr |= FLAG_INTERRUPT;
                break;
            case STA:
                s1 = ac;
                write(ADDRESS_MODES[opcode]);
                break;
            default:
                throw new UnsupportedOperationException("Opcode: 0x" + Integer.toHexString(opcode));
        }
        
        return cycles;
    }
    
    public int getAC() {
        return ac;
    }

    public int getPC() {
        return pc;
    }

    public int getSP() {
        return sp;
    }

    public int getSR() {
        return sr;
    }

    public int getXR() {
        return xr;
    }

    public int getYR() {
        return yr;
    }
    
    public void reset() {
        pc = mem.read(RESET_LO);
        pc |= (mem.read(RESET_HI) << 8);
        sp = 0xFF;
        sr = FLAG_RESERVED | FLAG_ZERO;
    }

    public void setMemoryIO(MemoryIO mem) {
        this.mem = mem;
    }

    public void setAC(int ac) {
        this.ac = ac;
    }
    
    public void setXR(int xr) {
        this.xr = xr;
    }

    public void setYR(int yr) {
        this.yr = yr;
    }
    
    // ------------------------------------------------------------------------
    
    private void branch() {
        cycles++;
        if((s2 & 0xff00) != (s3 & 0xff00)) { cycles++; }
        pc = s2;
    }
    
    private void calculateAddress(AddressMode addressMode, boolean extraCycle)
    {
        switch(addressMode)
        {
            case ABS:
                s2 = mem.read(pc);
                nextPC();
                s2 |= (mem.read(pc) << 8);
                nextPC();
                break;
            case ABX:
                s3 = mem.read(pc);
                nextPC();
                s3 |= (mem.read(pc) << 8);
                nextPC();
                s2 = s3 + xr;
                s2 &= 0xffff;
                if(extraCycle && ((s2 & 0xff00) != (s3 & 0xff00))) { cycles++; }
                break;
            case ABY:
                s3 = mem.read(pc);
                nextPC();
                s3 |= (mem.read(pc) << 8);
                nextPC();
                s2 = s3 + yr;
                s2 &= 0xffff;
                if(extraCycle && ((s2 & 0xff00) != (s3 & 0xff00))) { cycles++; }
                break;
            case ACC:
                s1 = ac;
                break;
            case IDX:
                s4 = mem.read(pc);
                nextPC();
                s3 = s4 + xr;
                s3 &= 0xff;
                s2 = mem.read(s3);
                s3++; s3 &= 0xff;
                s2 |= (mem.read(s3) << 8);
                break;
            case IDY:
                s4 = mem.read(pc);
                nextPC();
                s3 = mem.read(s4);
                s4++; s4 &= 0xff;
                s3 |= (mem.read(s4) << 8);
                s2 = s3 + yr;
                s2 &= 0xffff;
                if(extraCycle && ((s2 & 0xff00) != (s3 & 0xff00))) { cycles++; }
                break;
            case IMM:
                s1 = mem.read(pc);
                nextPC();
                break;
            case IMP:
                break;
            case IND:
                s3 = mem.read(pc);
                nextPC();
                s3 |= (mem.read(pc) << 8);
                nextPC();
                s2 = mem.read(s3);
                s3++; s3 &= 0xffff;
                s2 |= (mem.read(s3) << 8);
                break;
            case REL:
                s4 = mem.read(pc);
                nextPC();
                s3 = pc;
                if(s4 < 0x80) { s2 = pc + s4; }
                else { s2 = pc - (s4 & 0x7f); }
                s2 &= 0xffff;
                break;
            case ZPG:
                s2 = mem.read(pc);
                nextPC();
                break;
            case ZPX:
                s3 = mem.read(pc);
                nextPC();
                s2 = s3 + xr;
                s2 &= 0xff;
                break;
            case ZPY:
                s3 = mem.read(pc);
                nextPC();
                s2 = s3 + yr;
                s2 &= 0xff;
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
    
    private void nextPC() {
        pc++;
        pc &= 0xffff;
    }

    private void read(AddressMode addressMode) {
        switch(addressMode) {
            case ACC:
            case IMM:
                break;
            default:
                s1 = mem.read(s2);
                break;
        }
    }
    
    private void updateNZ(int value) {
        if((value & 0x80) == 0x80) { sr |= FLAG_NEGATIVE;  }
        else                       { sr &= ~FLAG_NEGATIVE; }
        if(value == 0)             { sr |= FLAG_ZERO; }
        else                       { sr &= ~FLAG_ZERO; }
        sr |= FLAG_RESERVED;
    }

    private void write(AddressMode addressMode) {
        switch(addressMode) {
            case IMM:
                break;
            case ACC:
                ac = s1;
                break;
            default:
                mem.write(s2,s1);
                break;
        }
    }
    
    private int ac;
    private int pc;
    private int sp;
    private int sr;
    private int xr;
    private int yr;
    
    private MemoryIO mem;
    
    private int cycles;
    private int s1;
    private int s2;
    private int s3;
    private int s4;
}
