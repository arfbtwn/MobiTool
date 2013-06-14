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

import little.nj.adts.ByteField;
import little.nj.adts.ByteFieldSet;
import little.nj.adts.IntByteField;
import little.nj.adts.ShortByteField;
import little.nj.adts.StringByteField;
import little.nj.algorithms.KmpSearch;
import exceptions.InvalidHeaderException;
import headers.Enumerations.Encoding;
import headers.Enumerations.Locale;
import headers.Enumerations.MobiType;

public class MobiDocHeader {

    /**
     * A placeholder for all known fields. This is mainly useful for
     * new headers and allows us to store some defaults
     */
    public static final ByteFieldSet ALL_FIELDS   = new ByteFieldSet();

    /**
     * The number of 'extra index' fields. These are unused
     */
    public static final short       INDEX_EXTRAS = 6;
    
    /**
     * Populate the ALL_FIELDS ByteFieldSet
     */
    static {
        ALL_FIELDS.add(new StringByteField(4, "Identifier", CHARSET, "MOBI"));
        ALL_FIELDS.add(new IntByteField("Length"));
        ALL_FIELDS.add(new IntByteField("MobiType", MobiType.PALM_BOOK
                .getValue()));
        ALL_FIELDS.add(new IntByteField("Encoding", Encoding.UTF8.getValue()));
        ALL_FIELDS.add(new IntByteField("Unique ID"));
        ALL_FIELDS.add(new IntByteField("Version", 6));
        ALL_FIELDS.add(new IntByteField("Orthographic Index", -1));
        ALL_FIELDS.add(new IntByteField("Inflexion Index", -1));
        ALL_FIELDS.add(new IntByteField("Index Names", -1));
        ALL_FIELDS.add(new IntByteField("Index Keys", -1));
        for (int i = 0; i < INDEX_EXTRAS; i++)
            ALL_FIELDS.add(new IntByteField(String
                    .format("Index Extra %1$d", i), -1));
        ALL_FIELDS.add(new IntByteField("First Non-Book Record", -1));
        ALL_FIELDS.add(new IntByteField("Full Name Offset"));
        ALL_FIELDS.add(new IntByteField("Full Name Length"));
        ALL_FIELDS
                .add(new IntByteField("Locale", Locale.UK_ENGLISH.getValue()));
        ALL_FIELDS.add(new IntByteField("Language: Input"));
        ALL_FIELDS.add(new IntByteField("Language: Output"));
        ALL_FIELDS.add(new IntByteField("Minimum Version Compatible Reader"));
        ALL_FIELDS.add(new IntByteField("First Image Record", -1));
        ALL_FIELDS.add(new IntByteField("First Huffman Record", -1));
        ALL_FIELDS.add(new IntByteField("Huffman Record Count"));
        ALL_FIELDS.add(new IntByteField("Huffman Table Offset"));
        ALL_FIELDS.add(new IntByteField("Huffman Table Length"));
        ALL_FIELDS.add(new IntByteField("EXTH Flags"));
        ALL_FIELDS.add(new ByteField(32, ByteField.FieldType.BYTE,
                "32 Unknown Bytes"));
        ALL_FIELDS.add(new IntByteField("DRM Offset", -1));
        ALL_FIELDS.add(new IntByteField("DRM Count", -1));
        ALL_FIELDS.add(new IntByteField("DRM Size"));
        ALL_FIELDS.add(new IntByteField("DRM Flags"));
        ALL_FIELDS.add(new ByteField(12, ByteField.FieldType.BYTE,
                "12 Unknown Bytes"));
        ALL_FIELDS.add(new ShortByteField("First Content Record", (short) -1));
        ALL_FIELDS.add(new ShortByteField("Last Content Record", (short) -1));
        ALL_FIELDS.add(new IntByteField("Unknown Integer", 1));
        ALL_FIELDS.add(new IntByteField("FCIS Record", -1));
        ALL_FIELDS.add(new IntByteField("FCIS Count"));
        ALL_FIELDS.add(new IntByteField("FLIS Record", -1));
        ALL_FIELDS.add(new IntByteField("FLIS Count"));
        ALL_FIELDS.add(new ByteField(8, ByteField.FieldType.BYTE,
                "8 Unknown Bytes"));
        ALL_FIELDS.add(new IntByteField("Unknown Integer", -1));
        ALL_FIELDS.add(new IntByteField("Unknown Integer"));
        ALL_FIELDS.add(new IntByteField("Unknown Integer", -1));
        ALL_FIELDS.add(new IntByteField("Unknown Integer", -1));
        ALL_FIELDS.add(new IntByteField("Extra Record Flags"));
        ALL_FIELDS.add(new IntByteField("INDX Record", -1));
    }

    private ExthHeader               exth;

    private ByteFieldSet             fields;

    public MobiDocHeader() {
        fields = ALL_FIELDS.clone();
    }

    public MobiDocHeader(ByteBuffer in) throws InvalidHeaderException {
        this();
        parse(in);
    }

    public Encoding getEncoding() {
        return Encoding.valueOf(fields.<IntByteField>getAs("Encoding").getValue());
    }

    public int getExtendedFlags() {
        return fields.<IntByteField>getAs("Extra Record Flags").getValue();
    }

    public ExthHeader getExthHeader() {
        return exth;
    }

    public int getFcisRecord() {
        return fields.<IntByteField>getAs("FCIS Record").getValue();
    }

    public ByteFieldSet getFields() {
        return fields;
    }

    public short getFirstContentRecord() {
        return fields.<ShortByteField>getAs("First Content Record").getValue();
    }

    public int getFirstImageRecord() {
        return fields.<IntByteField>getAs("First Image Record").getValue();
    }

    public int getFirstNonBookRecord() {
        return fields.<IntByteField>getAs("First Non-Book Record").getValue();
    }

    public int getFlisRecord() {
        return fields.<IntByteField>getAs("FLIS Record").getValue();
    }

    public int getFullNameLength() {
        return fields.<IntByteField>getAs("Full Name Length").getValue();
    }

    public int getFullNameOffset() {
        return fields.<IntByteField>getAs("Full Name Offset").getValue();
    }

    public int getHuffmanCount() {
        return fields.<IntByteField>getAs("Huffman Record Count").getValue();
    }

    public int getHuffmanRecord() {
        return fields.<IntByteField>getAs("First Huffman Record").getValue();
    }

    public int getIndxRecord() {
        return fields.<IntByteField>getAs("INDX Record").getValue();
    }

    public short getLastContentRecord() {
        return fields.<ShortByteField>getAs("Last Content Record").getValue();
    }

    public int getLength() {
        int len = fields.length();
        if (exth != null)
            len += exth.getLength();
        return len;
    }

    public MobiType getType() {
        return MobiType.valueOf(fields.<IntByteField>getAs("MobiType").getValue());
    }

    public void parse(ByteBuffer in) throws InvalidHeaderException {
        int offset = KmpSearch.indexOf(in.array(),
                new String("MOBI").getBytes(PdbHeader.CHARSET));
        if (offset >= 0) {
            in.position(offset - in.arrayOffset());
            in = in.slice();
        } else
            throw new InvalidHeaderException();
        int max_len = fields.length();
        fields.parseBetween(in, 0, 8);
        int len = fields.<IntByteField>getAs("Length").getValue();
        fields.parseBetween(in, 8, len > 0 ? len : max_len);
        try {
            System.out.println("Extracting ExthHeader...");
            exth = new ExthHeader(in, getEncoding().getCharset());
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }
    }

    public void setEncoding(Encoding e) {
        fields.<IntByteField>getAs("Encoding").setValue(e.getValue());
    }

    public void setExtendedFlags(int i) {
        fields.<IntByteField>getAs("Extra Record Flags").setValue(i);
    }

    public void setExthHeader(boolean enable) {
        switch (enable ? 0 : 1) {
        case 0:
            fields.<IntByteField>getAs("EXTH Flags").setValue(0x50);
            if (exth != null)
                break;
            exth = new ExthHeader(getEncoding().getCharset());
            break;
        case 1:
            fields.<IntByteField>getAs("EXTH Flags").setValue(0x0);
            exth = null;
        }
    }

    public void setFcisRecord(int i) {
        fields.<IntByteField>getAs("FCIS Record").setValue(i);
    }

    public void setFirstContentRecord(int i) {
        fields.<ShortByteField>getAs("First Content Record").setValue((short) i);
    }

    public void setFirstImageRecord(int i) {
        fields.<IntByteField>getAs("First Image Record").setValue(i);
    }

    public void setFirstNonBookRecord(int i) {
        fields.<IntByteField>getAs("First Non-Book Record").setValue(i);
    }

    public void setFlisRecord(int i) {
        fields.<IntByteField>getAs("FLIS Record").setValue(i);
    }

    public void setFullNameLength(int i) {
        fields.<IntByteField>getAs("Full Name Length").setValue(i);
    }

    public void setFullNameOffset(int i) {
        fields.<IntByteField>getAs("Full Name Offset").setValue(i);
    }

    public void setHuffmanCount(int i) {
        fields.<IntByteField>getAs("Huffman Record Count").setValue(i);
    }

    public void setHuffmanRecord(int i) {
        fields.<IntByteField>getAs("First Huffman Record").setValue(i);
    }

    public void setIndxRecord(int i) {
        fields.<IntByteField>getAs("INDX Record").setValue(i);
    }

    public void setLastContentRecord(int i) {
        fields.<ShortByteField>getAs("Last Content Record").setValue((short) i);
    }

    public void setType(MobiType i) {
        fields.<IntByteField>getAs("MobiType").setValue(i.getValue());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[::::MobiDocHeader::::]\n");
        sb.append(fields);
        return sb.toString();
    }

    public void write(ByteBuffer out) {
        fields.<IntByteField>getAs("Length").setValue(fields.length());
        fields.write(out);
        if (exth != null)
            exth.write(out);
    }
}
