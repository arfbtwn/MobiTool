/**
 * Copyright (C) 2013 nicholas
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package algorithms;

import junit.framework.Assert;
import little.nj.util.Statics;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.MobiBaseTest;

/**
 * @author nicholas
 * 
 */
public class PalmDocCodecTest extends MobiBaseTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    byte[] compressed;

    byte[] uncompressed;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _filename = "LoremIpsum";
        //_filename = "cpp-style-technique";
        try {
            uncompressed = Statics.readFile(getHtmlContentFile(_filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /*
     * TODO: testEquivalence is valid iff decompression works...
     */
    // @Test
    public void testCompress() {
    }

    /*
     * TODO: testEquivalence is valid iff decompression works...
     */
    // @Test
    public void testDecompress() {
    }

    @Test
    public void testEquivalence() {
        PalmDocCodec codec = new PalmDocCodec();
        compressed = codec.compress(uncompressed);
        byte[] result = codec.decompress(compressed);
        Assert.assertEquals(uncompressed.length, result.length);
        for (int i = 0; i < result.length; ++i)
            Assert.assertEquals(String.format("Index %d Not Equal", i),
                    uncompressed[i], result[i]);
        try {
            Statics.writeFile(getHtmlOutputFile(_filename + "_compressed"),
                    compressed);
            Statics.writeFile(getHtmlOutputFile(_filename), uncompressed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
