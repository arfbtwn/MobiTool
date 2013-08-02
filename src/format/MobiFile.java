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
package format;

import headers.MobiDocHeader;
import headers.PalmDocHeader;
import interfaces.IManageCodecs;

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

import records.PdbRecord;
import exceptions.InvalidHeaderException;

public class MobiFile extends PdbFile {

    public static final String  IMAGE_EXT = "jpg";

    public IManageCodecs        codec_manager;

    private List<BufferedImage> images    = new LinkedList<BufferedImage>();

    private MobiDocHeader       mobi;

    /*
     * Headers
     */
    private PalmDocHeader       palm;

    /*
     * Content
     */
    private PalmDocText         text;

    /*
     * Header location
     */
    private PdbRecord           zero;

    public MobiFile(File in, IManageCodecs codecs) throws IOException,
            InvalidHeaderException {
        super(in);
        codec_manager = codecs;
        parse();
    }

    public MobiFile(IManageCodecs codecs) {
        zero = new PdbRecord();
        palm = new PalmDocHeader();
        mobi = new MobiDocHeader();
        text = new PalmDocText(mobi.getEncoding());
        codec_manager = codecs;
        getToc().iterator().add(zero);
    }

    protected void adjustRecordPointers(int from, int count) {
        int last = mobi.getLastContentRecord();
        int[] idxs = { 
                mobi.getFirstNonBookRecord(),
                
                // Not present == -1
                mobi.getIndxRecord(), 
                mobi.getFirstImageRecord(),
                // this.mobi.getLastContentRecord(),
                
                // Not present == 0
                mobi.getHuffmanRecord(),
                
                // Not present == -1
                mobi.getFlisRecord(), 
                mobi.getFcisRecord() 
                };
        
        adjustRecordPointers(from, count, idxs);
        
        mobi.setFirstNonBookRecord(idxs[0]);
        mobi.setIndxRecord(idxs[1]);
        mobi.setFirstImageRecord(idxs[2]);
        // this.mobi.setLastContentRecord(idxs[3]);
        mobi.setLastContentRecord(last + count);
        mobi.setHuffmanRecord(idxs[3]);
        mobi.setFlisRecord(idxs[4]);
        mobi.setFcisRecord(idxs[5]);
    }

    protected void adjustRecordPointers(int from, int count, int[] idxs) {
        if (count == 0)
            return;
        for (int i = 0; i < idxs.length; ++i) {
            if (idxs[i] < from)
                continue;
            if (idxs[i] >= from)
                idxs[i] += count;
            if (idxs[i] < from)
                idxs[i] = -1;
        }
    }

    protected void buildRecordZero() {
        int length = PalmDocHeader.LENGTH + mobi.getLength();
        /*
         * Update mobi header information
         */
        byte[] name = getTitle().getBytes(mobi.getEncoding().getCharset());
        mobi.setFullNameOffset(length);
        mobi.setFullNameLength(name.length);
        /*
         * Calculate extra length for padding
         */
        length += name.length + 2;
        if (length % 4 != 0)
            length += 4 - length % 4;
        ByteBuffer buf = ByteBuffer.allocate(length);
        /*
         * Write palm and mobi headers to buffer
         */
        palm.getFields().write(buf);
        mobi.write(buf);
        buf.put(name);
        zero.setBytes(buf);
    }

    protected void extractContent() {
        extractText();
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
                    bis = new ByteArrayInputStream(it.next().getBytes());
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
            ListIterator<?> it = getToc().iterator(record);
            text.setCodec(codec_manager.getCodec(palm.getCompression()
                    .toString()));
            while (count-- > 0)
                text.addToFile(((PdbRecord) it.next()).getBuffer());
        }
    }

    public String getAuthor() {
        try {
            return mobi.getExthHeader().getAuthor();
        } catch (Exception e) {
        }
        return "";
    }

    public String getBlurb() {
        try {
            return mobi.getExthHeader().getBlurb();
        } catch (Exception e) {
        }
        return "";
    }

    public BufferedImage getCover() {
        try {
            return images.get(mobi.getExthHeader().getCover());
        } catch (Exception e) {
        }
        return null;
    }

    public BufferedImage getCoverOrThumb() {
        BufferedImage cover = getCover();
        return cover != null ? cover : getThumb();
    }

    public List<BufferedImage> getImages() {
        return images;
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

    public BufferedImage getThumb() {
        try {
            return images.get(mobi.getExthHeader().getThumb());
        } catch (Exception e) {
        }
        return null;
    }

    public String getTitle() {
        try {
            return mobi.getExthHeader().getTitle();
        } catch (Exception e) {
        }
        return getHeader().getName();
    }

    public void importFromHtml(File file) throws IOException {
        getText().readFromFile(file);
    }

    protected void insertContent() {
        int record = 1;
        mobi.setFirstContentRecord(record);
        insertText(record);
        record = mobi.getFirstImageRecord();
        if (record <= 0)
            record = mobi.getLastContentRecord() + 1;
        insertImages(record);
    }

    protected void insertImages(int record) {
        if (images.size() == 0)
            return;
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
        this.adjustRecordPointers(record, images.size());
        setImagePointers(record, images.size());
    }

    protected void insertText(int record) {
        text.setCodec(codec_manager.getCodec(palm.getCompression().toString()));
        byte[][] records = text.getRecords();
        if (records.length == 0)
            return;
        ListIterator<PdbRecord> it = getToc().iterator(record);
        for (byte[] i : records)
            it.add(new PdbRecord(i));
        /*
         * Update mobi header
         */
        mobi.setEncoding(text.getEncoding());
        this.adjustRecordPointers(record, records.length);
        setTextPointers(record, records.length);
        palm.setUncompressedTextLength(text.getUncompressedLength());
    }

    protected void parse() throws InvalidHeaderException {
        zero = getToc().getRecord(0);
        ByteBuffer _zero = zero.getBuffer();
        System.out.println("Extracting Palm Header...");
        palm = new PalmDocHeader(_zero);
        System.out.println("Extracting Mobi Header...");
        mobi = new MobiDocHeader(_zero);
        text = new PalmDocText(palm.getTextRecordLength(), mobi.getEncoding());
        System.out.println("Extracting Text...");
        extractText();
        System.out.println("Extracting Images...");
        extractImages();
    }
    
    /* (non-Javadoc)
     * @see format.PdbFile#reload()
     */
    @Override
    public void reload() throws IOException {
        super.reload();
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void refresh() {
        removeText();
        removeImages();
        insertContent();
        buildRecordZero();
    }

    protected void removeImages() {
        int record = mobi.getFirstImageRecord();
        int end = mobi.getLastContentRecord();
        int count = end - record + 1;
        if (record > 0 && count > 0) {
            ListIterator<PdbRecord> it = getToc().iterator(record);
            this.adjustRecordPointers(record, -count);
            while (count-- > 0) {
                it.next();
                it.remove();
            }
        }
    }

    protected void removeText() {
        int record = mobi.getFirstContentRecord();
        int count = palm.getTextRecordCount();
        if (record > 0) {
            ListIterator<?> it = getToc().iterator(record);
            this.adjustRecordPointers(record, -count);
            while (count-- > 0) {
                it.next();
                it.remove();
            }
            palm.setTextRecordCount(0);
            palm.setUncompressedTextLength(0);
        }
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
                images.add(cover);
            }
            mobi.getExthHeader().setThumb(index);
        }
    }

    protected void setImagePointers(int first, int count) {
        if (count == 0)
            return;
        mobi.setFirstImageRecord(first);
        mobi.setLastContentRecord(first + count - 1);
        int fnonbook = mobi.getFirstNonBookRecord();
        if (fnonbook <= 0)
            mobi.setFirstNonBookRecord(first);
    }

    protected void setTextPointers(int first, int count) {
        if (count == 0)
            return;
        palm.setTextRecordCount(count);
        mobi.setFirstContentRecord(first);
        int last = mobi.getLastContentRecord();
        if (last <= 0)
            mobi.setLastContentRecord(first + count - 1);
    }

    public void setTitle(String x) {
        getHeader().setName(x);
        if (mobi.getExthHeader() != null)
            mobi.getExthHeader().setTitle(x);
    }

    @Override
    public boolean writeToFile(File out) {
        //refresh();
        return super.writeToFile(out);
    }
}
