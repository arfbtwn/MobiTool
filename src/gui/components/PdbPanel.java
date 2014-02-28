package gui.components;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import little.nj.gui.components.ListPanel;
import format.PdbFile;
import format.records.PdbRecord;

@SuppressWarnings("serial")
public class PdbPanel extends ListPanel {

    private JButton export;

    public PdbPanel() {
        export = new JButton("Export...");
        
        init();
    }

    private void init() {
        asJList().getSelectionModel().addListSelectionListener(listener);
        asJPanel().removeAll();
        asJPanel().add(export);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setFile(PdbFile file) {
        DefaultListModel model = (DefaultListModel)asJList().getModel();
        
        model.clear();
        for(PdbRecord i : file.getToc()) {
            model.addElement(i);
        }
    }

    public void setExportAction(Action a) {
        export.setAction(a);
    }

    private ListSelectionListener listener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
                return;
            
            export.getAction()
                  .setEnabled(asJList().getSelectedIndices().length > 0);
        }
        
    };
}
