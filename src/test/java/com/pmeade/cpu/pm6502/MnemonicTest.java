/*
 * MenmonicTest.java
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

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author pmeade
 */
public class MnemonicTest
{
    public MnemonicTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testAlwaysSucceed() {
        assertTrue(true);
    }
    
    @Test
    public void testLdaFromString() {
        assertEquals(Mnemonic.LDA, Mnemonic.fromString("LDA"));
    }

    @Test
    public void testNullFromString() {
        assertNull(Mnemonic.fromString("LolCat"));
    }
}
