/**
 * Copyright (C) 2013 
 * Nicholas J. Little <arealityfarbetween@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import static org.junit.Assert.*;

import javax.swing.text.html.HTMLDocument;
import org.junit.Test;

import test.MobiBaseTest;


public class HtmlImporterTest extends MobiBaseTest {

    HtmlImporter imp = new HtmlImporter();
    
    /**
     * Test method for {@link util.HtmlImporter#readFromFile(java.io.File)}.
     */
    @Test
    public void testReadFromFile() {
        imp.readFromFile(getHtmlContentFile("LoremIpsum"));
        
        HTMLDocument doc = imp.getDocument();
        
        assertTrue(doc.getLength() > 0);
    }

    /**
     * Test method for {@link util.HtmlImporter#stripParagraphStyle()}.
     */
    @Test
    public void testStripParagraphStyle() {
        testReadFromFile();
        
        imp.stripParagraphStyle();
    }

    /**
     * Test method for {@link util.HtmlImporter#generateMobiToc()}.
     */
    @Test
    public void testGenerateMobiToc() {
        testReadFromFile();
        
        imp.generateMobiToc();
    }
    
}
