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
package util;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.MobiBaseTest;

public class MediaLoaderTest extends MobiBaseTest {
    MediaLoader _loader;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            String strBase = String.format("%s://%s/%s/", "file",
                    System.getProperty("user.dir"), DIR_CONTENT);
            URL base = new URL(strBase);
            _loader = new MediaLoader(base);
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

    @Test
    public void test_loadFromUrl() {
        BufferedImage rv = null;
        try {
            _loader.put("image", null);
            _loader.load();
            rv = _loader.get("image");
            assertEquals(false, rv == null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
