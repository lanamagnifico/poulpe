/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.model.entity;

import java.util.List;

import org.jtalks.common.model.entity.Property;

/**
 * Poulpe entity which represents Administrator Panel as itself.
 * 
 * @author Vyacheslav Zhivaev
 */
public class Poulpe extends Component {

    /**
     * Creates Component with {@link ComponentType#ADMIN_PANEL}.
     * Visible for hibernate
     */
    protected Poulpe() {
        setComponentType(ComponentType.ADMIN_PANEL);
    }
    
    /**
     * Should be instantiated via {@link ComponentType}
     * 
     * @param name
     * @param description
     * @param properties
     */
    Poulpe(String name, String description, List<Property> properties) {
        super(name, description, ComponentType.FORUM, properties);
    }
    

}
