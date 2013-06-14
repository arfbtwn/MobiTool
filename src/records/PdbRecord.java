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
package records;

import java.nio.ByteBuffer;

public class PdbRecord implements Comparable<PdbRecord> {

    public static final byte LENGTH    = 8;

    public static final byte LENGTH_ID = 3;

    private byte[]           data;

    private byte             flags;

    private byte[]           id;

    private int              offset;

    public PdbRecord() {
        id = new byte[3];
        flags = 0;
        offset = 0;
    }

    public PdbRecord(byte[] in) {
        this();
        data = in;
    }

    public PdbRecord(byte[] in, int order) {
        this(in);
        offset = order;
    }

    public PdbRecord(ByteBuffer input) {
        this();
        parse(input);
    }

    @Override
    public int compareTo(PdbRecord r) {
        return Integer.compare(offset, r.offset);
    }

    public ByteBuffer getBuffer() {
        return ByteBuffer.wrap(data);
    }

    public byte[] getBytes() {
        return data;
    }

    public void getData(ByteBuffer raw, int end) {
        raw.position(offset);
        data = new byte[end - raw.position()];
        raw.get(data);
    }

    public byte getFlags() {
        return flags;
    }

    public int getID() {
        return (id[0] << 16 | id[1] << 8 | id[2]) & 0xFFFFFF;
    }

    public int getLength() {
        return data != null ? data.length : 0;
    }

    public int getOffset() {
        return offset;
    }

    public ByteBuffer getTocBuffer() {
        ByteBuffer rtn = ByteBuffer.allocate(8);
        rtn.putInt(offset);
        rtn.put(flags);
        rtn.put(id);
        rtn.flip();
        return rtn;
    }

    public void parse(ByteBuffer raw) {
        offset = raw.getInt();
        flags = raw.get();
        raw.get(id, 0, 3);
    }

    public void setBytes(byte[] in) {
        data = in;
    }

    public void setBytes(ByteBuffer in) {
        data = in.array();
    }

    public void setFlags(byte b) {
        flags = b;
    }

    public void setID(int i) {
        if (i >>> 24 != 0)
            throw new IllegalArgumentException("ID Out of Range: " + i);
        id[0] = (byte) ((i & 0xFF0000) >>> 16);
        id[1] = (byte) ((i & 0xFF00) >>> 8);
        id[2] = (byte) (i & 0xFF);
    }

    public void setOffset(int i) {
        offset = i;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Record { Offset: " + getOffset() + ", Flags: " + getFlags()
                + ", ID: " + getID() + ", ");
        sb.append("Data Length: " + getLength() + " }");
        return sb.toString();
    }
}
