/*
 * PM6502Test.java
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

import com.pmeade.cpu.pm6502.util.MemoryBuilder;
import org.junit.*;

import static com.pmeade.cpu.pm6502.Cpu6502.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author pmeade
 */
public class PM6502Test
{
    private Cpu6502 cpu6502;
    
    public PM6502Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        cpu6502 = new PM6502();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testAlwaysSucceed() {
        assertTrue(true);
    }
    
    @Test
    public void testCpuNotNull() {
        assertNotNull(cpu6502);
    }
    
    @Test
    public void testCpuReadsResetVectorOnReset() {
        MemoryIO mem = createMock(MemoryIO.class);
        expect(mem.read(RESET_LO)).andReturn(0x00);
        expect(mem.read(RESET_HI)).andReturn(0x80);
        replay(mem);
        
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        
        verify(mem);
    }
    
    @Test
    public void testCpuRegistersOnReset() {
        MemoryIO mem = createMock(MemoryIO.class);
        expect(mem.read(RESET_LO)).andReturn(0xCD);
        expect(mem.read(RESET_HI)).andReturn(0xAB);
        replay(mem);
        
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xABCD, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_ZERO | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        
        verify(mem);
    }

    // ------------------------------------------------------------------------

    @Test // 0x00
    public void testBrkImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x00) // brk
                .irqAt(0xFACE)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(7, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xFACE, cpu6502.getPC());
        assertEquals(0xFC, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_BREAK | FLAG_INTERRUPT, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xc0, mem.read(0x100 | 0xFF));
        assertEquals(0x02, mem.read(0x100 | 0xFE));
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_BREAK, mem.read(0x100 | 0xFD));
    }

    @Test // 0x06
    public void testAslZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x06, 0x25) // asl $25
                .putAt(0x0025, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xfe, mem.read(0x0025));
    }

    @Test // 0x09
    public void testOraImmediate() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0x09, 0x7f) // ora #$7f
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x7f, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x09
    public void testOraImmediateNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x09, 0x55) // ora #$55
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xff, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x09
    public void testOraImmediateZero() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0x09, 0x00) // ora #$00
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x0A
    public void testAslAccumulator() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xff) // lda #$ff
                .put(0x0a)       // asl a
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xfe, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x0E
    public void testAslAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xff)       // lda #$ff
                .put(0x0e, 0xce, 0xfa) // asl $face
                .putAt(0xface, 0x08)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0xff, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x10, mem.read(0xface));
    }
    
    @Test // 0x10
    public void testBplAccepted() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0x10, 0x12) // bpl +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC016, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x10
    public void testBplAcceptedBack() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0x10, 0xfc) // bpl -4
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC000, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x10
    public void testBplAcceptedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC0E0)
                .put(0xA9, 0x00) // lda #$00
                .put(0x10, 0x40) // bpl +64
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC124, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x10
    public void testBplRejected() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x88) // lda #$88
                .put(0x10, 0x12) // bpl +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x88, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x16
    public void testAslZeroPageX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x16, 0x25) // asl $25,x
                .putAt(0x002a, 0x80)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_CARRY | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x00, mem.read(0x002a));
    }

    @Test // 0x18
    public void testClcImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x18) // cld
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x1E
    public void testAslAbsoluteX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xdead)
                .put(0xA9, 0x00)       // lda #$00
                .put(0x1e, 0xce, 0xfa) // asl $face,x
                .putAt(0xfade, 0x08)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x10);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(7, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xdeb2, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x10, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x10, mem.read(0xfade));
    }
    
    @Test // 0x20
    public void testJsrAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x20, 0xCD, 0xAB) // jsr $abcd
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xabcd, cpu6502.getPC());
        assertEquals(0xFD, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xC0, mem.read(0x1FF));
        assertEquals(0x02, mem.read(0x1FE));
    }
    
    @Test // 0x24
    public void testBitZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xf0) // lda #$f0
                .put(0x24, 0x25) // bit $25
                .putAt(0x0025, 0x0f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0xf0, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x24
    public void testBitZeroPageOverflow() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x0f) // lda #$0f
                .put(0x24, 0x25) // bit $25
                .putAt(0x0025, 0x4f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x0f, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_OVERFLOW, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x24
    public void testBitZeroPageNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x0f) // lda #$0f
                .put(0x24, 0x25) // bit $25
                .putAt(0x0025, 0x8f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x0f, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x29
    public void testAndImmediate() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x29, 0x55) // and #$55
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x29
    public void testAndImmediateNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x29, 0xd5) // and #$d5
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x80, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x2C
    public void testBitAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x0f)       // lda #$0f
                .put(0x2C, 0xcd, 0xab) // bit $abcd
                .putAt(0xabcd, 0xff)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x0f, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE | FLAG_OVERFLOW, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x2C
    public void testBitAbsoluteFullMonty() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x3f)       // lda #$0f
                .put(0x2C, 0xcd, 0xab) // bit $abcd
                .putAt(0xabcd, 0xc0)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x3f, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_NEGATIVE | FLAG_OVERFLOW, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x38
    public void testSecImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x38) // sec
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x46
    public void testLsrZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x46, 0x25) // lsr $25
                .putAt(0x0025, 0xfe)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x7f, mem.read(0x0025));
    }
    
    @Test // 0x4A
    public void testLsrAccumulator() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xff) // lda #$ff
                .put(0x4a)       // lsr a
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x7f, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x4E
    public void testLsrAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xff)       // lda #$ff
                .put(0x4e, 0xce, 0xfa) // lsr $face
                .putAt(0xface, 0x01)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0xff, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x00, mem.read(0xface));
    }

    @Test // 0x56
    public void testLsrZeroPageX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x56, 0x25) // lsr $25,x
                .putAt(0x002a, 0x21)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x10, mem.read(0x002a));
    }

    @Test // 0x5E
    public void testLsrAbsoluteX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xdead)
                .put(0xA9, 0x00)       // lda #$00
                .put(0x5e, 0xce, 0xfa) // lsr $face,x
                .putAt(0xfade, 0x02)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x10);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(7, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xdeb2, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x10, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x01, mem.read(0xfade));
    }

    @Test // 0x60
    public void testRtsImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x20, 0xCD, 0xAB) // jsr $abcd
                .putAt(0xabcd, 0x60)   // rts
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xc003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xC0, mem.read(0x1FF));
        assertEquals(0x02, mem.read(0x1FE));
    }

    @Test // 0x65
    public void testAdcZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)       // cld
                .put(0x18)       // clc
                .put(0xa9, 0xff) // lda #$ff
                .put(0x65, 0x55) // adc $55
                .putAt(0x55, 0xff)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0xfe, cpu6502.getAC());
        assertEquals(0xC006, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_NEGATIVE | FLAG_RESERVED | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x69
    public void testAdcImmediateBcd() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xf8)       // sed
                .put(0x18)       // clc
                .put(0xa9, 0x80) // lda #$80
                .put(0x69, 0x80) // adc #$80
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x60, cpu6502.getAC());
        assertEquals(0xC006, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(  FLAG_OVERFLOW
                     | FLAG_RESERVED
                     | FLAG_DECIMAL
                     | FLAG_ZERO
                     | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x6C
    public void testJmpIndirect() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x6C, 0xCD, 0xAB) // jmp ($abcd)
                .putAt(0xABCD, 0x06, 0x80)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0x8006, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x6D
    public void testAdcAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)             // cld
                .put(0x38)             // sec
                .put(0xa9, 0x7f)       // lda #$7f
                .put(0x6d, 0xce, 0xfa) // adc $face
                .putAt(0xface, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0xff, cpu6502.getAC());
        assertEquals(0xC007, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_NEGATIVE | FLAG_OVERFLOW | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x6D
    public void testAdcAbsolute2() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)             // cld
                .put(0x38)             // sec
                .put(0xa9, 0x80)       // lda #$80
                .put(0x6d, 0xce, 0xfa) // adc $face
                .putAt(0xface, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC007, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x75
    public void testAdcZeroPageX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)       // cld
                .put(0x38)       // sec
                .put(0xa9, 0xff) // lda #$ff
                .put(0x75, 0x55) // adc $55,x
                .putAt(0x65, 0x00)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x10);
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC006, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_CARRY | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x10, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x78
    public void testSeiImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x78) // sei
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_INTERRUPT | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x79
    public void testAdcAbsoluteY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)             // cld
                .put(0x18)             // clc
                .put(0xa9, 0x80)       // lda #$80
                .put(0x79, 0xce, 0xfa) // adc $face,y
                .putAt(0xfb1e, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x50);
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0xff, cpu6502.getAC());
        assertEquals(0xC007, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_NEGATIVE | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x50, cpu6502.getYR());
    }
    
    @Test // 0x7D
    public void testAdcAbsoluteX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xd8)             // cld
                .put(0x38)             // sec
                .put(0xa9, 0x80)       // lda #$80
                .put(0x7d, 0xce, 0xfa) // adc $face,x
                .putAt(0xfb1e, 0x80)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x50);
        cpu6502.execute();
        cpu6502.execute();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x01, cpu6502.getAC());
        assertEquals(0xC007, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_OVERFLOW | FLAG_RESERVED | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x50, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x81
    public void testStaIndirectX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x81, 0x3E) // sta $(3e,x)
                .putAt(0x0043, 0x15)
                .putAt(0x0044, 0x24)
                .putAt(0x2415, 0x55)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0xAA, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xAA, mem.read(0x2415));
    }
    
    @Test // 0x85
    public void testStaZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x85, 0x25) // sta $25
                .putAt(0x0025, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0xAA, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xAA, mem.read(0x0025));
    }

    @Test // 0x86
    public void testStxZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA2, 0xAA) // ldx #$aa
                .put(0x86, 0x25) // stx $25
                .putAt(0x0025, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0xAA, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xAA, mem.read(0x0025));
    }
    
    @Test // 0x88
    public void testDeyImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x88) // dey
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x01);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x88
    public void testDeyImpliedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x88) // dey
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xFF, cpu6502.getYR());
    }

    @Test // 0x8D
    public void testStaAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00)       // lda #$00
                .put(0x8D, 0xCD, 0xAB) // sta $abcd
                .putAt(0xABCD, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x00, mem.read(0xABCD));
    }
    
    @Test // 0x8E
    public void testStxAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA2, 0x00)       // ldx #$00
                .put(0x8e, 0xCD, 0xAB) // stx $abcd
                .putAt(0xABCD, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x00, mem.read(0xABCD));
    }

    @Test // 0x90
    public void testBccAccepted() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x90, 0x12) // bcc +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC014, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0x90
    public void testBccAcceptedBack() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC080)
                .put(0x90, 0xfc) // bcc -4
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xc07e, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x90
    public void testBccAcceptedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC0E0)
                .put(0x90, 0x40) // bcc +64
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC122, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x90
    public void testBccRejected() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x90, 0x12) // bcc +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x91
    public void testStaIndirectY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xAA) // lda #$aa
                .put(0x91, 0x4C) // lda $(4c),y
                .putAt(0x004C, 0x00)
                .putAt(0x004D, 0x21)
                .putAt(0x2105, 0x55)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x05);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0xAA, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x05, cpu6502.getYR());
        assertEquals(0xAA, mem.read(0x2105));
    }
    
    @Test // 0x95
    public void testStaZeroPageX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x55) // lda #$55
                .put(0x95, 0x25) // sta $25,x
                .putAt(0x002A, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x55, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0x55, mem.read(0x002A));
    }

    @Test // 0x96
    public void testStxZeroPageY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA2, 0x55) // ldx #$55
                .put(0x96, 0x25) // stx $25,x
                .putAt(0x002A, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x05);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x55, cpu6502.getXR());
        assertEquals(0x05, cpu6502.getYR());
        assertEquals(0x55, mem.read(0x002A));
    }

    @Test // 0x98
    public void testTyaImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x98) // tya
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x55);
        cpu6502.setAC(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x55, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x55, cpu6502.getYR());
    }

    @Test // 0x98
    public void testTyaImpliedNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x98) // tya
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0xce);
        cpu6502.setAC(0x00);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xce, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_NEGATIVE | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xce, cpu6502.getYR());
    }

    @Test // 0x98
    public void testTyaImpliedZero() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x98) // tya
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x00);
        cpu6502.setAC(0xce);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x99
    public void testStaAbsoluteY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xB000)
                .put(0xA9, 0x72)       // lda #$72
                .put(0x99, 0x15, 0x20) // sta $2015,y
                .putAt(0x2041, 0x8D)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x2C);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x72, cpu6502.getAC());
        assertEquals(0xB005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x2C, cpu6502.getYR());
        assertEquals(0x72, mem.read(0x2041));
    }

    @Test // 0x9A
    public void testTxsImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0x9a) // txs
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0x80, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x80, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0x9D
    public void testStaAbsoluteX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xEA)       // lda #$ea
                .put(0x9D, 0xAB, 0xCD) // sta $cdab,x
                .putAt(0xCDB0, 0x88)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0xEA, cpu6502.getAC());
        assertEquals(0xC005, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xEA, mem.read(0xCDB0));
    }

    @Test // 0xA0
    public void testLdyImmediate() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA0, 0xD2) // ldy #$d2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xd2, cpu6502.getYR());
    }
    
    @Test // 0xA1
    public void testLdaIndirectX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA1, 0x3E) // lda $(3e,x)
                .putAt(0x0043, 0x15)
                .putAt(0x0044, 0x24)
                .putAt(0x2415, 0x6E)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x05);
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x6E, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x05, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xA2
    public void testLdxImmediate() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA2, 0xD2) // ldx #$d2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0xD2, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xA5
    public void testLdaZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA5, 0x25) // lda $25
                .putAt(0x0025, 0x55)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x55, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xA9
    public void testLdaImmediate() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xD2) // lda #$d2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xD2, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xAA
    public void testTaxImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xaa) // tax
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setAC(0x55);
        cpu6502.setXR(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x55, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x55, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xAA
    public void testTaxImpliedNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xaa) // tax
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setAC(0xea);
        cpu6502.setXR(0x55);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xea, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_NEGATIVE | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0xea, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xAA
    public void testTaxImpliedZero() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xaa) // tax
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setAC(0x00);
        cpu6502.setXR(0x55);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xff, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xAD
    public void testLdaAbsolute() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xAD, 0x20, 0xD0) // lda $d020
                .putAt(0xD019, 0xFF, 0x00, 0xFF)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setAC(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_ZERO | FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xB0
    public void testBcsAccepted() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xb0, 0x12) // bcs +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC014, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xB0
    public void testBcsAcceptedBack() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC080)
                .put(0xb0, 0xfc) // bcs -4
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xc07e, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xB0
    public void testBcsAcceptedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC0E0)
                .put(0xb0, 0x40) // bcs +64
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC122, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xB0
    public void testBcsRejected() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xb0, 0x12) // bcs +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() & ~FLAG_CARRY);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xB1
    public void testLdaIndirectY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB1, 0x4C) // lda $(4c),y
                .putAt(0x004C, 0x00)
                .putAt(0x004D, 0x21)
                .putAt(0x2105, 0x6D)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x05);
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x6D, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x05, cpu6502.getYR());
    }

    @Test // 0xB1
    public void testLdaIndirectYWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB1, 0x4C) // lda $(4c),y
                .putAt(0x004C, 0x80)
                .putAt(0x004D, 0x21)
                .putAt(0x2245, 0x9D)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0xC5);
        int cycles = cpu6502.execute();
        
        assertEquals(6, cycles);
        assertEquals(0x9D, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xC5, cpu6502.getYR());
    }
    
    @Test // 0xB5
    public void testLdaZeroPageX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB5, 0x25) // lda $25,x
                .putAt(0x0028, 0x05)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x03);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x05, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x03, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xB5
    public void testLdaZeroPageXWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB5, 0xFE) // lda $fe,x
                .putAt(0x000A, 0xBB)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x0C);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0xBB, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x0C, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xB6
    public void testLdxZeroPageY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB6, 0x25) // ldx $25,y
                .putAt(0x0028, 0x55)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x03);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x55, cpu6502.getXR());
        assertEquals(0x03, cpu6502.getYR());
    }
    
    @Test // 0xB9
    public void testLdaAbsoluteY() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB9, 0x00, 0x25) // lda $2500,y
                .putAt(0x2580, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x7f, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x80, cpu6502.getYR());
    }

    @Test // 0xB9
    public void testLdaAbsoluteYWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xB9, 0x80, 0x25) // lda $2580,y
                .putAt(0x2640, 0x80)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0xC0);
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x80, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xC0, cpu6502.getYR());
    }
    
    @Test // 0xBD
    public void testLdaAbsoluteX() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xBD, 0x00, 0x25) // lda $2500,x
                .putAt(0x2580, 0x7f)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x80);
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x7f, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x80, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xBD
    public void testLdaAbsoluteXWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xBD, 0x80, 0x25) // lda $2580,x
                .putAt(0x2640, 0x80)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0xC0);
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x80, cpu6502.getAC());
        assertEquals(0xC003, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0xC0, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xC0
    public void testCpyImmediateEqual() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA0, 0xD2) // ldy #$d2
                .put(0xC0, 0xD2) // cpy #$d2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0xd2, cpu6502.getYR());
    }
    
    @Test // 0xC8
    public void testInyImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xc8) // iny
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x00);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x01, cpu6502.getYR());
    }

    @Test // 0xC8
    public void testInyImpliedNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xc8) // iny
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0x7f);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x80, cpu6502.getYR());
    }
    
    @Test // 0xC8
    public void testInyImpliedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xc8) // iny
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setYR(0xff);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xC9
    public void testCmpImmediateEqual() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xD2) // lda #$d2
                .put(0xC9, 0xD2) // cmp #$d2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xD2, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xC9
    public void testCmpImmediateLess() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xD2) // lda #$d2
                .put(0xC9, 0xC2) // cmp #$c2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xD2, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xC9
    public void testCmpImmediateMore() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0xd2) // lda #$d2
                .put(0xC9, 0xe2) // cmp #$e2
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0xd2, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE | FLAG_CARRY, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xC9
    public void testCmpImmediateNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x90) // lda #$90
                .put(0xC9, 0x01) // cmp #$01
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x90, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xCA
    public void testDexImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xCA) // dex
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x01);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xCA
    public void testDexImpliedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xCA) // dex
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0xFF, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xD0
    public void testBneAccepted() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x01) // lda #$01
                .put(0xD0, 0x12) // bne +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x01, cpu6502.getAC());
        assertEquals(0xC016, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xD0
    public void testBneAcceptedBack() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x01) // lda #$01
                .put(0xD0, 0xfc) // beq -4
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x01, cpu6502.getAC());
        assertEquals(0xC000, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xD0
    public void testBneAcceptedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC0E0)
                .put(0xA9, 0x01) // lda #$01
                .put(0xD0, 0x40) // beq +64
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x01, cpu6502.getAC());
        assertEquals(0xC124, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xD0
    public void testBneRejected() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0xD0, 0x12) // beq +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xD8
    public void testCldImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xD8) // cld
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setSR(cpu6502.getSR() | FLAG_DECIMAL);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xe6
    public void testIncZeroPage() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xe6, 0x25) // inc $25
                .putAt(0x0025, 0xfe)
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(5, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC002, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
        assertEquals(0xff, mem.read(0x0025));
    }

    @Test // 0xE8
    public void testInxImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xE8) // inx
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x00);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x01, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xE8
    public void testInxImpliedNegative() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xE8) // inx
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0x7f);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_NEGATIVE, cpu6502.getSR());
        assertEquals(0x80, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xE8
    public void testInxImpliedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xE8) // inx
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.setXR(0xff);
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xF0
    public void testBeqAccepted() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0xF0, 0x12) // beq +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC016, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }

    @Test // 0xF0
    public void testBeqAcceptedBack() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x00) // lda #$00
                .put(0xF0, 0xfc) // beq -4
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(3, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC000, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xF0
    public void testBeqAcceptedWrap() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC0E0)
                .put(0xA9, 0x00) // lda #$00
                .put(0xF0, 0x40) // beq +64
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(4, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC124, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xF0
    public void testBeqRejected() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xA9, 0x01) // lda #$01
                .put(0xF0, 0x12) // beq +18
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        cpu6502.execute();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x01, cpu6502.getAC());
        assertEquals(0xC004, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
    
    @Test // 0xf8
    public void testSedImplied() {
        MemoryIO mem = new MemoryBuilder().startAt(0xC000)
                .put(0xf8) // sed
                .create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        int cycles = cpu6502.execute();
        
        assertEquals(2, cycles);
        assertEquals(0x00, cpu6502.getAC());
        assertEquals(0xC001, cpu6502.getPC());
        assertEquals(0xFF, cpu6502.getSP());
        assertEquals(FLAG_RESERVED | FLAG_DECIMAL | FLAG_ZERO, cpu6502.getSR());
        assertEquals(0x00, cpu6502.getXR());
        assertEquals(0x00, cpu6502.getYR());
    }
}
