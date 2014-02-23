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

import format.headers.Enumerations.Compression;
import interfaces.ICodec;
import interfaces.IManageCodecs;

import java.util.HashMap;

import algorithms.HuffCdicCodec;
import algorithms.PalmDocCodec;
import algorithms.RawCodec;

@SuppressWarnings("serial")
public class CodecManager extends HashMap<String, ICodec> implements
        IManageCodecs {

    public CodecManager() {
        put(Compression.HUFF_CDIC.toString(), new HuffCdicCodec());
        put(Compression.PALMDOC.toString(), new PalmDocCodec());
        put(Compression.NONE.toString(), new RawCodec());
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.IManageCodecs#get(java.lang.String)
     */
    @Override
    public ICodec getCodec(String codec) {
        return get(codec);
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.IManageCodecs#getKeys()
     */
    @Override
    public String[] getKeys() {
        return keySet().toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see interfaces.IManageCodecs#getValues()
     */
    @Override
    public ICodec[] getValues() {
        return values().toArray(new ICodec[0]);
    }
}
