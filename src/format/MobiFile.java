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

import format.headers.InvalidHeaderException;
import format.headers.MobiDocHeader;
import format.headers.PalmDocHeader;
import format.records.EofRecord;
import format.records.FcisRecord;
import format.records.FlisRecord;
import format.records.PdbRecord;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import little.nj.adts.ByteFieldMapSet;
import little.nj.adts.IntByteField;
import little.nj.util.StringUtil;

public class MobiFile extends PdbFile {

    public static final String IMAGE_EXT = "jpg";

    public CodecManager codec_manager;

    private List<BufferedImage> images = new LinkedList<BufferedImage>();

    private MobiDocHeader mobi;

    private PalmDocHeader palm;

    private PalmDocText text;

    private PdbRecord zero;

    private boolean write_flis, write_fcis;

    public MobiFile(CodecManager codecs) {
        zero = new PdbRecord();
        palm = new PalmDocHeader();
        mobi = new MobiDocHeader();
        text = new PalmDocText(mobi.getEncoding());
        codec_manager = codecs;
    }

    public MobiFile(File in, CodecManager codecs) throws IOException,
            InvalidHeaderException {
        super(in);
        codec_manager = codecs;
        parse();
    }

    protected void parse() throws InvalidHeaderException {
        zero = getToc().iterator().next();
        ByteBuffer _zero = zero.getBuffer();
        System.out.println("Extracting Palm Header...");
        palm = PalmDocHeader.parseBuffer(_zero);
        System.out.println("Extracting Mobi Header...");
        mobi = MobiDocHeader.parseBuffer(_zero);
        text = new PalmDocText(palm.getTextRecordLength(), mobi.getEncoding());
        extractContent();
    }

    protected void extractContent() {
        System.out.println("Extracting Text...");
        extractText();
        System.out.println("Extracting Images...");
        extractImages();
    }

    protected void extractImages() {
        int record = mobi.getFirstImageRecord();
        int end = mobi.getLastContentRecord();
        if (record > 0) {
            ListIterator<PdbRecord> it = getToc().iterator(record);
            ByteArrayInputStream bis;
            ImageInputStream iis;
            BufferedImage img;

            while (it.nextIndex() <= end)
                try {
                    bis = new ByteArrayInputStream(it.next().getData());
                    iis = ImageIO.createImageInputStream(bis);
                    img = ImageIO.read(iis);
                    if (img != null)
                        images.add(img);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }

    protected void extractText() {
        int record = mobi.getFirstContentRecord();
        int count = palm.getTextRecordCount();
        if (record > 0) {
            ensureTextCodec();
            ListIterator<PdbRecord> it = getToc().iterator(record);

            while (count-- > 0) {
                text.addToFile(it.next().getBuffer());
            }
        }
    }

    public MobiDocHeader getMobiDocHeader() {
        return mobi;
    }

    public PalmDocHeader getPalmDocHeader() {
        return palm;
    }

    public PalmDocText getText() {
        return text;
    }

    public List<BufferedImage> getImages() {
        return images;
    }

    public String getTitle() {
        if (!mobi.hasExthHeader())
            return getHeader().getName();

        return mobi.getExthHeader().getTitle();
    }

    public String getAuthor() {
        if (!mobi.hasExthHeader())
            return StringUtil.EMPTY_STRING;

        return mobi.getExthHeader().getAuthor();
    }

    public String getBlurb() {
        if (!mobi.hasExthHeader())
            return StringUtil.EMPTY_STRING;

        return mobi.getExthHeader().getBlurb();
    }

    public BufferedImage getCover() {
        if (!mobi.hasExthHeader())
            return null;
        
        int x = mobi.getExthHeader().getCover();
        
        if (x < 0)
            return null;
        
        return images.get(x);
    }

    public BufferedImage getThumb() {
        if (!mobi.hasExthHeader())
            return null;
        
        int x = mobi.getExthHeader().getThumb();
        if (x < 0)
            return null;
        
        return images.get(x);
    }

    public BufferedImage getCoverOrThumb() {
        BufferedImage cover = getCover();
        return cover != null ? cover : getThumb();
    }

    public void setTitle(String x) {
        getHeader().setName(x);
        mobi.getExthHeader().setTitle(x);
    }

    public void setAuthor(String x) {
        mobi.setExthHeader(true);
        mobi.getExthHeader().setAuthor(x);
    }

    public void setBlurb(String x) {
        mobi.setExthHeader(true);
        mobi.getExthHeader().setBlurb(x);
    }

    public void setCovers(BufferedImage... covers) {
        BufferedImage cover = null;
        BufferedImage thumb = null;
        if (covers.length > 1) {
            cover = covers[0];
            thumb = covers[1];
        } else if (covers.length > 0) {
            cover = covers[0];
            thumb = cover;
        }
        if (cover != null) {
            mobi.setExthHeader(true);
            int index = images.indexOf(cover);
            if (index < 0) {
                index = images.size();
                images.add(cover);
            }
            mobi.getExthHeader().setCover(index);
            index = images.indexOf(thumb);
            if (index < 0) {
                index = images.size();
                images.add(thumb);
            }
            mobi.getExthHeader().setThumb(index);
        }
    }

    @Override
    public boolean writeToFile(File out) {
        refresh();
        return super.writeToFile(out);
    }

    public final void refresh() {
        buildFile();
    }

    protected void buildFile() {
        getHeader().setBookType("BOOK");
        getHeader().setCreator(MobiDocHeader.MOBI);

        getToc().clear(); // Clear the toc
        getToc().iterator().add(zero); // Place record zero
        int curr = 1; // Start placing from first record

        /*
         * Set any unsupported record pointers as they 
         * will have been stripped
         */
        setUnsupportedRecordPointers();

        /*
         * Insert text, set pointers: 
         * - First Content Record 
         * - First Non Book Record
         */
        curr += insertText(curr);

        int fnonbook = curr;
        /*
         * Insert images, set pointers: 
         * - First Image Record
         */
        curr += insertImages(curr);

        /*
         * If we inserted anything after the book we 
         * need to set the first non book record
         */
        if (fnonbook < curr) {
            mobi.setFirstNonBookRecord(fnonbook);
        }

        /*
         * Done inserting content
         */
        mobi.setLastContentRecord(curr - 1);

        curr += insertFlis(curr);

        curr += insertFcis(curr);

        insertEof(curr);

        buildRecordZero();
    }

    protected void setUnsupportedRecordPointers() {
        /*
         * FIXME: Deal with these records
         */
        mobi.setFcisRecord(-1);
        mobi.setFlisRecord(-1);
        mobi.setHuffmanRecord(0);
        mobi.setIndxRecord(-1);
        mobi.setHuffmanCount(0);
    }

    protected int insertText(int record) {
        ensureTextCodec();
        byte[][] records = text.getCompressedRecords();
        ListIterator<PdbRecord> it = getToc().iterator(record);
        for (byte[] i : records)
            it.add(new PdbRecord(i));

        /*
         * Update mobi header
         */
        mobi.setEncoding(text.getEncoding());
        mobi.setFirstContentRecord(record);

        /*
         * And palm
         */
        palm.setUncompressedTextLength(text.getUncompressedLength());
        palm.setTextRecordCount(records.length);

        return records.length;
    }

    protected void ensureTextCodec() {
        Codec codec = codec_manager.getCodec(palm.getCompression().toString());
        getText().setCodec(codec);
    }

    protected int insertImages(int record) {
        ListIterator<PdbRecord> it_toc = getToc().iterator(record);
        Iterator<BufferedImage> it_img = images.iterator();
        while (it_img.hasNext())
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
                ImageIO.write(it_img.next(), "jpg", ios);
                ios.close();
                it_toc.add(new PdbRecord(bos.toByteArray()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        /*
         * Update Mobi image record pointer
         */
        mobi.setFirstImageRecord(images.size() > 0 ? record : -1);

        return images.size();
    }

    protected int insertFlis(int record) {
        if (write_flis) {
            mobi.setFlisRecord(record);
            PdbRecord flis = new PdbRecord(FlisRecord.getFields().getBuffer()
                    .array());
            getToc().iterator(record).add(flis);

            return 1;
        }

        return 0;
    }

    protected int insertFcis(int record) {
        if (write_fcis) {
            mobi.setFcisRecord(record);
            ByteFieldMapSet fcis = FcisRecord.getFields();

            fcis.<IntByteField> getAs("Text Length").setValue(
                    palm.getUncompressedTextLength());

            PdbRecord rec = new PdbRecord(fcis.getBuffer().array());
            getToc().iterator(record).add(rec);

            return 1;
        }

        return 0;
    }

    protected int insertEof(int record) {
        PdbRecord eof = new PdbRecord(EofRecord.getFields().getBuffer().array());

        getToc().iterator(record).add(eof);

        return 1;
    }

    /**
     * Populate record zero with the {@link PalmDocHeader},
     * {@link MobiDocHeader} and Book Title
     */
    protected void buildRecordZero() {
        int length = PalmDocHeader.LENGTH + mobi.getLength();

        /*
         * Update mobi header information
         */
        byte[] title = getTitle().getBytes(mobi.getEncoding().getCharset());
        mobi.setFullNameOffset(length);
        mobi.setFullNameLength(title.length);

        /*
         * Calculate extra length for padding, 2 bytes
         * after the title to a multiple of 4
         */
        length += title.length + 2;
        if (length % 4 != 0)
            length += 4 - length % 4;
        ByteBuffer buf = ByteBuffer.allocate(length);

        /*
         * Write palm and mobi headers to buffer
         */
        palm.getFields().write(buf);
        mobi.write(buf);
        buf.put(title);
        zero.setData(buf);
    }
}
