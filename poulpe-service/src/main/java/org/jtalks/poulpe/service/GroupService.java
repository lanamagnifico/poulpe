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
package org.jtalks.poulpe.service;

import java.util.List;

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.service.EntityService;

/**
 * Service for dealing with {@link Group} objects
 * 
 * @author unascribed
 */
public interface GroupService extends EntityService<Group> {

    /**
     * @return list of all {@link Group} objects
     */
    List<Group> getAll();

    /**
     * @param name to look up
     * @return list of groups that names match the given name
     */
    List<Group> getAllMatchedByName(String name);

    /**
     * Delete group
     * 
     * @param group to be delete
     * @exception IllegalArgumentException if group is null
     */
    void deleteGroup(Group group);

    /**
     * Save or update group.
     * 
     * @param selectedGroup instance to save
     * @exception IllegalArgumentException if group is null
     */
    void saveGroup(Group selectedGroup);

}