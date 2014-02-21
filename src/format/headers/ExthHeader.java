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
package format.headers;

import static format.headers.PdbHeader.CHARSET;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import little.nj.adts.ByteFieldMapSet;
import little.nj.adts.IntByteField;
import little.nj.adts.StringByteField;
import little.nj.algorithms.KmpSearch;
import little.nj.util.StringUtil;
import exceptions.InvalidHeaderException;
import format.headers.ExthHeader.ExthRecord;

public class ExthHeader implements Iterable<ExthRecord> {

    private static final int INT_SIZE = 4;

	private static final String US_ASCII = "US-ASCII";

	private static final String EXTH = "EXTH";

	private static final String COUNT = "Count";

	private static final String LENGTH = "Length";

	private static final String IDENTIFIER = "Identifier";

	public static enum DataType {
        INT, STRING;
    }

    public static final ByteFieldMapSet ALL_FIELDS = new ByteFieldMapSet();

    public static final int AUTHOR = 100;

    public static final int BLURB = 103;

    public static final int COVER = 201;

    public static final int CREATOR_ID = 204;

    public static final int CREATOR_STRING = 108;

    public static final int FAKECOVER = 203;

    public static final int ISBN = 104;

    public static final int THUMB = 202;

    public static final int TITLE = 503;

    /**
     * Used by the {@link ExthRecord#getType()} function
     */
    public static final Map<Integer, DataType> ENCODE_MAP;
    
    static {
        ALL_FIELDS.add(new StringByteField(4, IDENTIFIER, CHARSET, EXTH));
        ALL_FIELDS.add(new IntByteField(LENGTH));
        ALL_FIELDS.add(new IntByteField(COUNT));
        
        ENCODE_MAP = new TreeMap<Integer, DataType>();
        ENCODE_MAP.put(AUTHOR, DataType.STRING);
        ENCODE_MAP.put(BLURB, DataType.STRING);
        ENCODE_MAP.put(ISBN, DataType.STRING);
        ENCODE_MAP.put(CREATOR_STRING, DataType.STRING);
        ENCODE_MAP.put(TITLE, DataType.STRING);
        
        ENCODE_MAP.put(COVER, DataType.INT);
        ENCODE_MAP.put(THUMB, DataType.INT);
        ENCODE_MAP.put(FAKECOVER, DataType.INT);
        ENCODE_MAP.put(CREATOR_ID, DataType.INT);
    }

    Charset charset;

    private ByteFieldMapSet fields;

    private List<ExthRecord> records;
    
    public ExthHeader(Charset ch) {
        fields = ALL_FIELDS.clone();
        records = new LinkedList<ExthRecord>();
        charset = ch;
    }

    public ExthHeader(ByteBuffer in, Charset ch) throws InvalidHeaderException {
        this(ch);
        parse(in);
    }
    
    public void parse(ByteBuffer raw) throws InvalidHeaderException {
        int offset = KmpSearch.indexOf(raw.array(),
                new String(EXTH).getBytes(Charset.forName(US_ASCII)));
        if (offset >= 0) {
            raw.position(offset - raw.arrayOffset());
            raw = raw.slice();
        } else
            throw new InvalidHeaderException();
        
        fields.parseAll(raw);
        int count = fields.<IntByteField> getAs(COUNT).getValue();
        for (int i = 0; i < count; i++)
            records.add(new ExthRecord(raw));
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
    
    private void setRecord(int id, byte[] data) {
        ExthRecord rec = getRecord(id);
        
        if (rec != null)
            rec.setData(data);
        else
            addRecord(id, data);
    }

    private void addRecord(int id, byte[] data) {
        iterator().add(new ExthRecord(id, data));
    }

    public String getAuthor() {
    	return getRecordValue(AUTHOR);
    }

    public String getBlurb() {
    	return getRecordValue(BLURB);
    }
    
    private String getRecordValue(int id) {
    	ExthRecord rec = getRecord(id);
    	
    	return null != rec ? rec.getValue() : StringUtil.EMPTY_STRING;
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

    public int getThumb() {
        try {
            return ByteBuffer.wrap(getRecord(THUMB).getData()).getInt();
        } catch (NullPointerException e) {
            return getCover();
        }
    }

    public String getTitle() {
    	return getRecordValue(TITLE);
    }
    
    public void setAuthor(String s) {
        setRecord(AUTHOR, s.getBytes(charset));
    }

    public void setBlurb(String s) {
        setRecord(BLURB, s.getBytes(charset));
    }

    public void setCover(int i) {
        byte[] data = ByteBuffer.allocate(INT_SIZE).putInt(i).array();
        setRecord(COVER, data);
    }

    public void setThumb(int i) {
        byte[] data = ByteBuffer.allocate(INT_SIZE).putInt(i).array();
        setRecord(THUMB, data);
    }

    public void setTitle(String s) {
        setRecord(TITLE, s.getBytes(charset));
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

    @Override
    public ListIterator<ExthRecord> iterator() {
        return records.listIterator();
    }

    public ListIterator<ExthRecord> iterator(int i) {
        return records.listIterator(i);
    }

    public void write(ByteBuffer out) {
        fields.<IntByteField> getAs(LENGTH).setValue(getLength());
        fields.<IntByteField> getAs(COUNT).setValue(records.size());
        fields.write(out);
        for (ExthRecord i : records)
            out.put(i.getBuffer());
    }
    
    public class ExthRecord {

    	private static final int STATIC_DATA = 8;

		private final int id;
		
		private byte[] cache;

        private Integer integer_data;

        private String string_data;

        private DataType type;

        public ExthRecord(ByteBuffer in) {
    		id = in.getInt();
            int length = in.getInt();
            
            byte[] data = new byte[length - STATIC_DATA];
            in.get(data, 0, length - STATIC_DATA);
            setData(data);
        }

        public ExthRecord(int id, byte[] data) {
    		this.id = id;
            setData(data);
        }
        
        public int getId() {
            return id;
        }
        
        public byte[] getData() {
        	if (null == cache) {
	        	switch (getType()) {
		        case INT:
		            cache = ByteBuffer.allocate(INT_SIZE)
		            		.putInt(integer_data.intValue())
		                    .array();
		        case STRING:
		            cache = string_data.getBytes(charset);
		        }
        	}
	        return cache;
        }

        public void setData(byte[] data) {
        	this.cache = data;
        	switch (getType()) {
            case INT:
                integer_data = ByteBuffer.wrap(data).getInt();
                break;
            case STRING:
                string_data = new String(data, charset);
            }
        }

        public int getLength() {
            return getData().length + STATIC_DATA;
        }        
        
        protected DataType getType() {
            if (null == type) {
                type = ENCODE_MAP.get(id);
                
                /*
                 * HACK: Unknown ID, let's take a look as a string
                 */
                if (null == type) {
                	type = DataType.STRING;
                }
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

        public void setValue(String s) {
        	cache = null;
            switch (getType()) {
            case INT:
                integer_data = Integer.parseInt(s);
                break;
            case STRING:
                string_data = s;
            }
        }

        public byte[] getBytes() {
            return getBuffer().array();
        }

        public ByteBuffer getBuffer() {
        	int length = getLength();
            byte[] data = getData();
            return (ByteBuffer) ByteBuffer.allocate(length)
            		.putInt(id)
                    .putInt(length)
                    .put(data)
                    .rewind();
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
}
