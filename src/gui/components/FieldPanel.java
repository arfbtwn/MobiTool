/**
 * Copyright (C) 2014 
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
package gui.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import little.nj.adts.ByteField;
import little.nj.adts.ByteFieldSet;
import little.nj.gui.components.EditableTableModel;
import little.nj.gui.components.FieldPanelEditor;

@SuppressWarnings({ "serial" })
public class FieldPanel extends JPanel {
    
    private EditableTableModel<ByteField> model = new EditableTableModel<>();
    private JTable table = new JTable(model);
    private JScrollPane scroll = new JScrollPane(table);

    public FieldPanel() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        table.setTableHeader(null);
        table.setDefaultRenderer(ByteField.class, new FieldPanelEditor());
        table.setDefaultEditor(ByteField.class, new FieldPanelEditor());
        add(scroll, BorderLayout.CENTER);
    }

    public void setFields(ByteFieldSet fields) {
        model.clear();
        
        for (ByteField i : fields) {
            model.add(i);
        }
        
        int rend = table.getRowCount(),
            cend = table.getColumnCount();
        
        for (int i = 0; i < rend; ++i) {
            
            int row_height = 0;
            
            for(int j = 0; j < cend; ++j) {
                Component comp = table.prepareRenderer(table.getCellRenderer(i, j), i, j);
                
                int height = (int)comp.getPreferredSize().getHeight();
                
                if (height > row_height) {
                    row_height = height;
                }
            }
            
            table.setRowHeight(row_height);
        }
        
        scroll.validate();
    }
}
