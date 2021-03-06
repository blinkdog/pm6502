/*
 * MemoryBuilder.java
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

package com.pmeade.cpu.pm6502.util;

import com.pmeade.cpu.pm6502.MemoryIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.pmeade.cpu.pm6502.Cpu6502.*;

/**
 * @author pmeade
 */
public class MemoryBuilder
{
    public MemoryBuilder() {
        memory = new int[0x10000];
    }
    
    public MemoryIO create() {
        if(nmiAddrSet) {
            memory[NMI_LO] = (nmiAddr & 0xff);
            memory[NMI_HI] = ((nmiAddr & 0xff00) >> 8);
        }
        
        if(resetAddrSet) {
            memory[RESET_LO] = (resetAddr & 0xff);
            memory[RESET_HI] = ((resetAddr & 0xff00) >> 8);
        }
        
        if(irqAddrSet) {
            memory[IRQ_LO] = (irqAddr & 0xff);
            memory[IRQ_HI] = ((irqAddr & 0xff00) >> 8);
        }
        
        return new MemoryIO() {
            private int[] localMemory = memory;
            
            public int read(int address) {
                return (localMemory[address] & 0xff);
            }

            public void write(int address, int data) {
                localMemory[address] = (data & 0xff);
            }
        };
    }

    public MemoryBuilder irqAt(int address) {
        putAddr = address;
        irqAddr = address;
        irqAddrSet = true;
        return this;
    }
    
    public MemoryBuilder load(File binaryFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(binaryFile);
            int data = fileInputStream.read();
            while(data != -1) {
                memory[putAddr] = data; putAddr++;
                data = fileInputStream.read();
            }
        } catch(IOException e) {
            // not the most elegant solution...
            throw new RuntimeException(e);
        }
        return this;
    }

    public MemoryBuilder load(int[] data) {
        if(data != null) {
            for(int i=0; i<data.length; i++) {
                memory[putAddr] = data[i];
                putAddr++;
            }
        }
        return this;
    }
    
    public MemoryBuilder loadAt(int address, File binaryFile) {
        putAddr = address;
        return load(binaryFile);
    }

    public MemoryBuilder loadAt(int address, int[] data) {
        putAddr = address;
        return load(data);
    }

    public MemoryBuilder nmiAt(int address) {
        putAddr = address;
        nmiAddr = address;
        nmiAddrSet = true;
        return this;
    }
    
    public MemoryBuilder put(int... data) {
        for(int i : data) {
            memory[putAddr] = i; putAddr++;
        }
        return this;
    }

    public MemoryBuilder putAt(int address, int... data) {
        putAddr = address;
        return put(data);
    }

    public MemoryBuilder startAt(int address) {
        putAddr = address;
        resetAddr = address;
        resetAddrSet = true;
        return this;
    }
    
    private int[] memory;
    private int irqAddr;
    private boolean irqAddrSet;
    private int nmiAddr;
    private boolean nmiAddrSet;
    private int putAddr;
    private int resetAddr;
    private boolean resetAddrSet;
}
