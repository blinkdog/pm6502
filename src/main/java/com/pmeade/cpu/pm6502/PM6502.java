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
    public void doNMI() {
        push((pc & 0xff00) >> 8);
        push(pc & 0xff);
        sr |= FLAG_RESERVED;
        push(sr);
        sr |= FLAG_INTERRUPT;
        pc = mem.read(NMI_LO);
        pc |= (mem.read(NMI_HI) << 8);
    }

    public int execute() {
        int c1;
        int temp;
        int opcode = mem.read(pc);
        nextPC();
        
        cycles = CYCLES[opcode];
        
        calculateAddress(ADDRESS_MODES[opcode], EXTRA_CYCLES[opcode]);
        
        switch(MNEMONIC[opcode]) {
            case ADC:
                read(ADDRESS_MODES[opcode]);
                c1 = (((sr & FLAG_CARRY) == FLAG_CARRY) ? 1 : 0);
                temp = s1 + ac + c1;
                updateZ(temp & 0xff);
                if((sr & FLAG_DECIMAL) == FLAG_DECIMAL) {
                    if(((ac & 0xf) + (s1 & 0xf) + c1) > 9) { temp += 6; }
                    updateN(temp);
                    boolean v3 = ((ac ^ s1) & 0x80) == 0x00;
                    boolean v2 = ((ac ^ temp) & 0x80) != 0x00;
                    boolean v1 = v2 && v3;
                    if(v1) { sr |= FLAG_OVERFLOW; }
                    else   { sr &= ~FLAG_OVERFLOW; }
                    if(temp > 0x99) { temp += 96; }
                    if(temp > 0x99) { sr |= FLAG_CARRY; }
                    else            { sr &= ~FLAG_CARRY; }
                } else {
                    updateN(temp);
                    boolean v3 = ((ac ^ s1) & 0x80) == 0x00;
                    boolean v2 = ((ac ^ temp) & 0x80) != 0x00;
                    boolean v1 = v2 && v3;
                    if(v1) { sr |= FLAG_OVERFLOW; }
                    else   { sr &= ~FLAG_OVERFLOW; }
                    if(temp > 0xff) { sr |= FLAG_CARRY; }
                    else            { sr &= ~FLAG_CARRY; }
                }
                ac = temp & 0xff;
                break;
            case AND:
                read(ADDRESS_MODES[opcode]);
                ac &= s1;
                updateNZ(ac);
                break;
            case ASL:
                read(ADDRESS_MODES[opcode]);
                if((s1 & 0x80) == 0x80) { sr |= FLAG_CARRY; }
                else                    { sr &= ~FLAG_CARRY; }
                s1 <<= 1; s1 &= 0xfe;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case BCC:
                if((sr & FLAG_CARRY) == 0x00) {
                    branch();
                }
                break;
            case BCS:
                if((sr & FLAG_CARRY) == FLAG_CARRY) {
                    branch();
                }
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
            case BMI:
                if((sr & FLAG_NEGATIVE) == FLAG_NEGATIVE) {
                    branch();
                }
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
            case BVC:
                if((sr & FLAG_OVERFLOW) == 0x00) {
                    branch();
                }
                break;
            case BVS:
                if((sr & FLAG_OVERFLOW) == FLAG_OVERFLOW) {
                    branch();
                }
                break;
            case CLC:
                sr &= ~FLAG_CARRY;
                break;
            case CLD:
                sr &= ~FLAG_DECIMAL;
                break;
            case CLI:
                sr &= ~FLAG_INTERRUPT;
                break;
            case CLV:
                sr &= ~FLAG_OVERFLOW;
                break;
            case CMP:
                s1 = ac - s1;
                if(s1 < 0) { sr |= FLAG_CARRY; }
                else       { sr &= FLAG_CARRY; }
                updateN(s1);
                updateZ(s1 & 0xff);
                break;
            case CPX:
                s1 = xr - s1;
                if(s1 < 0) { sr |= FLAG_CARRY; }
                else       { sr &= FLAG_CARRY; }
                updateN(s1);
                updateZ(s1 & 0xff);
                break;
            case CPY:
                s1 = yr - s1;
                if(s1 < 0) { sr |= FLAG_CARRY; }
                else       { sr &= FLAG_CARRY; }
                updateN(s1);
                updateZ(s1 & 0xff);
                break;
            case DEC:
                read(ADDRESS_MODES[opcode]);
                s1--; s1 &= 0xff;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case DEX:
                xr--; xr &= 0xff;
                updateNZ(xr);
                break;
            case DEY:
                yr--; yr &= 0xff;
                updateNZ(yr);
                break;
            case EOR:
                read(ADDRESS_MODES[opcode]);
                ac ^= s1;
                updateNZ(ac);
                break;
            case INC:
                read(ADDRESS_MODES[opcode]);
                s1++; s1 &= 0xff;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case INX:
                xr++; xr &= 0xff;
                updateNZ(xr);
                break;
            case INY:
                yr++; yr &= 0xff;
                updateNZ(yr);
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
            case LDY:
                read(ADDRESS_MODES[opcode]);
                yr = s1;
                updateNZ(yr);
                break;
            case LSR:
                read(ADDRESS_MODES[opcode]);
                if((s1 & 0x01) == 0x01) { sr |= FLAG_CARRY; }
                else                    { sr &= ~FLAG_CARRY; }
                s1 >>= 1;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case NOP:
                // this instruction intentionally left blank
                break;
            case ORA:
                read(ADDRESS_MODES[opcode]);
                ac |= s1;
                updateNZ(ac);
                break;
            case PHA:
                push(ac);
                break;
            case PHP:
                sr |= FLAG_RESERVED;
                push(sr);
                break;
            case PLA:
                ac = pop();
                updateNZ(ac);
                break;
            case PLP:
                sr = pop();
                sr |= FLAG_RESERVED;
                break;
            case ROL:
                read(ADDRESS_MODES[opcode]);
                s1 <<= 1;
                if((sr & FLAG_CARRY) == FLAG_CARRY) { s1 |= 0x01; }
                if(s1 > 0xff) { sr |= FLAG_CARRY; }
                else          { sr &= ~FLAG_CARRY; }
                s1 &= 0xff;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case ROR:
                read(ADDRESS_MODES[opcode]);
                if((sr & FLAG_CARRY) == FLAG_CARRY) { s1 |= 0x100; }
                if((s1 & 0x01) == 0x01) { sr |= FLAG_CARRY; }
                else                    { sr &= ~FLAG_CARRY; }
                s1 >>= 1;
                updateNZ(s1);
                write(ADDRESS_MODES[opcode]);
                break;
            case RTI:
                sr = pop();
                sr |= FLAG_RESERVED;
                pc = pop();
                pc |= (pop() << 8);
                break;
            case RTS:
                pc = pop();
                pc |= (pop() << 8);
                nextPC();
                break;
            case SBC:
                read(ADDRESS_MODES[opcode]);
                c1 = (((sr & FLAG_CARRY) == FLAG_CARRY) ? 0 : 1);
                temp = ac - s1 - c1;
                updateN(temp);
                updateZ(temp & 0xff);
                boolean v3 = ((ac ^ temp) & 0x80) != 0x00;
                boolean v2 = ((ac ^ s1) & 0x80) != 0x00;
                boolean v1 = v2 && v3;
                if(v1) { sr |= FLAG_OVERFLOW; }
                else   { sr &= ~FLAG_OVERFLOW; }
                if((sr & FLAG_DECIMAL) == FLAG_DECIMAL) {
                    if(((ac & 0xf) - (c1)) < (s1 & 0xf)) { temp -= 0x6; }
                    if(temp > 0x99) { temp -= 0x60; }
                }
                if(temp < 0x100) { sr |= FLAG_CARRY; }
                else             { sr &= ~FLAG_CARRY; }
                ac = temp & 0xff;
                break;
            case SEC:
                sr |= FLAG_CARRY;
                break;
            case SED:
                sr |= FLAG_DECIMAL;
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
            case STY:
                s1 = yr;
                write(ADDRESS_MODES[opcode]);
                break;
            case TAX:
                xr = ac;
                updateNZ(xr);
                break;
            case TAY:
                yr = ac;
                updateNZ(yr);
                break;
            case TSX:
                xr = sp;
                updateNZ(xr);
                break;
            case TXA:
                ac = xr;
                updateNZ(ac);
                break;
            case TXS:
                sp = xr;
                break;
            case TYA:
                ac = yr;
                updateNZ(ac);
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

    public void setSP(int sp) {
        this.sp = sp;
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
