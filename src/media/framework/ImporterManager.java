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

import media.framework.ImportFramework.BookData;
import media.framework.MediaLoader.Importer;

class ImporterManager implements Importer {
    
    PdfImporter  pdf_imp = new PdfImporter();
    HtmlImporter htm_imp = new HtmlImporter();
    TextImporter txt_imp = new TextImporter();
    
    Importer last_imp;

    /* (non-Javadoc)
     * @see media.framework.MediaLoader.Importer#doImport(byte[])
     */
    @Override
    public boolean doImport(byte[] data) {
        last_imp = null;
        
        if (pdf_imp.doImport(data)) {
            last_imp = pdf_imp;
            return true;
        }
        
        if (htm_imp.doImport(data)) {
            last_imp = htm_imp;
            return true;
        }
        
        if (txt_imp.doImport(data)) {
            last_imp = txt_imp;
            return true;
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see media.framework.MediaLoader.Importer#data()
     */
    @Override
    public BookData data() {
        return last_imp != null ? last_imp.data() : null;
    }

}