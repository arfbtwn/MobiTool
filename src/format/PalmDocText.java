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

import format.headers.Enumerations.Encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PalmDocText {

    public static final short DEFAULT_RECORD_LENGTH = 4096;

    private Codec codec;

    private Encoding encoding;

    private short record_length;

    private ByteArrayOutputStream o_stream = new ByteArrayOutputStream();

    public PalmDocText(Encoding enc) {
        this(DEFAULT_RECORD_LENGTH, enc);
    }

    public PalmDocText(short r_length, Encoding enc) {
        record_length = r_length;
        encoding = enc;
    }

    public void addToFile(ByteBuffer in) {
        try {
            byte[] bytes = codec.decompress(in.array());
            o_stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Codec getCodec() {
        return codec;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public String getText() {
        return new String(o_stream.toByteArray(), encoding.getCharset());
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public void setEncoding(Encoding e) {
        if (encoding != e) {
            String text = getText();
            encoding = e;
            setText(text);
        }
    }

    public void setText(String sb) {
        o_stream.reset();
        try {
            o_stream.write(sb.getBytes(encoding.getCharset()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ByteArrayInputStream getStream() {
        return new ByteArrayInputStream(o_stream.toByteArray());
    }

    public int getRecordCount() {
        return (int) Math.ceil(getUncompressedLength() / (double) record_length);
    }

    public int getUncompressedLength() {
        return o_stream.size();
    }

    public byte[][] getCompressedRecords() {
        byte[][] rtn = new byte[getRecordCount()][];
        byte[] bytes = o_stream.toByteArray();
        for (int i = 0; i < bytes.length; i += record_length) {
            byte[] record;
            if (bytes.length - i < record_length)
                record = new byte[bytes.length - i];
            else
                record = new byte[record_length];
            System.arraycopy(bytes, i, record, 0, record.length);
            rtn[i / record_length] = codec.compress(record);
        }
        return rtn;
    }
}
