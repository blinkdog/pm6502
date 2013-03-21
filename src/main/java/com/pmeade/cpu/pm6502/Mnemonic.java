/*
 * Mnemonic.java
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
public enum Mnemonic
{
    ADC, AND, ASL, BCC, BCS, BEQ, BIT, BMI,
    BPL, BRK, BVC, BVS, CLC, CLD, CLI, CLV,
    CMP, CPX, CPY, DEC, DEX, DEY, EOR, INC,
    INX, INY, JMP, JSR, LDA, LDX, LDY, LSR,
    NOP, ORA, PHA, PHP, PLA, PLP, ROL, ROR,
    RTI, RTS, SBC, SEC, SED, SEI, STA, STX,
    STY, TAX, TAY, TSX, TXA, TXS, TYA, HLT;

    public static Mnemonic fromString(String s) {
        for(Mnemonic mnemonic : Mnemonic.values()) {
            if(mnemonic.toString().equalsIgnoreCase(s)) {
                return mnemonic;
            }
        }
        return null;
    }
}
