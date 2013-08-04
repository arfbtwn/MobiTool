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

import little.nj.util.Statics;
import exceptions.InvalidHeaderException;
import format.MobiFile;


public class FileCommands {

    @SuppressWarnings("serial")
    public static class FileNewCommand extends BaseCommand {

        public FileNewCommand(Controller c) {
            super("New", Statics.getImageIcon("images/New24.gif"), c);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.file = new MobiFile(controller.codecs);
            controller.refresh();
        }
    }

    @SuppressWarnings("serial")
    public static class FileOpenCommand extends BaseCommand {

        public FileOpenCommand(Controller c) {
            super("Open...", Statics.getImageIcon("images/Open24.gif"), c);
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (jfc.showOpenDialog(controller.edit) == JFileChooser.APPROVE_OPTION)
                try {
                    controller.file = new MobiFile(jfc.getSelectedFile(),
                            controller.codecs);
                    controller.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidHeaderException e) {
                    e.printStackTrace();
                }
        }
    }

    @SuppressWarnings("serial")
    public static class FileSaveAsCommand extends BaseCommand {

        public FileSaveAsCommand(Controller c) {
            super("Save As...", Statics.getImageIcon("images/SaveAs24.gif"), c);
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
    public static class FileSaveCommand extends BaseCommand {

        public FileSaveCommand(Controller c) {
            super("Save", Statics.getImageIcon("images/Save24.gif"), c);
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
