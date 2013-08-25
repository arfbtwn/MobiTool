/**
 * Copyright (C) 2013 
 * Nicholas J. Little <arealityfarbetween@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.Element;

import little.nj.util.FileUtil;
import little.nj.util.StringUtil;
import little.nj.util.StreamUtil.InputAction;


public class HtmlImporter {
    
    private String title;
    
    private static final Comparator<Element> offsetComparator = new Comparator<Element>() {

        @Override
        public int compare(Element o1, Element o2) {
            return o1.getStartOffset() - o2.getStartOffset();
        }
    };
    
    private TreeMap<Element, String> map = new TreeMap<>(offsetComparator);
    
    private HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
    private StyleSheet styles = doc.getStyleSheet();
    
    public HtmlImporter() {
        doc.setPreservesUnknownTags(true);
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
    }
    
    public String getTitle() {
        if (title == null) {
            Object fromDoc = doc.getProperty(HTMLDocument.TitleProperty);
            title = fromDoc == null ? StringUtil.EMPTY_STRING : (String) fromDoc;
        }
        return title;
    }
    
    public HTMLDocument getDocument() { return doc; }
    
    public boolean readFromFile(File file) {
        
        FileUtil futil = new FileUtil();
        
        return futil.read(file, readAction);
    }
    
    public void stripParagraphStyle() {
        Enumeration<?> rules = styles.getStyleNames();
        while(rules.hasMoreElements()) {
            String name = (String)rules.nextElement();
            
            if (name.equals("p")) {
                Style style = styles.getStyle(name);
                
                Enumeration<?> pairs = style.getAttributeNames();
                
                while(pairs.hasMoreElements()) {
                     Object attr = pairs.nextElement();
                    
                    if (attr instanceof CSS.Attribute && 
                            attr.toString().startsWith("margin")) {
                        style.removeAttribute(attr);
                    }
                }
            }
        }
    }
    
    private static final String FMT_REF_TAG = "<reference type=\"toc\" title=\"Table of Contents\" filepos=%010d/>";
    private static final String FMT_TOC_TAG = "<a filepos=%010d>%s</a>";
    
    public void generateMobiToc() {
        System.out.println(String.format("Title: '%s'", getTitle()));
        
        Element elem = doc.getDefaultRootElement();
        
        walkTree(elem);
        
        int elems = elem.getElementCount();
        
        if (elems == 2) {
            try {
                // Insert head element <reference type="toc" title="Table of Contents" filepos=0000000000/>
                Element head = elem.getElement(0);         
                Element body = elem.getElement(1);
                
                int offset = body.getEndOffset() + FMT_REF_TAG.getBytes().length + "00000".getBytes().length;
                
                doc.insertBeforeEnd(head, String.format(FMT_REF_TAG, offset));
                StringBuilder toc = new StringBuilder();
                
                // Append new division <div>
                toc.append("<div>");
                
                // Insert element for toc heading <h1>Table of Contents</h1>
                toc.append("<h1>Table of Contents</h1>");
                
                // Insert elements for each toc element <a filepos=0000000000>map.get(elem)</a>
                for(Map.Entry<Element, String> i : map.entrySet())
                {
                    toc.append(String.format(FMT_TOC_TAG, i.getKey().getStartOffset(), i.getValue()));
                }
                
                // Add <mbp:pagebreak /> ?
                
                // Close division </div>
                toc.append("</div>");
                
                doc.insertBeforeEnd(body, toc.toString());
                
                kit.write(System.out, doc, 0, doc.getLength());
                
            } catch (BadLocationException | IOException e) {
            }
        }
    }
    
    private void walkTree(Element elem) {
        String nameLower = elem.getName().toLowerCase();
        
        if (nameLower.equalsIgnoreCase(HTML.Tag.OL.toString()) ||
            nameLower.equalsIgnoreCase(HTML.Tag.UL.toString()) ||
            nameLower.equalsIgnoreCase(HTML.Tag.DL.toString()))
            return;
        
        if (nameLower.startsWith("h") && nameLower.length() <= 2) {
             // found a heading, can headings have children?
            int start = elem.getStartOffset();
            int length = elem.getEndOffset() - start;
            
            try {
                String heading = elem.getDocument().getText(start, length).trim();
                
                if (!heading.equals(getTitle())) {
                    map.put(elem, heading);
                
                    System.out.println(
                            String.format("Found %s at %d: '%s'", 
                                          elem.getName(), 
                                          elem.getStartOffset(), 
                                          heading));
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            
        } else {
            for (int i=0; i<elem.getElementCount(); ++i) {
                walkTree(elem.getElement(i));
            }
        }
    }
    
    private final InputAction readAction = new InputAction() {
        @Override
        public void act(InputStream stream) throws IOException {
            try {
                doc.remove(0, doc.getLength());
                kit.read(stream, doc, 0);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }                    
        }
    };
    
}
