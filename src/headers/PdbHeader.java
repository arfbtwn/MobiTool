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
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;

import little.nj.adts.ByteFieldMapSet;
import little.nj.adts.IntByteField;
import little.nj.adts.ShortByteField;
import little.nj.adts.StringByteField;


public class PdbHeader {

    /**
     * A predefined, Cloneable set of fields
     * 
     * FIXME: This is not immutable
     */
    public static final ByteFieldMapSet ALL_FIELDS;
    
    /**
     * Defined as Charset.forName("US-ASCII")
     */
    public static final Charset      CHARSET;

    /**
     * January 1, 1904 Etc/UTC
     * 
     * FIXME: This is not immutable
     */
    public static final Calendar     EPOCH_MAC;

    /**
     * January 1, 1970 Etc/UTC
     * 
     * FIXME: This is not immutable
     */
    public static final Calendar     EPOCH_NIX;

    /**
     * Length of the Name String field
     */
    public static final int          LENGTH_NAME = 32;

    /**
     * TimeZone.getTimeZone("Etc/UTC")
     */
    public static final TimeZone     TIMEZONE;
    
    static {
        CHARSET = Charset.forName("US-ASCII");
        TIMEZONE = TimeZone.getTimeZone("Etc/UTC");
        EPOCH_MAC = Calendar.getInstance(TIMEZONE);
        EPOCH_MAC.clear();
        EPOCH_MAC.set(1904, 0, 1);
        EPOCH_NIX = Calendar.getInstance(TIMEZONE);
        EPOCH_NIX.clear();
        EPOCH_NIX.set(1970, 0, 1);
        ALL_FIELDS = new ByteFieldMapSet();
        ALL_FIELDS.add(new StringByteField(LENGTH_NAME, "Name", CHARSET));
        ALL_FIELDS.add(new ShortByteField("Attributes"));
        ALL_FIELDS.add(new ShortByteField("Version"));
        ALL_FIELDS.add(new IntByteField("Creation Time", getPdbSeconds(
                Calendar.getInstance(), true)));
        ALL_FIELDS.add(new IntByteField("Modification Time", getPdbSeconds(
                Calendar.getInstance(), true)));
        ALL_FIELDS.add(new IntByteField("Backedup Time"));
        ALL_FIELDS.add(new IntByteField("Modification Number"));
        ALL_FIELDS.add(new IntByteField("App Info ID"));
        ALL_FIELDS.add(new IntByteField("Sort Info ID"));
        ALL_FIELDS.add(new StringByteField(4, "Type", CHARSET, "BOOK"));
        ALL_FIELDS.add(new StringByteField(4, "Creator", CHARSET, "MOBI"));
        ALL_FIELDS.add(new IntByteField("Unique Seed ID"));
        ALL_FIELDS.add(new IntByteField("Next Record List ID"));
    }

    private static Calendar getDate(int i, boolean signed_date) {
        Calendar c = Calendar.getInstance();
        long time = i * 1000L;
        switch (signed_date ? 0 : 1) {
        case 0:
            c.setTimeInMillis(EPOCH_NIX.getTimeInMillis() + time);
            break;
        case 1:
            c.setTimeInMillis(EPOCH_MAC.getTimeInMillis() + time);
        }
        return c;
    }

    private static int getPdbSeconds(Calendar c, boolean signed_date) {
        long i = 0L;
        switch (signed_date ? 0 : 1) {
        case 0:
            i = c.getTimeInMillis() - EPOCH_NIX.getTimeInMillis();
            break;
        case 1:
            i = c.getTimeInMillis() - EPOCH_MAC.getTimeInMillis();
        }
        i = i / 1000L;
        return (int) i;
    }

    private ByteFieldMapSet fields;

    private boolean      signed_date;

    private PdbToc       toc;

    public PdbHeader() {
        fields = ALL_FIELDS.clone();
        toc = new PdbToc();
        signed_date = true;
    }

    public PdbHeader(ByteBuffer buffer) {
        fields = ALL_FIELDS.clone();
        parse(buffer.slice());
    }

    public Calendar getBackedupTime() {
        return getDate(fields.<IntByteField>getAs("Backedup Time").getValue(), signed_date);
    }

    public Calendar getCreationTime() {
        return getDate(fields.<IntByteField>getAs("Creation Time").getValue(), signed_date);
    }

    public Calendar getModificationTime() {
        return getDate(fields.<IntByteField>getAs("Modification Time").getValue(),
                signed_date);
    }

    public String getName() {
        return fields.<StringByteField>getAs("Name").getValue().trim();
    }

    public PdbToc getToc() {
        return toc;
    }

    public void parse(ByteBuffer raw) {
        fields.parseAll(raw);
        signed_date = fields.<IntByteField>getAs("Creation Time").getValue() >>> 31 == 0;
        toc = new PdbToc(raw);
    }

    public void setBackedupTime(Calendar c) {
        int secs = getPdbSeconds(c, signed_date);
        fields.<IntByteField>getAs("Backedup Time").setValue(secs);
    }

    public void setCreationTime(Calendar c) {
        int secs = getPdbSeconds(c, signed_date);
        fields.<IntByteField>getAs("Creation Time").setValue(secs);
    }

    public void setModificationTime(Calendar c) {
        int secs = getPdbSeconds(c, signed_date);
        fields.<IntByteField>getAs("Modification Time").setValue(secs);
    }

    public void setName(String x) {
        fields.<StringByteField>getAs("Name").setValue(x);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[::::PDB Header::::]\n");
        sb.append(fields);
        return sb.toString();
    }

    public void write(ByteBuffer out) {
        setModificationTime(Calendar.getInstance());
        fields.write(out);
        toc.write(out);
    }
}
