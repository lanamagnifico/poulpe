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
package org.jtalks.poulpe.web.controller.component;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.Validate;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.exceptions.ElementDoesNotExist;
import org.jtalks.poulpe.web.controller.DialogManager;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.zkutils.BindUtilsWrapper;
import org.zkoss.bind.annotation.Command;

import java.io.IOException;
import java.util.List;

/**
 * Adding, removing, editing and configuring of components.
 *
 * @author Alexey Grigorev
 */
public class ComponentsVm {
    public static final String SELECTED = "selected", CAN_CREATE_NEW_COMPONENT = "ableToCreateNewComponent",
            COMPONENTS = "components";
    public static final String COMPONENTS_PAGE_LOCATION = "/WEB-INF/pages/component/components.zul";

    private final ComponentService componentService;
    private final DialogManager dialogManager;
    private final WindowManager windowManager;
    private final SelectedEntity<Component> selectedEntity;

    private BindUtilsWrapper bindWrapper = new BindUtilsWrapper();

    private Component selected;

    /**
     * @param componentService service for loading and saving component
     * @param dialogManager    shows confirmation dialog for deletion
     * @param windowManager    object for opening and closing application windows
     * @param selectedEntity   desktop-scoped bean to which selected entities passed, used for editing components
     */
    public ComponentsVm(ComponentService componentService, DialogManager dialogManager, WindowManager windowManager,
                        SelectedEntity<Component> selectedEntity) {
        this.componentService = componentService;
        this.dialogManager = dialogManager;
        this.windowManager = windowManager;
        this.selectedEntity = selectedEntity;
    }

    /**
     * @return list of all component
     */
    public List<Component> getComponents() {
        return componentService.getAll();
    }

    /**
     * Deletes selected component. Selected component is set using {@link #setSelected(Component)}.
     *
     * @throws IllegalStateException if no component selected
     */
    @Command
    public void deleteComponent() {
        Validate.validState(selected != null, "entity to delete must be selected");

        DialogManager.Performable dc = new DialogManager.Performable() {
            @Override
            public void execute() {
                try {
                    componentService.deleteComponent(selected);
                    selected = null;
                    // Because confirmation needed, we have to send notification event programmatically
                    bindWrapper.postNotifyChange(ComponentsVm.this, SELECTED, COMPONENTS, CAN_CREATE_NEW_COMPONENT);

                } catch (ElementDoesNotExist elementDoesNotExist) {
                    //TODO add error message
                } catch (IOException e) {
                    //TODO add error message
                }

            }
        };

        dialogManager.confirmDeletion(selected.getName(), dc);
    }

    /**
     * Shows a window for adding component
     */
    @Command
    public void addNewComponent() {
        AddComponentVm.openWindowForAdding(windowManager);
    }

    /**
     * Shows a component edit window for currently selected element. Selected component is set using {@link
     * #setSelected(Component)}.
     */
    @Command
    public void configureComponent() {
        selectedEntity.setEntity(selected);
        EditComponentVm.openWindowForEdit(windowManager);
    }

    /**
     * @return {@code true} only if new component can be created, {@code false} otherwise.
     */
    public boolean isAbleToCreateNewComponent() {
        return !componentService.getAvailableTypes().isEmpty();
    }

    /**
     * @param selected currently selected component
     */
    public void setSelected(Component selected) {
        this.selected = selected;
    }

    @VisibleForTesting
    void setBindWrapper(BindUtilsWrapper bindWrapper) {
        this.bindWrapper = bindWrapper;
    }

    public static void show(WindowManager windowManager) {
        windowManager.open(COMPONENTS_PAGE_LOCATION);
    }

}