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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import little.nj.util.FileUtil;
import little.nj.util.StreamUtil.OutputAction;


public class HtmlActions extends FileActions {
    
    @SuppressWarnings("serial")
    public static class HtmlExportAction extends BaseAction {

        FileUtil futil = new FileUtil();
        
        public HtmlExportAction(Controller c) {
            super("Extract Text to...", null, c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jfc.showSaveDialog(controller.edit) == JFileChooser.APPROVE_OPTION) {
                
                futil.write(jfc.getSelectedFile(), new OutputAction() {

                    @Override
                    public void act(OutputStream stream) throws IOException {
                        ByteArrayInputStream bis = controller.file.getText().getStream();
                        
                        byte[] buf = new byte[bis.available()];
                        bis.read(buf);
                        
                        stream.write(buf);
                    }});
            }
        }
    }

    @SuppressWarnings("serial")
    public static class HtmlImportAction extends BaseAction {

        public HtmlImportAction(Controller c) {
            super("Read Text from...", null, c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Controller c = controller;
            if (jfc.showOpenDialog(c.edit) == JFileChooser.APPROVE_OPTION) {
                c.file.importFromHtml(jfc.getSelectedFile());
                c.refresh();
            }
        }
    }
}
