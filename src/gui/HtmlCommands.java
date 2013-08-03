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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;


public class HtmlCommands extends FileCommands {

    @SuppressWarnings("serial")
    public static class HtmlExportCommand extends BaseCommand {

        public HtmlExportCommand(Controller c) {
            super("Extract Text to...", null, c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getController().file != null)
                if (jfc.showSaveDialog(getController().edit) == JFileChooser.APPROVE_OPTION) {
                    
                    try (FileOutputStream fos = new FileOutputStream(jfc.getSelectedFile())) {
                        byte[][] records = getController().file.getText().getRecords();
                        
                        for(byte[] i : records)
                            fos.write(i);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }                 
        }
    }

    @SuppressWarnings("serial")
    public static class HtmlImportCommand extends BaseCommand {

        public HtmlImportCommand(Controller c) {
            super("Read Text from...", null, c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Controller c = getController();
            if (c.file != null)
                if (jfc.showOpenDialog(c.edit) == JFileChooser.APPROVE_OPTION)
                    try {
                        c.file.importFromHtml(jfc.getSelectedFile());
                        c.refresh();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
        }
    }
}
