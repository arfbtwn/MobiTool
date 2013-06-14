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
package format;

import static org.junit.Assert.*;

import org.junit.Test;

public class MobiFileTest2 extends MobiFileTest {

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

}
