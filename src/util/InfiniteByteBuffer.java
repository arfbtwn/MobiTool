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
package util;

import java.nio.ByteBuffer;

public class InfiniteByteBuffer {

    public static final double SCALING = 1.25D;

    private ByteBuffer         raw;

    @SuppressWarnings("unused")
    private int                resizes;

    private double             scaling;

    public InfiniteByteBuffer(int capacity) {
        raw = ByteBuffer.allocate(capacity);
        scaling = 1.25D;
    }

    public byte[] array() {
        return raw.array();
    }

    public int capacity() {
        return raw.capacity();
    }

    public byte get() {
        return raw.get();
    }

    public InfiniteByteBuffer get(byte[] out) {
        return get(out, 0, out.length);
    }

    public InfiniteByteBuffer get(byte[] out, int offset, int length) {
        raw.get(out, offset, length);
        return this;
    }

    public ByteBuffer getBacking() {
        return raw;
    }

    public int position() {
        return raw.position();
    }

    public void position(int pos) {
        raw.position(pos);
    }

    public InfiniteByteBuffer put(byte b) {
        return put(new byte[] { b }, 0, 1);
    }

    public InfiniteByteBuffer put(byte[] in) {
        return put(in, 0, in.length);
    }

    public InfiniteByteBuffer put(byte[] in, int offset, int length) {
        if (length - offset <= raw.remaining())
            raw.put(in, offset, length);
        else {
            resizes += 1;
            int new_capacity = (int) ((raw.capacity() + length) * scaling);
            ByteBuffer tmp = ByteBuffer.allocate(new_capacity);
            raw.position(0);
            tmp.put(raw);
            tmp.put(in, offset, length);
            raw = tmp;
        }
        return this;
    }
}
