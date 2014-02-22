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
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import little.nj.gui.components.ImageCellRenderer;
import little.nj.gui.components.ListPanel;

@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class ImagePanel extends JPanel {
	
    JPanel        header = new JPanel();
    
    DefaultListModel model = new DefaultListModel();

    ListPanel 	  images = new ListPanel(model);
    
    JList		  list	 = images.asJList();
    
    JButton       _extract = new JButton();

    JButton       _set_covers = new JButton();

    public ImagePanel() {
        init();
    }

    public void init() {
    	images.addComponentListener(componentListener);
    	list.setCellRenderer(new ImageCellRenderer());
    	list.setVisibleRowCount(-1);
    	
        setLayout(new BorderLayout());
        
        header.add(_extract);
        header.add(_set_covers);
        
        add(header, BorderLayout.PAGE_START);
        add(images, BorderLayout.CENTER);
    }
    
    public JList asJList() { return list; }

    public void setCoversAction(Action a) {
        _set_covers.setAction(a);
    }

    public void setExtractAction(Action a) {
        _extract.setAction(a);
    }

    public void setImages(List<BufferedImage> list) {
    	model.clear();
    	for(BufferedImage i : list) {
    		model.addElement(i);
    	}
    }
    
    /**
	 * Listens for component display and resize events
	 */
	private ComponentListener componentListener = new ComponentAdapter() {
		@Override
		public void componentShown(ComponentEvent e) {
			componentResized(e);
		}
		@Override
		public void componentResized(ComponentEvent e) {
			Component component = e.getComponent();
			
			System.out.printf("componentListener.componentResized: Height = %d, Width = %d%n", component.getHeight(), component.getWidth());
			
			if (component.getHeight() >= component.getWidth()) {
				list.setLayoutOrientation(JList.VERTICAL);
			} else {
				list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			}
		}
	};
}
