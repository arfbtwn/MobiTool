package gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import little.nj.adts.ByteField;
import little.nj.adts.ByteFieldSet;
import little.nj.gui.components.ByteFieldPanel;
import little.nj.gui.components.FieldPanelFactory;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class FieldPanel extends JPanel {

    private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    private JScrollPane scroll = new JScrollPane(list);

    public FieldPanel() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        list.setCellRenderer(render);
        add(scroll, BorderLayout.CENTER);
    }

    public void setFields(ByteFieldSet fields) {
        model.clear();

        for (ByteField i : fields) {
            model.addElement(i);
        }
    }

    private ListCellRenderer render = new ListCellRenderer() {
        
        private Map<ByteField, ByteFieldPanel> panels = new TreeMap<>();

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            ByteField field = (ByteField)value;
            
            ByteFieldPanel panel = panels.get(field);
            
            if (null == panel) {
                panel = FieldPanelFactory.create(field);
            }
            
            if (isSelected) {
                panel.setForeground(list.getSelectionForeground());
                panel.setBackground(list.getSelectionBackground());
            } else {
                panel.setForeground(list.getForeground());
                panel.setBackground(list.getBackground());
            }
            
            if (cellHasFocus) {
                panel.setBorder(BorderFactory.createDashedBorder(null, 2f, 5f, 1f, true));
            } else {
                panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
            
            return panel;
        }
    };
}
