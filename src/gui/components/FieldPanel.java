package gui.components;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import little.nj.adts.ByteField;
import little.nj.adts.ByteFieldSet;
import little.nj.gui.components.FieldPanelFactory;

@SuppressWarnings("serial")
public class FieldPanel extends JPanel {
	
	ByteFieldSet model;
	
    public FieldPanel() {
        super();
        init();
    }

    private void init() {
    }

    public void setFields(ByteFieldSet fields) {
        model = fields;
        
        removeAll();
        
        for(ByteField i : model) {
            JComponent comp = FieldPanelFactory.create(i);
            comp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(comp);
        }
    }

}
