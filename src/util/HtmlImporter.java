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
import java.util.Enumeration;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.InputAction;


public class HtmlImporter {

    private HTMLEditorKit kit;
    private HTMLDocument doc;
    private StyleSheet styles;
    
    public HtmlImporter() {
        kit = new HTMLEditorKit();
        doc = (HTMLDocument)kit.createDefaultDocument();
        styles = doc.getStyleSheet();
        
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
    }
    
    public HTMLDocument getDocument() { return doc; }
    
    public boolean readFromFile(File file) {
        
        FileUtil futil = FileUtil.getInstance();
        
        return futil.readFile(file, readAction);
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
