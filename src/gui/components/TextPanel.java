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
package gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;

import editorkit.MobiEditorKit;

@SuppressWarnings("serial")
public class TextPanel extends JPanel implements HyperlinkListener {

    public static final int[] FONT_SIZES = { 8, 10, 14, 18, 22, 30, 38, 46 };

    private JEditorPane       _content;

    private JButton           _extract, _read, _prev, _next, _tsize_a,
            _tsize_b;

    private MobiEditorKit     _kit;

    private JScrollPane       _scroller;

    private JComboBox<Object> compression;

    private int               font_size  = FONT_SIZES.length / 2;

    private JPanel            footer;

    private JPanel            header, controls;

    public TextPanel() {
        header = new JPanel();
        _scroller = new JScrollPane();
        _content = new JEditorPane();
        footer = new JPanel();
        compression = new JComboBox<>();
        controls = new JPanel();
        _extract = new JButton("Extract...");
        _read = new JButton("Read...");
        _next = new JButton(">");
        _prev = new JButton("<");
        _tsize_a = new JButton("a");
        _tsize_b = new JButton("A");
        _kit = new MobiEditorKit();
        init();
    }

    public void addItemListener(ItemListener listener) {
        compression.addItemListener(listener);
    }

    public MobiEditorKit getEditorKit() {
        return _kit;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
    }

    private void init() {
        setLayout(new BorderLayout());
        _next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Rectangle vis = _content.getVisibleRect();
                vis.y += vis.height * 0.9;
                _content.scrollRectToVisible(vis);
            }
        });
        _prev.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Rectangle vis = _content.getVisibleRect();
                vis.y -= vis.height * 0.9;
                _content.scrollRectToVisible(vis);
            }
        });
        _tsize_a.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (font_size > 0) {
                    Font font = _content.getFont();
                    font = new Font(font.getName(), font.getStyle(),
                            FONT_SIZES[--font_size]);
                    _content.setFont(font);
                }
            }
        });
        _tsize_b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (font_size < FONT_SIZES.length - 1) {
                    Font font = _content.getFont();
                    font = new Font(font.getName(), font.getStyle(),
                            FONT_SIZES[++font_size]);
                    _content.setFont(font);
                }
            }
        });
        header.add(compression);
        _scroller.setViewportView(_content);
        _scroller
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        _scroller
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        _content.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        _content.setEditable(false);
        _kit.setParent(_scroller);
        _content.setEditorKit(_kit);
        _content.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                Boolean.TRUE);
        _content.setFont(Font.decode(Font.SERIF + "-" + FONT_SIZES[font_size]));
        footer.add(_extract);
        footer.add(_read);
        controls.setLayout(new GridLayout(4, 1));
        controls.add(_tsize_a);
        controls.add(_tsize_b);
        controls.add(_prev);
        controls.add(_next);
        add(header, BorderLayout.PAGE_START);
        add(_scroller, BorderLayout.CENTER);
        add(controls, BorderLayout.LINE_END);
        add(footer, BorderLayout.PAGE_END);
    }

    public void setCompressionComboItems(Object[] items) {
        compression.setModel(new DefaultComboBoxModel<>(items));
    }

    public void setExtractAction(Action a) {
        _extract.setAction(a);
    }

    public void setReadAction(Action a) {
        _read.setAction(a);
    }

    public void setSelectedItem(Object item) {
        compression.setSelectedItem(item);
    }

    public void setText(String in) {
        HTMLDocument doc = (HTMLDocument) _kit.createDefaultDocument();
        
        try {
            StringReader read = new StringReader(in);
            
            doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
            _kit.read(read, doc, 0);
            
            ElementIterator it = new ElementIterator(doc);
            Element elmo;
            while ((elmo = it.next()) != null) {
                AttributeSet attr = elmo.getAttributes();
                for (Enumeration<?> i = attr.getAttributeNames(); i
                        .hasMoreElements();) {
                    Object x = i.nextElement();
                    Object y = attr.getAttribute(x);
                    if (x.toString().equals("width")
                            && y.toString().matches("[-0]\\d*\\w+")) {
                        attr = doc.getStyleSheet().removeAttribute(attr, x);
                        doc.setParagraphAttributes(elmo.getStartOffset(),
                                elmo.getEndOffset() - elmo.getStartOffset(),
                                attr, true);
                    }
                }
            }
        } catch (IOException | BadLocationException ex) {
            ex.printStackTrace();
        } finally {
            _content.setDocument(doc);
        }
    }
}
