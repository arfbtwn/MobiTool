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
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import exceptions.InvalidHeaderException;
import format.CodecManager;
import format.MobiFile;
import format.headers.Enumerations.Compression;
import gui.components.EditorFrame;
import gui.components.HeaderPanel;
import gui.components.ImagePanel;
import gui.components.InfoPanel;
import gui.components.PdbPanel;
import gui.components.TextPanel;

public class Controller {

    CodecManager codecs;

    EditorFrame  edit;

    MobiFile     file;
    
    PdbPanel	 pdb;
    
    InfoPanel    info;

    TextPanel    text;

    ImagePanel   images;
    
    HeaderPanel  header;

    public Controller() {
        codecs = new CodecManager();
        file = new MobiFile(codecs);
        createComponents();
        init();
    }

    public Controller(File in) throws IOException, InvalidHeaderException {
        codecs = new CodecManager();
        file = new MobiFile(in, codecs);
        createComponents();
        init();
        refresh();
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
        file.getMobiDocHeader().getExthHeader().setAuthor(info.getAuthor());
        file.getMobiDocHeader().getExthHeader().setBlurb(info.getBlurb());
    }

    @SuppressWarnings("serial")
    public void init() {
    	pdb.setExportAction(new AbstractAction("Export...") {

    		@Override
    		public boolean isEnabled() {
    			return pdb.hasSelection();
    		}
    		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			} });
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

            /* (non-Javadoc)
             * @see javax.swing.AbstractAction#isEnabled()
             */
            @Override
            public boolean isEnabled() {
                return images.getImageView().getSelectedItems().length > 0;
            }
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                file.setCovers(images.getImageView().getSelectedItems());
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
                images.getImageView()
                        .setSelectedItems(
                                new BufferedImage[] { file.getCover(),
                                        file.getThumb() });
                
//                text.getEditorKit().setImageList(file.getImages());
//                text.setText(file.getText().getText());
                text.readFromStream(file.getText().getStream());
//                text.setSelectedItem(file.getPalmDocHeader().getCompression()
//                        .toString());
                header.setHeader(file.getMobiDocHeader());
            }
        });
    }
}
