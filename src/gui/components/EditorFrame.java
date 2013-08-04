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

import static util.ProgramInfo.PROG_NAME;
import static util.ProgramInfo.PROG_VER;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class EditorFrame extends JFrame {

    public static final Border BORDER  = BorderFactory.createEmptyBorder(10,
                                               10, 10, 10);

    public static final int    MARGINS = 10;

    private JButton            _new;

    private JButton            _open;

    private JButton            _save;

    private JButton            _saveas;

    private HeaderPanel        header;

    private ImagePanel         images;

    private InfoPanel          info;

    private JTabbedPane        tabs;

    private TextPanel          text;

    private JToolBar           tools;

    public EditorFrame() {
        tools = new JToolBar();
        tabs = new JTabbedPane();
        info = new InfoPanel();
        images = new ImagePanel();
        text = new TextPanel();
        header = new HeaderPanel();
        _new = new JButton("New");
        _open = new JButton("Open...");
        _save = new JButton("Save");
        _saveas = new JButton("Save As...");
        setDefaultCloseOperation(3);
        setLayout(new BorderLayout());
        init();
    }

    /**
     * @return
     */
    public HeaderPanel getHeader() {
        return header;
    }

    public ImagePanel getImages() {
        return images;
    }

    public InfoPanel getInfo() {
        return info;
    }

    public TextPanel getText() {
        return text;
    }

    public void init() {
        tools.removeAll();
        tabs.removeAll();
        tools.add(_new);
        tools.add(_open);
        tools.add(_save);
        tools.add(_saveas);
        add(tools, "First");
        add(tabs, "Center");
        tabs.addTab("Info", info);
        tabs.addTab("Text", text);
        tabs.addTab("Images", images);
        tabs.addTab("Header", header);
        pack();
        setTitle(null);
        setVisible(true);
    }

    public void setNewAction(Action a) {
        _new.setAction(a);
    }

    public void setOpenAction(Action a) {
        _open.setAction(a);
    }

    public void setSaveAction(Action a) {
        _save.setAction(a);
    }

    public void setSaveAsAction(Action a) {
        _saveas.setAction(a);
    }

    @Override
    public void setTitle(String title) {
        StringBuilder app = new StringBuilder(String.format("%s %s", PROG_NAME,
                PROG_VER));
        if (title != null)
            app.append(": " + title);
        super.setTitle(app.toString());
    }
}