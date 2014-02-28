/**
 * Copyright (C) 2013 
 * Nicholas J. Little <arealityfarbetween@googlemail.com>
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
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.OutputAction;
import format.DefaultCodecManager;
import format.MobiFile;
import format.headers.Enumerations.Compression;
import format.records.PdbRecord;
import gui.components.EditorFrame;
import gui.components.HeaderPanel;
import gui.components.ImagePanel;
import gui.components.InfoPanel;
import gui.components.PdbPanel;
import gui.components.TextPanel;

@SuppressWarnings("deprecation")
public class Controller {

    DefaultCodecManager codecs;

    EditorFrame edit;

    MobiFile file;

    PdbPanel pdb;

    InfoPanel info;

    TextPanel text;

    ImagePanel images;

    HeaderPanel header;

    public Controller() {
        codecs = new DefaultCodecManager();
        file = new MobiFile(codecs);
        createComponents();
        init();
    }

    private void createComponents() {
        edit = new EditorFrame();
        pdb = edit.getPdb();
        info = edit.getInfo();
        images = edit.getImages();
        text = edit.getText();
        header = edit.getHeader();
    }

    public void apply() {
        file.getMobiDocHeader().setExthHeader(true);
        file.setTitle(info.getTitle());
        file.setAuthor(info.getAuthor());
        file.setBlurb(info.getBlurb());
    }

    @SuppressWarnings("serial")
    public void init() {
        pdb.setExportAction(new AbstractAction("Export...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                
                if ((chooser.showSaveDialog(pdb)) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                String dir = chooser.getSelectedFile().getPath();
                
                @SuppressWarnings("unchecked")
                List<PdbRecord> vals = pdb.asJList().getSelectedValuesList();
                
                FileUtil util = new FileUtil();
                
                for(final PdbRecord i : vals) {
                    File file = new File(dir + File.separator + i.getID());
                    util.write(file, new OutputAction() {

                        @Override
                        public void act(OutputStream stream) throws IOException {
                            stream.write(i.getData());
                        }
                        
                    });
                }
            }
        });
        edit.setNewAction(new FileActions.FileNewAction(this));
        edit.setOpenAction(new FileActions.FileOpenAction(this));
        edit.setSaveAction(new FileActions.FileSaveAction(this));
        edit.setSaveAsAction(new FileActions.FileSaveAsAction(this));
        info.setApplyListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Controller.this.apply();
            }
        });
        text.setExtractAction(new HtmlActions.HtmlExportAction(this));
        text.setReadAction(new HtmlActions.HtmlImportAction(this));
        text.setCompressionComboItems(codecs.getKeys());
        text.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();
                    file.getPalmDocHeader().setCompression(
                            Compression.valueOf(item));
                }
            }
        });
        images.setExtractAction(new ImageExportAction(this));
        images.setCoversAction(new AbstractAction("Set Cover & Thumbnail") {

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.AbstractAction#isEnabled()
             */
            @Override
            public boolean isEnabled() {
                return images.asJList().getSelectedIndices().length > 0;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JList list = images.asJList();
                file.setCovers((BufferedImage[]) list.getSelectedValues());
                info.setThumb(file.getThumb());
            }
        });
    }

    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MobiFile file = Controller.this.file;
                
                edit.setTitle(file.getTitle());
                
                pdb.setFile(file);
                
                info.setTitle(file.getTitle());
                info.setAuthor(file.getAuthor());
                info.setBlurb(file.getBlurb());
                info.setThumb(file.getCoverOrThumb());
                
                images.setImages(file.getImages());
                int[] indices = new int[] {
                  file.getMobiDocHeader().getExthHeader().getCover(),
                  file.getMobiDocHeader().getExthHeader().getThumb()
                };
                images.asJList().setSelectedIndices(indices);

                text.getEditorKit().setImageList(file.getImages());
                text.readFromStream(file.getText().getStream());
                text.setSelectedItem(file.getPalmDocHeader().getCompression().toString());
                header.setHeader(file.getMobiDocHeader());
            }
        });
    }
}
