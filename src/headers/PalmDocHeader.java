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
package headers;

import java.nio.ByteBuffer;

import little.nj.adts.ByteFieldMapSet;
import little.nj.adts.IntByteField;
import little.nj.adts.ShortByteField;

import headers.Enumerations.Compression;

public class PalmDocHeader {

    public static final ByteFieldMapSet ALL_FIELDS = new ByteFieldMapSet();

    public static final short        LENGTH     = 16;
    static {
        ALL_FIELDS.add(new ShortByteField("Compression", Compression.NONE
                .getValue()));
        ALL_FIELDS.add(new ShortByteField("Unused"));
        ALL_FIELDS.add(new IntByteField("Uncompressed Text Length"));
        ALL_FIELDS.add(new ShortByteField("Record Count"));
        ALL_FIELDS.add(new ShortByteField("Record Size", (short) 4096));
        ALL_FIELDS.add(new IntByteField("Current Position"));
    }

    protected ByteFieldMapSet fields;

    public PalmDocHeader() {
        fields = ALL_FIELDS.clone();
    }

    public PalmDocHeader(ByteBuffer in) {
        fields = ALL_FIELDS.clone();
        parse(in.slice());
    }

    public ByteBuffer getBuffer() {
        return fields.getBuffer();
    }

    public Compression getCompression() {
        return Compression.valueOf(fields.<ShortByteField>getAs("Compression").getValue());
    }

    public ByteFieldMapSet getFields() {
        return fields;
    }

    public short getTextRecordCount() {
        return fields.<ShortByteField>getAs("Record Count").getValue();
    }

    public short getTextRecordLength() {
        return fields.<ShortByteField>getAs("Record Size").getValue();
    }

    public int getUncompressedTextLength() {
        return fields.<IntByteField>getAs("Uncompressed Text Length").getValue();
    }

    public void parse(ByteBuffer in) {
        fields.parseAll(in);
    }

    public void setCompression(Compression c) {
        fields.<ShortByteField>getAs("Compression").setValue(c.getValue());
    }

    public void setTextRecordCount(int i) {
        fields.<ShortByteField>getAs("Record Count").setValue((short) i);
    }

    public void setUncompressedTextLength(int i) {
        fields.<IntByteField>getAs("Uncompressed Text Length").setValue(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[::::PalmDOC Header::::]\n");
        sb.append(fields);
        return sb.toString();
    }
}
