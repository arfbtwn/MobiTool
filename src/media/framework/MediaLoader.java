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
package media.framework;

import java.net.URI;

import media.framework.ImportFramework.BookData;
import media.framework.ImportFramework.LoaderImporter;


public class MediaLoader implements LoaderImporter {

    /**
     * Loads data from a URI
     */
    public static interface Loader {
        boolean load(URI uri);
        
        byte[] data();
    }
    
    /**
     * Converts data to HTML format text and a list of images
     */
    public static interface Importer {
        boolean doImport(byte[] data);
        
        BookData data();
    }
    
    BookData data;
    
    @Override
    public boolean load(URI location) {
        data = null;
        
        Loader loader = getLoader();
        
        if (!loader.load(location))
            return false;
        
        Importer imp = getImporter();
            
        if (!imp.doImport(loader.data()))
            return false;
        
        data = imp.data();
        
        return true;
    }

    @Override
    public BookData data() { return data; }
    
    protected Loader getLoader() { return new UriLoader(); }
    
    protected Importer getImporter() { return new ImporterManager(); }
}