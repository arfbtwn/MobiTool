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

import javax.swing.JTabbedPane;

import format.headers.MobiDocHeader;

@SuppressWarnings("serial")
public class HeaderPanel extends JTabbedPane {
    
    private FieldPanel palm = new FieldPanel();
    private FieldPanel mobi = new FieldPanel();
    private FieldPanel exth = new FieldPanel();
    
    public HeaderPanel() { super(); init(); }
    
    private void init() {
        setTabPlacement(JTabbedPane.BOTTOM);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        add("Palm Doc Header", palm);
        add("Mobi Doc Header", mobi);
        add("EXTH Header", exth);
    }

    public void setHeader(MobiDocHeader header) {
        mobi.setFields(header.getFields());
    }
}
