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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.entity.User;

/**
 * Forum branch that contains topics related to branch theme.
 *
 * @author Pavel Vervenko
 */
public class Branch extends Entity {

    private String name;
    private String description;
    private boolean deleted;
    private Section section;
    private List<User> moderators;

    public Branch() {
    }
    
    public Branch(String name) {
        this.name = name;
    }
    
    public Branch(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Get branch name which briefly describes the topics contained in it.
     */
    public String getName() {
        return name;
    }

    /**
     * Set branch name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get branch description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set branch description which contains additional information about the branch.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Check if branch marked is deleted.
     * @return true if is deleted, false otherwise
     */
    public boolean getDeleted() {
        return deleted;
    }

    /**
     * Mark branch as deleted. True means that branch is deleted.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Returns the section in which this branch is.
     * @return the parent section
     */
    public Section getSection() {
        return section;
    }

    /**
     * Sets the section in which this branch is.
     * @param section the parent section
     */
    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * @return get a list of {@link User} which are signed to moderate this branch
     */
    public List<User> getModerators() {
        return moderators;
    }

    /**
     * @param moderators a list of {@link User} which will be signed to moderate this branch
     */
    public void setModerators(List<User> moderators) {
        this.moderators = moderators;
    }
    
    
}
