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
package org.jtalks.poulpe.model.dao;

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.poulpe.model.entity.ComponentBase;

import java.util.List;
import java.util.Set;

/**
 * Dao for jtalks engine {@link Component}.
 * 
 * @author Pavel Vervenko
 */
public interface ComponentDao extends Crud<Component> {

    /**
     * Get the list of all components.
     * 
     * @return components list
     */
    List<Component> getAll();

    /**
     * Get the set of unoccupied ComponentType.
     * 
     * @return set of ComponentType
     */
    Set<ComponentType> getAvailableTypes();

    /**
     * Gets component by it's type.
     * 
     * @param componentType the component's type
     * @return the component
     */
    Component getByType(ComponentType componentType);

    /**
     * @param componentType for base component
     * @return base component for the given type
     */
    ComponentBase getBaseComponent(ComponentType componentType);
}
