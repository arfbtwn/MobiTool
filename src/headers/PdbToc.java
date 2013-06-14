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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import records.PdbRecord;

public class PdbToc implements Iterable<PdbRecord> {

    public static final int START_OFFSET = 76; // Length of the PDB Header

    private List<PdbRecord> records;

    private int             total_length;

    public PdbToc() {
        records = new ArrayList<PdbRecord>();
        total_length = 80;
    }

    public PdbToc(ByteBuffer toc) {
        total_length = toc.capacity();
        parse(toc);
    }

    public short getCount() {
        return (short) records.size();
    }

    public PdbRecord getRecord(int i) {
        return records.get(i);
    }

    public int getTotalLength() {
        return total_length;
    }

    @Override
    public ListIterator<PdbRecord> iterator() {
        return records.listIterator();
    }

    public ListIterator<PdbRecord> iterator(int i) {
        return records.listIterator(i);
    }

    private void parse(ByteBuffer raw) {
        raw.position(START_OFFSET);
        int count = raw.getShort();
        records = new ArrayList<PdbRecord>();
        for (int i = 0; i < count; ++i)
            records.add(new PdbRecord(raw));
        ListIterator<PdbRecord> it = records.listIterator(records.size());
        int last_offset = total_length;
        while (it.hasPrevious()) {
            PdbRecord item = it.previous();
            item.getData(raw, last_offset);
            last_offset = item.getOffset();
        }
    }

    public void refresh() {
        total_length = 76 + records.size() * 8 + 4;
        int j = 0;
        for (PdbRecord i : records) {
            i.setOffset(total_length);
            if (i.getID() == 0 && j > 0)
                i.setID(j);
            ++j;
            total_length += i.getLength();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(
                "[::::PDB Table of Contents::::]\n");
        sb.append("Records: " + records.size() + "\n");
        int j = 0;
        for (PdbRecord i : records)
            sb.append(String.format("%-3d. %s\n", j++, i.toString()));
        return sb.toString();
    }

    public void write(ByteBuffer out) {
        out.putShort(getCount());
        for (PdbRecord i : records) {
            out.put(i.getTocBuffer());
            int pos = out.position();
            out.position(i.getOffset());
            out.put(i.getBuffer());
            out.position(pos);
        }
        out.putShort((short) 0);
    }
}
