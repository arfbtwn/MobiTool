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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ImageExportAction extends BaseAction {

    public ImageExportAction(Controller c) {
        super("Extract Images to...", null, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.file != null) {
            List<BufferedImage> list = Arrays.asList(controller.images
                    .getImageView().getSelectedItems());
            int j = 1;
            File file = null;
            for (BufferedImage i : list) {
                file = new File(
                        String.format("%s%s%sImage%d.jpg", 
                                System.getProperty("user.dir"),
                                File.separator,
                                j++));
                try {
                    ImageIO.write(i, "jpg", file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (file != null)
                JOptionPane.showMessageDialog(controller.edit,
                        "Images Extracted to: " + file.getParentFile()
                                + "\\Image*.jpg");
        }
    }
}
