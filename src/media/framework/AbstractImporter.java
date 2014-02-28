/**
 * Copyright (C) 2014 
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
package media.framework;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import media.framework.ImportFramework.BookData;
import media.framework.MediaLoader.Importer;

public abstract class AbstractImporter implements Importer {
    
    String title, author, blurb, text;
    
    List<Image> images = new ArrayList<Image>();
    
    protected void title  (String title)  { this.title = title; }
    protected void author (String author) { this.author = author; }
    protected void blurb  (String blurb)  { this.blurb = blurb; }
    protected void text   (String text)   { this.text =  text; }
    protected void image  (Image i)       { images.add(i); }
    
    @Override
    public BookData data() {
        return data;
    }
    
    BookData data = new BookData() {
        public String title() { return title; }
        public String author() { return author; }
        public String blurb() { return blurb; }
        
        public String text() { return text; }
        public Iterable<Image> images() { return images; }
    };
}
