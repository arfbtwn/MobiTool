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

import javax.swing.AbstractAction;
import javax.swing.Icon;

@SuppressWarnings("serial")
public abstract class BaseCommand extends AbstractAction {

    private Controller controller;

    protected BaseCommand(String name, Icon icon, Controller c) {
        super(name, icon);
        controller = c;
    }

    protected Controller getController() {
        return controller;
    }
}
