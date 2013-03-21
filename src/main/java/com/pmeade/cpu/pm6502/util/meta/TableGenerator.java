/*
 * TableGenerator.java
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

package com.pmeade.cpu.pm6502.util.meta;

import com.google.common.base.Predicate;
import com.pmeade.cpu.pm6502.AddressMode;
import com.pmeade.cpu.pm6502.Cpu6502;
import com.pmeade.cpu.pm6502.MemoryIO;
import com.pmeade.cpu.pm6502.Mnemonic;
import com.pmeade.cpu.pm6502.util.MemoryBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;


/**
 * @author pmeade
 */
public class TableGenerator implements Runnable
{
    public static void main(String[] args)
    {
        File doc = new File("doc/6502.txt");
        if(doc.exists() == false) {
            System.err.println("File not found: doc/6502.txt");
            System.exit(-1);
        }
        if(doc.canRead() == false) {
            System.err.println("Unable to read: doc/6502.txt");
            System.exit(-1);
        }
        
        TableGenerator tableGenerator = new TableGenerator(doc);
        tableGenerator.run();
    }
    
    public TableGenerator(File doc) {
        this.doc = doc;
    }

    public void run() {
        List<String> docLines = readDoc();
        List<String> opcodeLines = newArrayList(filter(docLines, new Predicate<String>() {
            public boolean apply(String t) {
                return OPCODE_INFO.matcher(t).matches();
            }
        }));
        
//        List<String> addressingModes = getAddressingModes(opcodeLines);
//            for(String addrMode : addressingModes) {
//                System.out.println(addrMode);
//            }
//        List<String> mnemonics = getMnemonics(opcodeLines);
//            for(String mnemonic : mnemonics) {
//                System.out.println(mnemonic);
//            }
//            System.out.println(mnemonics.size());
//        List<String> opcodes = getOpcodes(opcodeLines);
//        List<String> lengths = getLengths(opcodeLines);
//        List<String> cycles = getCycles(opcodeLines);
        
        List<OpcodeInfo> opcodes = getOpcodeInfo(opcodeLines);
//        printCycleTable(opcodes);
//        printLengthTable(opcodes);
//        printExtraCycleTable(opcodes);
//        printMnemonicTable(opcodes);
//        printMnemonicTableEnum(opcodes);
//        printAddressModeTable(opcodes);
//        printAddressModeTableEnum(opcodes);
//        printAddressModesWithExtraCycle(opcodes);
        printBadOpcodeTests();
    }

    private List<String> getAddressingModes(List<String> opcodeLines) {
        List<String> addressingModes = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String addressingMode = matcher.group(1).trim();
                if(addressingModes.contains(addressingMode) == false) {
                    addressingModes.add(addressingMode);
                }
            }
        }
        Collections.sort(addressingModes);
        return addressingModes;
    }

    private List<String> getMnemonics(List<String> opcodeLines) {
        List<String> mnemonics = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String asmForm = matcher.group(2).trim();
                String[] asmFormWords = asmForm.split(" ");
                if(mnemonics.contains(asmFormWords[0]) == false) {
                    mnemonics.add(asmFormWords[0]);
                }
            }
        }
        Collections.sort(mnemonics);
        return mnemonics;
    }

    private List<String> getOpcodes(List<String> opcodeLines) {
        List<String> opcodes = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String opcode = matcher.group(3).trim();
                if(opcodes.contains(opcode)) {
                    throw new IllegalStateException("Duplicate " + opcode);
                } else {
                    opcodes.add(opcode);
                }
            }
        }
        Collections.sort(opcodes);
        return opcodes;
    }

    private List<String> getLengths(List<String> opcodeLines) {
        List<String> lengths = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String length = matcher.group(4).trim();
                if(lengths.contains(length) == false) {
                    lengths.add(length);
                }
            }
        }
        Collections.sort(lengths);
        return lengths;
    }

    private List<String> getCycles(List<String> opcodeLines) {
        List<String> cycles = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String cycle = matcher.group(5).trim();
                if(cycles.contains(cycle) == false) {
                    cycles.add(cycle);
                }
            }
        }
        Collections.sort(cycles);
        return cycles;
    }

    private List<OpcodeInfo> getOpcodeInfo(List<String> opcodeLines) {
        List<OpcodeInfo> opcodes = new ArrayList();
        for(String opcodeLine : opcodeLines) {
            Matcher matcher = OPCODE_INFO.matcher(opcodeLine);
            if(matcher.matches()) {
                String asmForm = matcher.group(2).trim();
                String[] asmFormWords = asmForm.split(" ");
                boolean extraCycle = false;

                if("*".equalsIgnoreCase(matcher.group(6))) {
                    extraCycle = true;
                }

                OpcodeInfo opcodeInfo = new OpcodeInfo();
                opcodeInfo.addressMode = matcher.group(1).trim();
                opcodeInfo.mnemonic = asmFormWords[0];
                opcodeInfo.asmForm = asmForm;
                opcodeInfo.opcode = Integer.valueOf(matcher.group(3).trim(), 16);
                opcodeInfo.length = Integer.valueOf(matcher.group(4).trim());
                opcodeInfo.cycles = Integer.valueOf(matcher.group(5).trim());
                opcodeInfo.extraCycle = extraCycle;
                
                opcodes.add(opcodeInfo);
            }
        }
        Collections.sort(opcodes, new Comparator<OpcodeInfo>() {
            public int compare(OpcodeInfo o1, OpcodeInfo o2) {
                return Integer.valueOf(o1.opcode).compareTo(Integer.valueOf(o2.opcode));
            }
        });
        return opcodes;
    }

    private void printCycleTable(List<OpcodeInfo> opcodes) {
        int[] cycles = new int[256];
        for(OpcodeInfo opcode : opcodes) {
            cycles[opcode.opcode]=opcode.cycles;
        }
        System.out.println("\tpublic static final int[] CYCLES = {");
        for(int i=0; i<16; i++) {
            System.out.print("\t\t");
            for(int j=0; j<16; j++) {
                System.out.print(cycles[i*16+j]);
                if(j < 15) { System.out.print(", "); }
            }
            if(i < 15) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printLengthTable(List<OpcodeInfo> opcodes) {
        int[] length = new int[256];
        for(OpcodeInfo opcode : opcodes) {
            length[opcode.opcode]=opcode.length;
        }
        System.out.println("\tpublic static final int[] LENGTH = {");
        for(int i=0; i<16; i++) {
            System.out.print("\t\t");
            for(int j=0; j<16; j++) {
                System.out.print(length[i*16+j]);
                if(j < 15) { System.out.print(", "); }
            }
            if(i < 15) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printExtraCycleTable(List<OpcodeInfo> opcodes) {
        boolean[] extraCycles = new boolean[256];
        for(OpcodeInfo opcode : opcodes) {
            extraCycles[opcode.opcode]=opcode.extraCycle;
        }
        System.out.println("\tpublic static final boolean[] EXTRA_CYCLES = {");
        for(int i=0; i<32; i++) {
            System.out.print("\t\t");
            for(int j=0; j<8; j++) {
                System.out.print(extraCycles[i*8+j]);
                if(j < 7) { System.out.print(", "); }
                if(extraCycles[i*8+j]) { System.out.print(" "); }
            }
            if(i < 31) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printMnemonicTable(List<OpcodeInfo> opcodes) {
        String[] mnemonics = new String[256];
        for(OpcodeInfo opcode : opcodes) {
            mnemonics[opcode.opcode]=opcode.mnemonic;
        }
        System.out.println("\tpublic static final String[] MNEMONIC = {");
        for(int i=0; i<32; i++) {
            System.out.print("\t\t");
            for(int j=0; j<8; j++) {
                if(mnemonics[i*8+j] == null) { System.out.print("\"\""); }
                else { System.out.print("\"" + mnemonics[i*8+j] + "\""); }
                if(j < 7) { System.out.print(", "); }
                if(mnemonics[i*8+j] == null && (j != 3) && (j != 7))
                { System.out.print("   "); }
            }
            if(i < 31) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printMnemonicTableEnum(List<OpcodeInfo> opcodes) {
        Mnemonic[] mnemonics = new Mnemonic[256];
        for(OpcodeInfo opcode : opcodes) {
            mnemonics[opcode.opcode]=Mnemonic.fromString(opcode.mnemonic);
        }
        System.out.println("\tpublic static final Mnemonic[] MNEMONIC = {");
        for(int i=0; i<32; i++) {
            System.out.print("\t\t");
            for(int j=0; j<8; j++) {
                if(mnemonics[i*8+j] == null) { System.out.print("null"); }
                else { System.out.print(mnemonics[i*8+j]); }
                if(j < 7) { System.out.print(", "); }
                if(mnemonics[i*8+j] != null)
                { System.out.print(" "); }
            }
            if(i < 31) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }
    
    private void printAddressModeTable(List<OpcodeInfo> opcodes) {
        String[] addressModes = new String[256];
        for(OpcodeInfo opcode : opcodes) {
            addressModes[opcode.opcode]=opcode.addressMode;
        }
        System.out.println("\tpublic static final String[] ADDRESS_MODES = {");
        for(int i=0; i<64; i++) {
            System.out.print("\t\t");
            for(int j=0; j<4; j++) {
                if(addressModes[i*4+j] == null) { System.out.print("\"\""); }
                else {
                    String quoted = "\"" + addressModes[i*4+j] + "\"";
                    System.out.print(quoted);
                }
                if(j < 3) { System.out.print(", "); }
                if(addressModes[i*4+j] != null) {
                    for(int k=0; k<(12-(addressModes[i*4+j].length())); k++) {
                        System.out.print(" ");
                    }
                } else {
                    if(j < 3) {
                        System.out.print("            ");
                    }
                }
            }
            if(i < 63) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printAddressModeTableEnum(List<OpcodeInfo> opcodes) {
        AddressMode[] addressModes = new AddressMode[256];
        for(OpcodeInfo opcode : opcodes) {
            addressModes[opcode.opcode]=AddressMode.fromString(opcode.addressMode);
        }
        System.out.println("\tpublic static final AddressMode[] ADDRESS_MODES = {");
        for(int i=0; i<32; i++) {
            System.out.print("\t\t");
            for(int j=0; j<8; j++) {
                if(addressModes[i*8+j] == null) { System.out.print("null"); }
                else { System.out.print(addressModes[i*8+j]); }
                if(j < 7) { System.out.print(", "); }
                if(addressModes[i*8+j] != null) { System.out.print(" "); }
            }
            if(i < 31) { System.out.print(","); }
            System.out.println();
        }
        System.out.println("\t};");
    }

    private void printAddressModesWithExtraCycle(List<OpcodeInfo> opcodes) {
        List<String> addressModes = new ArrayList();
        for(OpcodeInfo opcode : opcodes) {
            if(opcode.extraCycle) {
                if(addressModes.contains(opcode.addressMode) == false) {
                    addressModes.add(opcode.addressMode);
                }
            }
        }
        for(String addressMode : addressModes) {
            System.out.println(addressMode);
        }
        System.out.println("Size: " + addressModes.size());
    }

    private void printBadOpcodeTests() {
        for(int i=0; i<0x100; i++) {
            Mnemonic m = Cpu6502.MNEMONIC[i];
            if(Mnemonic.HLT.equals(m)) {
                String hexI = Integer.toHexString(i);
                if(i < 0x10) {
                    hexI = "0" + Integer.toHexString(i);
                }
                hexI = hexI.toUpperCase();
                System.out.println("    @Test // 0x"+hexI);
                System.out.println("    public void testBad"+hexI+"() {");
                System.out.println("        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x"+hexI+").create();");
                System.out.println("        cpu6502.setMemoryIO(mem);");
                System.out.println("        cpu6502.reset();");
                System.out.println("        try {");
                System.out.println("            cpu6502.execute();");
                System.out.println("            fail();");
                System.out.println("        } catch(UnsupportedOperationException e) {");
                System.out.println("            // expected");
                System.out.println("        }");
                System.out.println("    }");
                System.out.println("");
            }
        }
    }
    
    private List<String> readDoc() {
        List<String> docLines = new ArrayList();
        
        try {
            FileReader fileReader = new FileReader(doc);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line != null) {
                docLines.add(line);
                line = bufferedReader.readLine();
            }
        } catch(Exception e) {
            // not the most elegant solution...
            throw new RuntimeException(e);
        }
        
        return docLines;
    }
    
    private File doc;
    
    private static final String OPCODE_PATTERN =
        "\\s*\\|([^\\|]+)\\s*\\|([^\\|]+)\\s*\\|\\s*(\\p{XDigit}+)\\s*\\|\\s*(\\d+)\\s*\\|\\s*(\\d+)(\\*)*\\s*\\|";
    private static final Pattern OPCODE_INFO = Pattern.compile(OPCODE_PATTERN);
}

class OpcodeInfo
{
    public String addressMode;
    public String mnemonic;
    public String asmForm;
    public int opcode;
    public int length;
    public int cycles;
    public boolean extraCycle;
}
