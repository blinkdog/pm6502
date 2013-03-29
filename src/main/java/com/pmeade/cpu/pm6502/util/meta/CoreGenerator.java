/*
 * CoreGenerator.java
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

public class CoreGenerator
{
    public static void main(String[] args) {
        CoreGenerator coreGenerator = new CoreGenerator();
        coreGenerator.run();
    }
    
    public void run() {
        printCore();
    }
    
    private void printCore()
    {
        System.out.println("/* Gen6502.java */\n");
        System.out.println("package com.pmeade.cpu.pm6502.util.meta;\n");
        System.out.println("abstract public class Gen6502");
        System.out.println("{");
        printDispatchSignature();
        printDispatch(0,7);
        printDispatchClosing();
        System.out.println("");
        printMethodSignatures();
        System.out.println("}");
    }
    
    private void printMethodSignatures()
    {
        for(int i=0; i<0x100; i++) {
            System.out.print("    abstract void do");
            if(i < 0x10) { System.out.print("0"); }
            System.out.print(Integer.toHexString(i).toUpperCase());
            System.out.println("();");
        }
    }

    private void printDispatchSignature()
    {
        System.out.println("    public void dispatch(int opcode)");
        System.out.println("    {");
        System.out.println("        boolean b0 = ((opcode & 0x01) == 0x01);");
        System.out.println("        boolean b1 = ((opcode & 0x02) == 0x02);");
        System.out.println("        boolean b2 = ((opcode & 0x04) == 0x04);");
        System.out.println("        boolean b3 = ((opcode & 0x08) == 0x08);");
        System.out.println("        boolean b4 = ((opcode & 0x10) == 0x10);");
        System.out.println("        boolean b5 = ((opcode & 0x20) == 0x20);");
        System.out.println("        boolean b6 = ((opcode & 0x40) == 0x40);");
        System.out.println("        boolean b7 = ((opcode & 0x80) == 0x80);");
    }

    private void printDispatchClosing()
    {
        System.out.println("    }");
    }

    private void printDispatch(int opcode, int level)
    {
        int indentLevel = (7-level)+2;
        for(int i=0; i<indentLevel; i++) { System.out.print("    "); }
        System.out.println("if(b" + level + ") {");
            if(level == 0) {
                opcode = (opcode << 1)+1;
                for(int i=0; i<indentLevel+1; i++) { System.out.print("    "); }
                System.out.print("do");
                if(opcode < 0x10) { System.out.print("0"); }
                System.out.print(Integer.toHexString(opcode).toUpperCase());
                System.out.println("();");
            } else {
                printDispatch((opcode << 1)+1, level-1);
            }
        for(int i=0; i<indentLevel; i++) { System.out.print("    "); }
        System.out.println("} else {");
            if(level == 0) {
                opcode = opcode-1;
                for(int i=0; i<indentLevel+1; i++) { System.out.print("    "); }
                System.out.print("do");
                if(opcode < 0x10) { System.out.print("0"); }
                System.out.print(Integer.toHexString(opcode).toUpperCase());
                System.out.println("();");
            } else {
                printDispatch((opcode << 1), level-1);
            }
        for(int i=0; i<indentLevel; i++) { System.out.print("    "); }
        System.out.println("}");
    }
}
