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
            case AND:
                read(ADDRESS_MODES[opcode]);
                ac &= s1;
                updateNZ(ac);
                break;
            case BEQ:
                if((sr & FLAG_ZERO) == FLAG_ZERO) {
                    branch();
                }
                break;
            case BIT:
                read(ADDRESS_MODES[opcode]);
                updateNV(s1);
                s1 &= ac;
                updateZ(s1);
                break;
            case BNE:
                if((sr & FLAG_ZERO) == 0x00) {
                    branch();
                }
                break;
            case BPL:
                if((sr & FLAG_NEGATIVE) == 0x00) {
                    branch();
                }
                break;
            case BRK:
                nextPC();
                push((pc & 0xff00) >> 8);
                push(pc & 0xff);
                sr |= (FLAG_BREAK | FLAG_RESERVED);
                push(sr);
                sr |= FLAG_INTERRUPT;
                pc = mem.read(IRQ_LO);
                pc |= (mem.read(IRQ_HI) << 8);
                break;
            case CLD:
                sr &= ~FLAG_DECIMAL;
                break;
            case CMP:
                s1 = ac - s1;
                if(s1 < 0) { sr |= FLAG_CARRY; }
                else       { sr &= FLAG_CARRY; }
                updateN(s1);
                updateZ(s1 & 0xff);
                break;
            case DEX:
                xr--; xr &= 0xff;
                updateNZ(xr);
                break;
            case DEY:
                yr--; yr &= 0xff;
                updateNZ(yr);
                break;
            case INX:
                xr++; xr &= 0xff;
                updateNZ(xr);
                break;
            case JMP:
                pc = s2;
                break;
            case JSR:
                pc--;
                push((pc & 0xff00) >> 8);
                push(pc & 0xff);
                pc = s2;
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
            case LSR:
                read(ADDRESS_MODES[opcode]);
                if((s1 & 0x01) == 0x01) { sr |= FLAG_CARRY; }
                else                    { sr &= ~FLAG_CARRY; }
                s1 >>= 1;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case RTS:
                pc = pop();
                pc |= (pop() << 8);
                nextPC();
                break;
            case SEI:
                sr |= FLAG_INTERRUPT;
                break;
            case STA:
                s1 = ac;
                write(ADDRESS_MODES[opcode]);
                break;
            case STX:
                s1 = xr;
                write(ADDRESS_MODES[opcode]);
                break;
            case TXS:
                sp = xr;
                break;
            default:
                throw new UnsupportedOperationException("Opcode: 0x" + Integer.toHexString(opcode));
        }
        
        System.err.println(MNEMONIC[opcode] + " " + Integer.toHexString(s2));
        
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

    public void setSR(int sr) {
        this.sr = sr;
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
//                if(s4 < 0x80) { s2 = pc + s4; }
//                else { s2 = pc - (s4 & 0x7f); }
                s2 = pc + ((byte)s4);
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
                throw new UnsupportedOperationException("AddressMode: " + addressMode);
        }
    }
    
    private void nextPC() {
        pc++;
        pc &= 0xffff;
    }

    private int pop() {
        sp++; sp &= 0xff;
        return mem.read(0x100 | sp);
    }
    
    private void push(int value) {
        mem.write((0x100 | sp), value);
        sp--; sp &= 0xff;
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

    private void updateN(int value) {
        if((value & 0x80) == 0x80) { sr |= FLAG_NEGATIVE;  }
        else                       { sr &= ~FLAG_NEGATIVE; }
        sr |= FLAG_RESERVED;
    }
    
    private void updateNV(int value) {
        if((value & 0x80) == 0x80) { sr |= FLAG_NEGATIVE;  }
        else                       { sr &= ~FLAG_NEGATIVE; }
        if((value & 0x40) == 0x40) { sr |= FLAG_OVERFLOW;  }
        else                       { sr &= ~FLAG_OVERFLOW; }
        sr |= FLAG_RESERVED;
    }

    private void updateNZ(int value) {
        if((value & 0x80) == 0x80) { sr |= FLAG_NEGATIVE;  }
        else                       { sr &= ~FLAG_NEGATIVE; }
        if(value == 0)             { sr |= FLAG_ZERO; }
        else                       { sr &= ~FLAG_ZERO; }
        sr |= FLAG_RESERVED;
    }

    private void updateZ(int value) {
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
