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
package editorkit;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

@SuppressWarnings("serial")
public class MobiEditorKit extends HTMLEditorKit {

    private List<BufferedImage> images;

    private JComponent parent;

    private MobiFactory factory = new MobiFactory();

    @Override
    public HTMLDocument createDefaultDocument() {
        return new MobiDocument();
        //return (HTMLDocument) super.createDefaultDocument();
    }

    @Override
    public String getContentType() {
        return "text/mobi-html";
    }

    @Override
    public ViewFactory getViewFactory() {
        return factory;
    }

    public void setImageList(List<BufferedImage> list) {
        images = list;
    }

    public void setParent(JComponent parent) {
        this.parent = parent;
    }

    class MobiFactory extends HTMLFactory {

        class ImgView extends ImageView {

            BufferedImage image;

            int idx = 0;
            boolean parse_tried = false;

            public ImgView(Element elem) {
                super(elem);
            }

            @Override
            public Image getImage() {
                if (parse_tried)
                    return image;
                
                parse_tried = true;
                try {
                    AttributeSet set = getElement().getAttributes();
                    Object att = set.getAttribute("recindex");
                    idx = Integer.parseInt((String) att, 10);
                    image = images.get(idx - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    image = null;
                }
                
                return image;
            }

            @Override
            public URL getImageURL() {
                return null;
            }

            @Override
            public float getPreferredSpan(int axis) {
                int rv = 50;
                if (image != null)
                    switch (axis) {
                    case View.Y_AXIS:
                        int height = parent.getHeight();
                        if (image.getHeight() < height)
                            height = image.getHeight();
                        rv = height;
                        break;
                    case View.X_AXIS:
                        int width = parent.getWidth();
                        if (image.getWidth() < width)
                            width = image.getWidth();
                        rv = width;
                        break;
                    }
                return rv;
            }

            @Override
            public void paint(Graphics g, Shape a) {
                if (image != null) {
                    Rectangle rect = a.getBounds();
                    g.drawImage(image, rect.x, rect.y, rect.width, rect.height,
                            null);
                } else
                    super.paint(g, a);
            }
        }

        class PageBreakView extends ComponentView {

            public PageBreakView(Element elem) {
                super(elem);
            }

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.text.ComponentView#getPreferredSpan(int)
             */
            @Override
            public float getPreferredSpan(int axis) {
                switch(axis) {
                case X_AXIS:
                    return parent.getWidth();
                default:
                case Y_AXIS:
                    return parent.getHeight();
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.text.ComponentView#getMinimumSpan(int)
             */
            @Override
            public float getMinimumSpan(int axis) {
                switch(axis) {
                case X_AXIS:
                    return parent.getWidth();
                default:
                case Y_AXIS:
                    return 0;
                }
            }

            @Override
            public void paint(Graphics g, Shape allocation) {
                Rectangle a = allocation.getBounds();
                System.out.println(a.toString());
                g.drawRect(a.x, a.y, a.width, a.height);
            }
        }

        @Override
        public View create(Element elem) {            
            if (elem.getName().equalsIgnoreCase("mbp"))
                return new PageBreakView(elem);
            else if (elem.getName().equalsIgnoreCase("img"))
                return new ImgView(elem);
            return super.create(elem);
        }
    }
}
