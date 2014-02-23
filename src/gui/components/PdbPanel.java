package gui.components;

import java.awt.BorderLayout;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import format.PdbFile;
import format.records.PdbRecord;

@SuppressWarnings("serial")
public class PdbPanel extends JPanel {

    private PdbFile file;

    private JScrollPane scroller;
    private JList<PdbRecord> list;
    private JPanel controls;
    private JButton export;

    public PdbPanel() {
        list = new JList<>();
        scroller = new JScrollPane(list);
        controls = new JPanel();
        export = new JButton("Export...");

        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        add(scroller, BorderLayout.CENTER);
        controls.add(export);
        add(controls, BorderLayout.PAGE_END);
    }

    public void setFile(PdbFile file) {
        this.file = file;
        list.setModel(new ListModel());
    }

    public void setExportAction(Action a) {
        export.setAction(a);
    }

    public boolean hasSelection() {
        return list.getSelectedIndices().length > 0;
    }

    private class ListModel extends AbstractListModel<PdbRecord> {

        @Override
        public int getSize() {
            return file.getToc().getCount();
        }

        @Override
        public PdbRecord getElementAt(int index) {
            return file.getToc().records().get(index);
        }
    }
}
