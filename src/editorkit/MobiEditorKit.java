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
import javax.swing.text.Position.Bias;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

@SuppressWarnings("serial")
public class MobiEditorKit extends HTMLEditorKit {

    class MobiFactory extends HTMLFactory {

        class ImgView extends ImageView {

            BufferedImage image;

            boolean       parse_tried = false;

            public ImgView(Element elem) {
                super(elem);
            }

            @Override
            public Image getImage() {
                AttributeSet set = getElement().getAttributes();
                int idx = 0;
                if (!parse_tried)
                    try {
                        parse_tried = true;
                        idx = Integer.parseInt(
                                (String) set.getAttribute("recindex"), 10);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                if (idx == 0 || idx > images.size())
                    return null;
                image = images.get(idx - 1);
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

            @Override
            public float getPreferredSpan(int axis) {
                if (axis == Y_AXIS)
                    return 1;
                return getContainer().getWidth();
            }

            @Override
            public void paint(Graphics g, Shape allocation) {
                Rectangle a = allocation.getBounds();
                System.out.println(a.toString());
                g.drawLine(a.x, a.y, a.x + a.width, a.y);
            }

            @Override
            public int viewToModel(float x, float y, Shape a, Bias[] bias) {
                return super.viewToModel(x, y, a, bias);
            }
        }

        List<BufferedImage> images;

        JComponent          parent;

        @Override
        public View create(Element elem) {
            if (elem.getName().equalsIgnoreCase("mbp"))
                return new PageBreakView(elem);
            else if (elem.getName().equalsIgnoreCase("img"))
                return new ImgView(elem);
            return super.create(elem);
        }
    }

    private MobiFactory factory;

    public MobiEditorKit() {
        super();
        factory = new MobiFactory();
    }

    @Override
    public HTMLDocument createDefaultDocument() {
        return (HTMLDocument) super.createDefaultDocument();
    }

    @Override
    public String getContentType() {
        return "text/mobi-html";
    }

    @Override
    protected Parser getParser() {
        // TODO Auto-generated method stub
        return super.getParser();
    }

    @Override
    public ViewFactory getViewFactory() {
        return factory;
    }

    public void setImageList(List<BufferedImage> list) {
        factory.images = list;
    }

    public void setParent(JComponent parent) {
        factory.parent = parent;
    }
}
