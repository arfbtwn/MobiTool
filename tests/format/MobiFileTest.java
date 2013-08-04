/**
 * Copyright (C) 2013
 * Nicholas J. Little <arealityfarbetween@googlemail.com>
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
package format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import headers.MobiDocHeader;
import headers.PalmDocHeader;
import headers.Enumerations.Compression;

import java.io.File;
import javax.imageio.ImageIO;

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
public class MobiFileTest extends MobiBaseTest {

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

    CodecManager  _codecs = new CodecManager();

    MobiFile      _file;

    MobiDocHeader _mobi;

    PalmDocHeader _palm;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        if (_filename != null)
            _file = new MobiFile(getMobiContentFile(_filename), _codecs);
        else
            _file = new MobiFile(_codecs);
        _mobi = _file.getMobiDocHeader();
        _palm = _file.getPalmDocHeader();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link format.MobiFile#adjustRecordPointers(int, int, int[])}.
     */
    @Test
    public void test_AdjustRecordPointers() {
        int[] idxs = { 10, // First Non-book
                10, // Indx
                11, // First Image
                15, // Last Content
                0,  // Huffman
                16, // FLIS
                17  // FCIS
        };
        _file.adjustRecordPointers(1, 10, idxs);
        assertEquals(20, idxs[0]);
        assertEquals(20, idxs[1]);
        assertEquals(21, idxs[2]);
        assertEquals(25, idxs[3]);
        assertEquals(0, idxs[4]);
        assertEquals(26, idxs[5]);
        assertEquals(27, idxs[6]);
        _file.adjustRecordPointers(1, -10, idxs);
        assertEquals(10, idxs[0]);
        assertEquals(10, idxs[1]);
        assertEquals(11, idxs[2]);
        assertEquals(15, idxs[3]);
        assertEquals(0, idxs[4]);
        assertEquals(16, idxs[5]);
        assertEquals(17, idxs[6]);
        _file.adjustRecordPointers(11, -7, idxs);
        assertEquals(10, idxs[0]);
        assertEquals(10, idxs[1]);
        assertEquals(-1, idxs[2]);
        assertEquals(-1, idxs[3]);
        assertEquals(0, idxs[4]);
        assertEquals(-1, idxs[5]);
        assertEquals(-1, idxs[6]);
    }

    @Test
    public void test_File_UnhandledRecords() {
        try {
            _filename = "Child, Lee - Bad Luck and Trouble";
            setUp();
            int rec_lcon = _mobi.getLastContentRecord();
            int rec_fnob = _mobi.getFirstNonBookRecord();
            int rec_imag = _mobi.getFirstImageRecord();
            int rec_indx = _mobi.getIndxRecord();
            int rec_huff = _mobi.getHuffmanRecord();
            int rec_flis = _mobi.getFlisRecord();
            int rec_fcis = _mobi.getFcisRecord();
            int cnt_text = _palm.getTextRecordCount();
            int cnt_imag = _file.getImages().size();
            _file.removeText();
            assertEquals("IndxRecord", rec_indx - cnt_text,
                    _mobi.getIndxRecord());
            assertEquals("FirstNonBookRecord", rec_fnob - cnt_text,
                    _mobi.getFirstNonBookRecord());
            assertEquals("FirstImageRecord", rec_imag - cnt_text,
                    _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", rec_lcon - cnt_text,
                    _mobi.getLastContentRecord());
            assertEquals("HuffmanRecord", rec_huff, _mobi.getHuffmanRecord());
            assertEquals("FlisRecord", rec_flis - cnt_text,
                    _mobi.getFlisRecord());
            assertEquals("FcisRecord", rec_fcis - cnt_text,
                    _mobi.getFcisRecord());
            _file.removeImages();
            assertEquals("IndxRecord", rec_indx - cnt_text,
                    _mobi.getIndxRecord());
            assertEquals("FirstNonBookRecord", rec_fnob - cnt_text,
                    _mobi.getFirstNonBookRecord());
            assertEquals("FirstImageRecord", -1, _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", rec_lcon - cnt_text - cnt_imag,
                    _mobi.getLastContentRecord());
            assertEquals("HuffmanRecord", rec_huff, _mobi.getHuffmanRecord());
            assertEquals("FlisRecord", rec_flis - cnt_text - cnt_imag,
                    _mobi.getFlisRecord());
            assertEquals("FcisRecord", rec_fcis - cnt_text - cnt_imag,
                    _mobi.getFcisRecord());
            _file.insertContent();
            assertEquals("IndxRecord", rec_indx, _mobi.getIndxRecord());
            assertEquals("FirstNonBookRecord", rec_fnob,
                    _mobi.getFirstNonBookRecord());
            assertEquals("FirstImageRecord", rec_imag,
                    _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", rec_lcon,
                    _mobi.getLastContentRecord());
            assertEquals("HuffmanRecord", rec_huff, _mobi.getHuffmanRecord());
            assertEquals("FlisRecord", rec_flis, _mobi.getFlisRecord());
            assertEquals("FcisRecord", rec_fcis, _mobi.getFcisRecord());
            ((PdbFile) _file).writeToFile(getMobiOutputFile(_filename));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        } finally {
            _filename = null;
        }
    }

    @Test
    public void test_New_Compressed_TextRecordOnly_FilePointers() {
        try {
            File html = getHtmlContentFile("LoremIpsum");
            _file.importFromHtml(html);
            _file.setTitle("Lorem Ipsum (Compressed)");
            _file.setAuthor("Me");
            _file.setBlurb("Test Document");
            _palm.setCompression(Compression.PALMDOC);
            _file.insertContent();
            _file.buildRecordZero();
            assertEquals("FirstContentRecord", 1, _mobi.getFirstContentRecord());
            assertEquals("FirstNonBookRecord", -1,
                    _mobi.getFirstNonBookRecord());
            assertEquals("IndxRecord", -1, _mobi.getIndxRecord());
            assertEquals("HuffmanRecord", -1, _mobi.getHuffmanRecord());
            assertEquals("FirstImageRecord", -1, _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", 1, _mobi.getLastContentRecord());
            assertEquals("FlisRecord", -1, _mobi.getFlisRecord());
            assertEquals("FcisRecord", -1, _mobi.getFcisRecord());
            assertEquals(1, _palm.getTextRecordCount());
            assertEquals(2, _file.getRecordCount());
            File out = getMobiOutputFile("LoremIpsum_compressed");
            ((PdbFile) _file).writeToFile(out);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            _filename = null;
        }
    }

    @Test
    public void test_New_TextImageRecord_FilePointers() {
        try {
            File html = getHtmlContentFile("LoremIpsum");
            _file.importFromHtml(html);
            _file.setCovers(ImageIO.read(getContentFile("image")));
            _file.setTitle("Lorem Ipsum (Cover)");
            _file.setAuthor("Me");
            _file.setBlurb("Test Document");
            _file.insertContent();
            _file.buildRecordZero();
            assertEquals("FirstContentRecord", 1, _mobi.getFirstContentRecord());
            assertEquals("FirstNonBookRecord", 2, _mobi.getFirstNonBookRecord());
            assertEquals("IndxRecord", -1, _mobi.getIndxRecord());
            assertEquals("HuffmanRecord", -1, _mobi.getHuffmanRecord());
            assertEquals("FirstImageRecord", 2, _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", 2, _mobi.getLastContentRecord());
            assertEquals("FlisRecord", -1, _mobi.getFlisRecord());
            assertEquals("FcisRecord", -1, _mobi.getFcisRecord());
            assertEquals(1, _palm.getTextRecordCount());
            assertEquals(3, _file.getRecordCount());
            ((PdbFile) _file)
                    .writeToFile(getMobiOutputFile("LoremIpsum_Image"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_New_TextRecordOnly_FilePointers() {
        try {
            File html = getHtmlContentFile("LoremIpsum");
            _file.importFromHtml(html);
            _file.setTitle("Lorem Ipsum");
            _file.setAuthor("Me");
            _file.setBlurb("Test Document");
            _file.insertContent();
            _file.buildRecordZero();
            assertEquals("FirstContentRecord", 1, _mobi.getFirstContentRecord());
            assertEquals("FirstNonBookRecord", -1,
                    _mobi.getFirstNonBookRecord());
            assertEquals("IndxRecord", -1, _mobi.getIndxRecord());
            assertEquals("HuffmanRecord", -1, _mobi.getHuffmanRecord());
            assertEquals("FirstImageRecord", -1, _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", 1, _mobi.getLastContentRecord());
            assertEquals("FlisRecord", -1, _mobi.getFlisRecord());
            assertEquals("FcisRecord", -1, _mobi.getFcisRecord());
            assertEquals(1, _palm.getTextRecordCount());
            assertEquals(2, _file.getRecordCount());
            File out = getMobiOutputFile("LoremIpsum");
            ((PdbFile) _file).writeToFile(out);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            _filename = null;
        }
    }

    @Test
    public void test_Old_TextRecordOnly_FilePointers() {
        try {
            _filename = "LoremIpsum";
            setUp();
            _file.refresh();
            assertEquals("FirstContentRecord", 1, _mobi.getFirstContentRecord());
            assertEquals("FirstNonBookRecord", -1,
                    _mobi.getFirstNonBookRecord());
            assertEquals("IndxRecord", -1, _mobi.getIndxRecord());
            assertEquals("HuffmanRecord", -1, _mobi.getHuffmanRecord());
            assertEquals("FirstImageRecord", -1, _mobi.getFirstImageRecord());
            assertEquals("LastContentRecord", 1, _mobi.getLastContentRecord());
            assertEquals("FlisRecord", -1, _mobi.getFlisRecord());
            assertEquals("FcisRecord", -1, _mobi.getFcisRecord());
            assertEquals(1, _palm.getTextRecordCount());
            assertEquals(2, _file.getRecordCount());
            File out = getMobiOutputFile(_filename + "_refresh");
            ((PdbFile) _file).writeToFile(out);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        } finally {
            _filename = null;
        }
    }
}