/*
 * MemoryBuilderTest.java
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
import org.junit.*;

import static com.pmeade.cpu.pm6502.Cpu6502.*;
import static org.junit.Assert.*;

/**
 * @author pmeade
 */
public class MemoryBuilderTest
{
    private MemoryBuilder memoryBuilder;
    
    public MemoryBuilderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        memoryBuilder = new MemoryBuilder();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAlwaysSucceed() {
        assertTrue(true);
    }
    
    @Test
    public void testMemoryBuilderNotNull() {
        assertNotNull(memoryBuilder);
    }
    
    @Test
    public void testCreate() {
        MemoryIO mem = memoryBuilder.create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            assertEquals(0x00, mem.read(i));
        }
    }
    
    @Test
    public void testStartAt() {
        MemoryIO mem = memoryBuilder.startAt(0xABCD).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == RESET_LO) {
                assertEquals(0xCD, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0xAB, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testPutAtLength1() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0xEA).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0xEA, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testPutAtLength2() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0xA9, 0x80).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0xA9, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0x80, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testPutAtLength3() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0x20, 0xD2, 0xFF).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0x20, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0xD2, mem.read(i));
            }
            else if(i == 0xC002) {
                assertEquals(0xFF, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testPutLength1() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0xEA).put(0x58).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0xEA, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0x58, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }

    @Test
    public void testPutLength2() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0xEA).put(0xA9,0x80).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0xEA, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0xA9, mem.read(i));
            }
            else if(i == 0xC002) {
                assertEquals(0x80, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testPutLength3() {
        MemoryIO mem = memoryBuilder.putAt(0xC000, 0xEA).put(0x20,0xD2,0xFF).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0xEA, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0x20, mem.read(i));
            }
            else if(i == 0xC002) {
                assertEquals(0xD2, mem.read(i));
            }
            else if(i == 0xC003) {
                assertEquals(0xFF, mem.read(i));
            }
            else { 
                assertEquals(0x00, mem.read(i));
            }
        }
    }

    @Test
    public void testPutFollowingStart() {
        MemoryIO mem = memoryBuilder.startAt(0xC000).put(0x20,0xD2,0xFF).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xC000) {
                assertEquals(0x20, mem.read(i));
            }
            else if(i == 0xC001) {
                assertEquals(0xD2, mem.read(i));
            }
            else if(i == 0xC002) {
                assertEquals(0xFF, mem.read(i));
            }
            else if(i == RESET_LO) {
                assertEquals(0x00, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0xC0, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testStartBeforeOverridesPut() {
        MemoryIO mem = memoryBuilder.startAt(0x80EF).putAt(RESET_LO,0xAB,0xCD).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == RESET_LO) {
                assertEquals(0xEF, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0x80, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }

    @Test
    public void testStartAfterOverridesPut() {
        MemoryIO mem = memoryBuilder.putAt(RESET_LO,0xAB,0xCD).startAt(0x80EF).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == RESET_LO) {
                assertEquals(0xEF, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0x80, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }

    @Test
    public void testLoadAt() {
        File binaryFile = new File("src/test/resources/256.bin");
        assertTrue(binaryFile.exists());
        assertTrue(binaryFile.canRead());
        MemoryIO mem = memoryBuilder.loadAt(0xC000,binaryFile).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if((i >= 0xC000) && (i < 0xC100)) {
                assertEquals((i & 0xff), mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testLoad() {
        File binaryFile = new File("src/test/resources/256.bin");
        assertTrue(binaryFile.exists());
        assertTrue(binaryFile.canRead());
        MemoryIO mem = memoryBuilder.startAt(0xCFFF).put(0xEA).load(binaryFile).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xCFFF) {
                assertEquals(0xEA, mem.read(i));
            }
            else if((i >= 0xD000) && (i < 0xD100)) {
                assertEquals((i & 0xff), mem.read(i));
            }
            else if(i == RESET_LO) {
                assertEquals(0xFF, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0xCF, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testFullMutability() {
        MemoryIO mem = memoryBuilder.startAt(0x8000).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == RESET_LO) {
                assertEquals(0x00, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0x80, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
        for(int i=0; i<0x10000; i++) {
            for(int j=0; j<0x100; j++) {
                mem.write(i, j);
                assertEquals(j, mem.read(i));
            }
        }
    }
    
    @Test
    public void testIrqAt() {
        MemoryIO mem = memoryBuilder.irqAt(0xABCD).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == IRQ_LO) {
                assertEquals(0xCD, mem.read(i));
            }
            else if(i == IRQ_HI) {
                assertEquals(0xAB, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }

    @Test
    public void testLoadAtArray() throws Exception {
        File binaryFile = new File("src/test/resources/256.bin");
        assertTrue(binaryFile.exists());
        assertTrue(binaryFile.canRead());
        
        int[] data = new int[256];
        FileInputStream fis = new FileInputStream(binaryFile);
        for(int i=0; i<256; i++) {
            data[i] = fis.read();
        }
        fis.close();
        
        MemoryIO mem = memoryBuilder.loadAt(0xC000,data).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if((i >= 0xC000) && (i < 0xC100)) {
                assertEquals((i & 0xff), mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testLoadArray() throws Exception {
        File binaryFile = new File("src/test/resources/256.bin");
        assertTrue(binaryFile.exists());
        assertTrue(binaryFile.canRead());
        
        int[] data = new int[256];
        FileInputStream fis = new FileInputStream(binaryFile);
        for(int i=0; i<256; i++) {
            data[i] = fis.read();
        }
        fis.close();
        
        MemoryIO mem = memoryBuilder.startAt(0xCFFF).put(0xEA).load(data).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == 0xCFFF) {
                assertEquals(0xEA, mem.read(i));
            }
            else if((i >= 0xD000) && (i < 0xD100)) {
                assertEquals((i & 0xff), mem.read(i));
            }
            else if(i == RESET_LO) {
                assertEquals(0xFF, mem.read(i));
            }
            else if(i == RESET_HI) {
                assertEquals(0xCF, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
    
    @Test
    public void testLoadAtArrayNull() throws Exception {
        int[] data = null;
        MemoryIO mem = memoryBuilder.loadAt(0xC000, data).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            assertEquals(0x00, mem.read(i));
        }
    }
    
    @Test
    public void testNmiAt() {
        MemoryIO mem = memoryBuilder.nmiAt(0xABCD).create();
        assertNotNull(mem);
        for(int i=0; i<0x10000; i++) {
            if(i == NMI_LO) {
                assertEquals(0xCD, mem.read(i));
            }
            else if(i == NMI_HI) {
                assertEquals(0xAB, mem.read(i));
            }
            else {
                assertEquals(0x00, mem.read(i));
            }
        }
    }
}
