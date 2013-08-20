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
import java.io.IOException;

import javax.swing.JFileChooser;

import little.nj.util.ImageUtil;
import exceptions.InvalidHeaderException;
import format.MobiFile;


public class FileActions {

    @SuppressWarnings("serial")
    public static class FileNewAction extends BaseAction {

        public FileNewAction(Controller c) {
            super("New", ImageUtil.getImageIcon("images/New24.gif"), c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.file = new MobiFile(controller.codecs);
            controller.refresh();
        }
    }

    @SuppressWarnings("serial")
    public static class FileOpenAction extends BaseAction {

        public FileOpenAction(Controller c) {
            super("Open...", ImageUtil.getImageIcon("images/Open24.gif"), c);
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (jfc.showOpenDialog(controller.edit) == JFileChooser.APPROVE_OPTION) {
//                    Thread t = new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
                            
                            try {
                                controller.file = new MobiFile(jfc.getSelectedFile(), controller.codecs);
                                controller.refresh();
                            } catch (InvalidHeaderException | IOException ex) {
                                ex.printStackTrace();
                            }
                            
//                        } });
//                    t.start();
                }
        }
    }

    @SuppressWarnings("serial")
    public static class FileSaveAsAction extends BaseAction {

        public FileSaveAsAction(Controller c) {
            super("Save As...", ImageUtil.getImageIcon("images/SaveAs24.gif"), c);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (controller.file != null)
                if (jfc.showSaveDialog(controller.edit) == JFileChooser.APPROVE_OPTION) {
                    controller.apply();
                    controller.file.writeToFile(jfc.getSelectedFile());
                }
        }
    }

    @SuppressWarnings("serial")
    public static class FileSaveAction extends BaseAction {

        public FileSaveAction(Controller c) {
            super("Save", ImageUtil.getImageIcon("images/Save24.gif"), c);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.AbstractAction#isEnabled()
         */
        @Override
        public boolean isEnabled() {
            return controller.file.canSave();
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (controller.file != null) {
                controller.apply();
                controller.file.writeToFile();
            }
        }
    }

    static protected JFileChooser jfc = new JFileChooser(
                                              System.getProperty("user.dir"));
}
