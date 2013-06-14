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

import static headers.PdbHeader.CHARSET;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import little.nj.adts.ByteFieldSet;
import little.nj.adts.IntByteField;
import little.nj.adts.StringByteField;
import little.nj.algorithms.KmpSearch;
import exceptions.InvalidHeaderException;
import headers.ExthHeader.ExthRecord;

public class ExthHeader implements Iterable<ExthRecord> {

    public class ExthRecord {

        private int  id;

        Integer      integer_data;

        String       string_data;

        private Type type;

        public ExthRecord(ByteBuffer in) {
            id = in.getInt();
            int length = in.getInt();
            byte[] data = new byte[length - 8];
            in.get(data, 0, length - 8);
            decodeData(data);
        }

        public ExthRecord(int id, byte[] data) {
            this.id = id;
            decodeData(data);
        }

        private void decodeData(byte[] data) {
            switch (getType()) {
            case INT:
                integer_data = Integer.valueOf(ByteBuffer.wrap(data).getInt());
                break;
            case STRING:
                string_data = new String(data, charset);
            }
        }

        public ByteBuffer getBuffer() {
            byte[] data = getData();
            return (ByteBuffer) ByteBuffer.allocate(8 + data.length).putInt(id)
                    .putInt(8 + data.length).put(data).rewind();
        }

        public byte[] getBytes() {
            return getBuffer().array();
        }

        public byte[] getData() {
            return recodeData();
        }

        public int getId() {
            return id;
        }

        public int getLength() {
            return getData().length + 8;
        }

        private ExthHeader.Type getType() {
            if (type == null) {
                int idx = Arrays.binarySearch(ENCODE_INT, id);
                type = idx >= 0 ? Type.INT : Type.STRING;
            }
            return type;
        }

        public String getValue() {
            switch (getType()) {
            case INT:
                return integer_data.toString();
            case STRING:
                return string_data;
            }
            return null;
        }

        private byte[] recodeData() {
            switch (getType()) {
            case INT:
                return ByteBuffer.allocate(4).putInt(integer_data.intValue())
                        .array();
            case STRING:
                return string_data.getBytes(charset);
            }
            return null;
        }

        public void setData(byte[] in) {
            decodeData(in);
        }

        public void setId(int i) {
            id = i;
        }

        public void setValue(String s) {
            switch (getType()) {
            case INT:
                integer_data = Integer.parseInt(s);
                break;
            case STRING:
                string_data = s;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: " + id + ", Length: " + getData().length);
            switch (getType()) {
            case INT:
                sb.append("\nInteger: " + integer_data);
                break;
            case STRING:
                sb.append("\nString: " + string_data);
            }
            return sb.toString();
        }
    }

    private static enum Type {
        INT, STRING;
    }

    public static final ByteFieldSet ALL_FIELDS     = new ByteFieldSet();

    public static final int          AUTHOR         = 100;

    public static final int          BLURB          = 103;

    public static final int          COVER          = 201;

    public static final int          CREATOR_ID     = 204;

    public static final int          CREATOR_STRING = 108;

    public static final Integer[]    ENCODE_INT;

    public static final Integer[]    ENCODE_STRING;

    public static final int          FAKECOVER      = 203;

    public static final int          ISBN           = 104;

    public static final int          THUMB          = 202;

    public static final int          TITLE          = 503;
    static {
        ALL_FIELDS.add(new StringByteField(4, "Identifier", CHARSET, "EXTH"));
        ALL_FIELDS.add(new IntByteField("Length"));
        ALL_FIELDS.add(new IntByteField("Count"));
        ENCODE_STRING = new Integer[] { Integer.valueOf(AUTHOR),
            Integer.valueOf(BLURB), Integer.valueOf(ISBN),
            Integer.valueOf(CREATOR_STRING), Integer.valueOf(TITLE) };
        ENCODE_INT = new Integer[] { Integer.valueOf(COVER),
            Integer.valueOf(THUMB), Integer.valueOf(FAKECOVER),
            Integer.valueOf(CREATOR_ID) };
    }

    private Charset                  charset;

    private ByteFieldSet             fields;

    private List<ExthRecord>         records;

    public ExthHeader(ByteBuffer in, Charset ch) throws InvalidHeaderException {
        this(ch);
        parse(in);
    }

    public ExthHeader(Charset ch) {
        fields = ALL_FIELDS.clone();
        records = new LinkedList<ExthRecord>();
        charset = ch;
    }

    private void addRecord(int id, byte[] data) {
        iterator().add(new ExthRecord(id, data));
    }

    public String getAuthor() {
        return new String(getRecord(AUTHOR).getData(), charset);
    }

    public String getBlurb() {
        return new String(getRecord(BLURB).getData(), charset);
    }

    public int getCount() {
        return records.size();
    }

    public int getCover() {
        try {
            return ByteBuffer.wrap(getRecord(COVER).getData()).getInt();
        } catch (NullPointerException e) {
        }
        return -1;
    }

    public int getLength() {
        int rtn = fields.length();
        for (ExthRecord i : records)
            rtn += i.getLength();
        return rtn;
    }

    public ExthRecord getRecord(int id) {
        ListIterator<ExthRecord> it = iterator();
        while (it.hasNext()) {
            ExthRecord i = it.next();
            if (i.getId() == id)
                return i;
        }
        return null;
    }

    public int getThumb() {
        try {
            return ByteBuffer.wrap(getRecord(THUMB).getData()).getInt();
        } catch (NullPointerException e) {
            return getCover();
        }
    }

    public String getTitle() {
        return new String(getRecord(TITLE).getData(), charset);
    }

    @Override
    public ListIterator<ExthRecord> iterator() {
        return records.listIterator();
    }

    public ListIterator<ExthRecord> iterator(int i) {
        return records.listIterator(i);
    }

    public void parse(ByteBuffer raw) throws InvalidHeaderException {
        int offset = KmpSearch.indexOf(raw.array(),
                new String("EXTH").getBytes(charset));
        if (offset >= 0) {
            raw.position(offset - raw.arrayOffset());
            raw = raw.slice();
        } else
            throw new InvalidHeaderException();
        records.clear();
        fields.parseAll(raw);
        int count = fields.<IntByteField>getAs("Count").getValue();
        for (int i = 0; i < count; i++)
            records.add(new ExthRecord(raw));
    }

    public void setAuthor(String s) {
        try {
            getRecord(AUTHOR).setData(s.getBytes(charset));
        } catch (NullPointerException e) {
            addRecord(AUTHOR, s.getBytes(charset));
        }
    }

    public void setBlurb(String s) {
        try {
            getRecord(BLURB).setData(s.getBytes(charset));
        } catch (NullPointerException e) {
            addRecord(BLURB, s.getBytes(charset));
        }
    }

    public void setCover(int i) {
        byte[] data = ByteBuffer.allocate(4).putInt(i).array();
        try {
            getRecord(COVER).setData(data);
        } catch (NullPointerException e) {
            addRecord(COVER, data);
        }
    }

    public void setThumb(int i) {
        byte[] data = ByteBuffer.allocate(4).putInt(i).array();
        try {
            getRecord(THUMB).setData(data);
        } catch (NullPointerException e) {
            addRecord(THUMB, data);
        }
    }

    public void setTitle(String s) {
        try {
            getRecord(TITLE).setData(s.getBytes(charset));
        } catch (NullPointerException e) {
            addRecord(TITLE, s.getBytes(charset));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[::::EXTH Header:::]\n");
        sb.append(fields.toString() + "\n");
        Iterator<ExthRecord> it = records.iterator();
        while (it.hasNext())
            sb.append(it.next() + "\n");
        return sb.toString();
    }

    public void write(ByteBuffer out) {
        fields.<IntByteField>getAs("Length").setValue(getLength());
        fields.<IntByteField>getAs("Count").setValue(records.size());
        fields.write(out);
        for (ExthRecord i : records)
            out.put(i.getBuffer());
    }
}
