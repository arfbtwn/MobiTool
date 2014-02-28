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
package editorkit;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

@SuppressWarnings("serial")
public class MobiDocument extends HTMLDocument {

    public MobiDocument() {
    }

    public MobiDocument(StyleSheet styles) {
        super(styles);
    }

    public MobiDocument(Content c, StyleSheet styles) {
        super(c, styles);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.html.HTMLDocument#getReader(int)
     */
    @Override
    public ParserCallback getReader(int pos) {
        return new MobiReader(pos);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.html.HTMLDocument#getReader(int, int, int,
     * javax.swing.text.html.HTML.Tag)
     */
    @Override
    public ParserCallback getReader(int pos, int popDepth, int pushDepth,
            Tag insertTag) {
        return new MobiReader(pos, popDepth, pushDepth, insertTag);
    }

    /**
     * This pretty much just allows us to see what the parser is doing
     * 
     */
    public class MobiReader extends HTMLReader {

        public MobiReader(int offset) {
            super(offset);
            registerActions();
        }

        public MobiReader(int offset, int popDepth, int pushDepth, Tag insertTag) {
            super(offset, popDepth, pushDepth, insertTag);
            registerActions();
        }

        protected void registerActions() {
            registerTag(new ReferenceTag(), new HiddenAction());
            registerTag(new Tag("guide", false, false) {
            }, new HiddenAction());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLDocument.HTMLReader#handleComment(char[],
         * int)
         */
        @Override
        public void handleComment(char[] data, int pos) {
            System.out.println("handleComment: " + data.toString());
            super.handleComment(data, pos);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLDocument.HTMLReader#handleEndOfLineString
         * (java.lang.String)
         */
        @Override
        public void handleEndOfLineString(String eol) {
            System.out.println("handleEndOfLineString: " + eol);
            super.handleEndOfLineString(eol);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLDocument.HTMLReader#handleEndTag(javax.
         * swing.text.html.HTML.Tag, int)
         */
        @Override
        public void handleEndTag(Tag t, int pos) {
            System.out.println("handleEndTag: " + t.toString());
            super.handleEndTag(t, pos);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLEditorKit.ParserCallback#handleError(java
         * .lang.String, int)
         */
        @Override
        public void handleError(String errorMsg, int pos) {
            System.out.println("handleError: " + errorMsg);
            super.handleError(errorMsg, pos);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLDocument.HTMLReader#handleStartTag(javax
         * .swing.text.html.HTML.Tag, javax.swing.text.MutableAttributeSet, int)
         */
        @Override
        public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
            System.out.println("handleStartTag: " + t.toString());
            super.handleStartTag(t, a, pos);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.text.html.HTMLDocument.HTMLReader#handleText(char[],
         * int)
         */
        @Override
        public void handleText(char[] data, int pos) {
            System.out.println("handleSimpleTag: " + data.toString());
            super.handleText(data, pos);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.text.html.HTMLDocument.HTMLReader#handleSimpleTag(javax
         * .swing.text.html.HTML.Tag, javax.swing.text.MutableAttributeSet, int)
         */
        @Override
        public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            System.out.println("handleSimpleTag: " + t.toString());
            super.handleSimpleTag(t, a, pos);
        }
    }

    public static class ReferenceTag extends Tag {

        public ReferenceTag() {
            super("reference", false, false);
        }
    }
}
