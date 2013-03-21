/*
 * AddressMode.java
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
public enum AddressMode
{
    BAD("???"),
    ABS("Absolute"),
    ABX("Absolute,X"),
    ABY("Absolute,Y"),
    ACC("Accumulator"),
    IDX("(Indirect,X)"),
    IDY("(Indirect),Y"),
    IMM("Immediate"),
    IMP("Implied"),
    IND("Indirect"),
    REL("Relative"),
    ZPG("Zero Page"),
    ZPX("Zero Page,X"),
    ZPY("Zero Page,Y");

    public static AddressMode fromString(String addressMode) {
        for(AddressMode addrMode : AddressMode.values()) {
            if(addrMode.addressMode.equalsIgnoreCase(addressMode)) {
                return addrMode;
            }
        }
        return null;
    }
    
    private AddressMode(String addressMode) {
        this.addressMode = addressMode;
    }
    
    private String addressMode;
}
