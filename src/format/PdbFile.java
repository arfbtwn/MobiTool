/**
 * Copyright (C) 2013 Nicholas J. Little <arealityfarbetween@googlemail.com>
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

import headers.PdbHeader;
import headers.PdbToc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import little.nj.util.FileUtil;
import little.nj.util.Statics;
import little.nj.util.StreamUtil.OutputAction;
import records.PdbRecord;
import util.WritesToFile;

public class PdbFile implements WritesToFile {

    private File      file;

    private PdbHeader header;

    public PdbFile() {
        header = new PdbHeader();
    }

    public PdbFile(File in) throws IOException {
        file = in;
        parse(ByteBuffer.wrap(Statics.readFile(file)));
    }

    public int getFileLength() {
        return getToc().getTotalLength();
    }

    public PdbHeader getHeader() {
        return header;
    }

    public PdbRecord getRecord(int i) {
        return getToc().getRecord(i);
    }

    public int getRecordCount() {
        return getToc().getCount();
    }

    public PdbToc getToc() {
        return header.getToc();
    }

    public void parse(ByteBuffer raw) {
        System.out.println("Extracting PdbHeader...");
        header = new PdbHeader(raw);
    }

    public void reload() throws IOException {
        parse(ByteBuffer.wrap(Statics.readFile(file)));
    }
    
    public boolean canSave() {
        return file != null;
    }

    public boolean writeToFile() {
        return writeToFile(file);
    }

    @Override
    public boolean writeToFile(File out) {
        /*
         * Prepare the output buffer
         */
        getToc().refresh();
        final ByteBuffer bb = ByteBuffer.allocate(getFileLength());
        header.write(bb);
        
        /*
         * Write the file
         */
        FileUtil util = FileUtil.getInstance();
        if (util.writeFile(out, new OutputAction() {

            @Override
            public void act(OutputStream stream) throws IOException {
                stream.write(bb.array());
                
            }})) {
            file = out;
            return true;
        }
        return false;
    }
}
