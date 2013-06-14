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
package util;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class MediaLoader extends HashMap<String, BufferedImage> {

    public static BufferedImage loadFromUrl(URL baseUrl, String url) {
        BufferedImage rv = null;
        try {
            URL _url = new URL(baseUrl, url);
            rv = ImageIO.read(_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rv;
    }

    private URL baseUrl;

    public MediaLoader(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * @return the baseUrl
     */
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * Loads the saved resources
     */
    public void load() {
        for (Map.Entry<String, BufferedImage> i : entrySet())
            i.setValue(loadFromUrl(baseUrl, i.getKey()));
    }

    /**
     * @param baseUrl
     *            the baseUrl to set
     */
    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }
}
