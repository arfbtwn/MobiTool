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
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import little.nj.util.ImageUtil;

@SuppressWarnings("serial")
public class InfoPanel extends JPanel {

    @SuppressWarnings("unused")
    private static final Dimension COVER_SIZE = new Dimension(200, 400);

    private static final Dimension PLACE_SIZE = new Dimension(100, 150);

    private JButton _apply;

    private JTextField _author;

    private JScrollPane _blb;

    private JTextArea _blurb;

    private ImageIcon _thumb;

    private JTextField _title;

    private JLabel author;

    private JLabel blurb;

    private JPanel controls;

    private JButton thumb;

    private JLabel title;

    public InfoPanel() {
        init();
    }

    public String getAuthor() {
        return _author.getText();
    }

    public String getBlurb() {
        return _blurb.getText();
    }

    public String getTitle() {
        return _title.getText();
    }

    public void init() {
        setLayout(new BorderLayout());
        thumb = new JButton();
        controls = new JPanel();
        GroupLayout group = new GroupLayout(controls);
        controls.setLayout(group);
        title = new JLabel("Title: ");
        _title = new JTextField(20);
        author = new JLabel("Author: ");
        _author = new JTextField(20);
        blurb = new JLabel("Blurb: ");
        _blurb = new JTextArea(10, 20);
        _blurb.setLineWrap(true);
        _blurb.setWrapStyleWord(true);
        _blb = new JScrollPane(_blurb);
        _apply = new JButton("Apply");
        JPanel side = new JPanel();
        side.add(thumb);
        add(side, "Before");
        add(controls, "Center");
        setThumb(null);
        group.setHorizontalGroup(group
                .createSequentialGroup()
                .addGroup(
                        group.createParallelGroup().addComponent(title)
                                .addComponent(author).addComponent(blurb))
                .addGroup(
                        group.createParallelGroup().addComponent(_title)
                                .addComponent(_author).addComponent(_blb)
                                .addComponent(_apply)));
        group.setVerticalGroup(group
                .createSequentialGroup()
                .addGroup(
                        group.createParallelGroup().addComponent(title)
                                .addComponent(_title))
                .addGroup(
                        group.createParallelGroup().addComponent(author)
                                .addComponent(_author))
                .addGroup(
                        group.createParallelGroup().addComponent(blurb)
                                .addComponent(_blb))
                .addGroup(
                        group.createParallelGroup(
                                GroupLayout.Alignment.TRAILING).addComponent(
                                _apply)).addGap(50));
    }

    public void setApplyListener(ActionListener a) {
        _apply.addActionListener(a);
    }

    public void setAuthor(String s) {
        _author.setText(s);
    }

    public void setBlurb(String s) {
        _blurb.setText(s);
    }

    public void setThumb(BufferedImage i) {
        if (i == null) {
            _thumb = null;
            thumb.setText("Add Cover...");
            thumb.setIcon(null);
        } else {
            _thumb = new ImageIcon(ImageUtil.resizeImage(i, PLACE_SIZE));
            thumb.setText("");
            thumb.setIcon(_thumb);
        }
    }

    public void setTitle(String s) {
        _title.setText(s);
    }
}
