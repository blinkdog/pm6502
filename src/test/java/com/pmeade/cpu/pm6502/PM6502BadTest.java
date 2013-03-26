/*
 * PM6502BadTest.java
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author pmeade
 */
public class PM6502BadTest
{
    private Cpu6502 cpu6502;
    
    public PM6502BadTest() {
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

    @Test // 0x02
    public void testBad02() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x02).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
    
    @Test // 0x03
    public void testBad03() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x03).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x04
    public void testBad04() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x04).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x07
    public void testBad07() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x07).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x0B
    public void testBad0B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x0B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x0C
    public void testBad0C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x0C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x0F
    public void testBad0F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x0F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x12
    public void testBad12() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x12).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x13
    public void testBad13() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x13).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x14
    public void testBad14() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x14).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x17
    public void testBad17() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x17).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x1A
    public void testBad1A() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x1A).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x1B
    public void testBad1B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x1B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x1C
    public void testBad1C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x1C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x1F
    public void testBad1F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x1F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x22
    public void testBad22() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x22).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x23
    public void testBad23() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x23).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x27
    public void testBad27() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x27).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x2B
    public void testBad2B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x2B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x2F
    public void testBad2F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x2F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x32
    public void testBad32() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x32).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x33
    public void testBad33() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x33).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x34
    public void testBad34() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x34).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x37
    public void testBad37() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x37).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x3A
    public void testBad3A() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x3A).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x3B
    public void testBad3B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x3B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x3C
    public void testBad3C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x3C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x3F
    public void testBad3F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x3F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x42
    public void testBad42() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x42).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x43
    public void testBad43() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x43).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x44
    public void testBad44() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x44).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x47
    public void testBad47() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x47).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x4B
    public void testBad4B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x4B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x4F
    public void testBad4F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x4F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x52
    public void testBad52() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x52).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x53
    public void testBad53() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x53).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x54
    public void testBad54() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x54).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x57
    public void testBad57() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x57).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x5A
    public void testBad5A() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x5A).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x5B
    public void testBad5B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x5B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x5C
    public void testBad5C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x5C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x5F
    public void testBad5F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x5F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x62
    public void testBad62() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x62).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x63
    public void testBad63() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x63).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x64
    public void testBad64() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x64).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x67
    public void testBad67() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x67).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x6B
    public void testBad6B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x6B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x6F
    public void testBad6F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x6F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x72
    public void testBad72() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x72).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x73
    public void testBad73() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x73).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x74
    public void testBad74() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x74).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x77
    public void testBad77() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x77).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x7A
    public void testBad7A() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x7A).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x7B
    public void testBad7B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x7B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x7C
    public void testBad7C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x7C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x7F
    public void testBad7F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x7F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x80
    public void testBad80() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x80).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
    
    @Test // 0x82
    public void testBad82() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x82).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x83
    public void testBad83() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x83).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x87
    public void testBad87() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x87).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x89
    public void testBad89() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x89).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x8B
    public void testBad8B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x8B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x8F
    public void testBad8F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x8F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x92
    public void testBad92() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x92).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x93
    public void testBad93() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x93).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x97
    public void testBad97() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x97).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x9B
    public void testBad9B() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x9B).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x9C
    public void testBad9C() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x9C).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x9E
    public void testBad9E() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x9E).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0x9F
    public void testBad9F() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0x9F).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xA3
    public void testBadA3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xA3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xA7
    public void testBadA7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xA7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xAB
    public void testBadAB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xAB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xAF
    public void testBadAF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xAF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xB2
    public void testBadB2() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xB2).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xB3
    public void testBadB3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xB3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xB7
    public void testBadB7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xB7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xBB
    public void testBadBB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xBB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xBF
    public void testBadBF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xBF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xC2
    public void testBadC2() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xC2).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xC3
    public void testBadC3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xC3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xC7
    public void testBadC7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xC7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xCB
    public void testBadCB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xCB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xCF
    public void testBadCF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xCF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xD2
    public void testBadD2() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xD2).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xD3
    public void testBadD3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xD3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xD4
    public void testBadD4() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xD4).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xD7
    public void testBadD7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xD7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xDA
    public void testBadDA() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xDA).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xDB
    public void testBadDB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xDB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xDC
    public void testBadDC() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xDC).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xDF
    public void testBadDF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xDF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xE2
    public void testBadE2() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xE2).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xE3
    public void testBadE3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xE3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xE7
    public void testBadE7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xE7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xEB
    public void testBadEB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xEB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xEF
    public void testBadEF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xEF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xF2
    public void testBadF2() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xF2).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xF3
    public void testBadF3() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xF3).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xF4
    public void testBadF4() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xF4).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xF7
    public void testBadF7() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xF7).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xFA
    public void testBadFA() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xFA).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xFB
    public void testBadFB() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xFB).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xFC
    public void testBadFC() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xFC).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    @Test // 0xFF
    public void testBadFF() {
        MemoryIO mem = new MemoryBuilder().startAt(0x8000).put(0xFF).create();
        cpu6502.setMemoryIO(mem);
        cpu6502.reset();
        try {
            cpu6502.execute();
            fail();
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }
}
