/**
 * Copyright (C) 2013 nicholas
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

import headers.MobiDocHeader;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import little.nj.adts.ByteField;
import little.nj.gui.components.FieldPanelFactory;


@SuppressWarnings("serial")
public class HeaderPanel extends JPanel {

    private MobiDocHeader model;

    public HeaderPanel() {
        super();
        init();
    }

    private void init() {
        setLayout(new GridLayout(0, 5));
    }

    public void setHeader(MobiDocHeader header) {
        model = header;
        
        removeAll();
        
        for(ByteField i : model.getFields()) {
            JComponent comp = FieldPanelFactory.create(i);
            comp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(comp);
        }
    }
}
