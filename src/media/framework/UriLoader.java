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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import little.nj.util.StreamUtil;
import little.nj.util.StreamUtil.InputAction;


class UriLoader extends ThreadSafeLoader {

    final static StreamUtil util = new StreamUtil();
    
    byte[] buffer = new byte[1024 * 1000]; // 1 MB buffer
    
    @Override
    protected boolean loadImpl(URI uri) {

        data(null);
        
        InputStream stream = null;
        try {
            stream = uri.toURL().openStream();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return util.read(stream, new InputAction() {

            @Override
            public void act(InputStream stream) throws IOException {
                ByteArrayOutputStream bos = 
                        new ByteArrayOutputStream(buffer.length);
                
                int b;
                while((b = stream.read(buffer)) > -1)
                    bos.write(buffer, 0, b);
                
                data(bos.toByteArray());
                
            } });
    }
}