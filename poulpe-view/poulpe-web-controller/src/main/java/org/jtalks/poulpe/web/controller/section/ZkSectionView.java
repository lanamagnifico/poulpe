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
package org.jtalks.poulpe.web.controller.section;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.model.entity.Section;
import org.jtalks.poulpe.validation.ValidationResult;
import org.jtalks.poulpe.web.controller.DialogManager;
import org.jtalks.poulpe.web.controller.ZkInitializer;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * 
 * @author unascribed
 * @author Alexey Grigorev
 */
public class ZkSectionView extends Window implements AfterCompose, SectionView {

    private static final long serialVersionUID = -2745900622179593542L;

    private ZkInitializer zkInitializer = new ZkInitializer(this);

    private transient SectionPresenter presenter;

    private Window newSectionDialog;
    private Textbox newSectionDialog$sectionName;
    private Textbox newSectionDialog$sectionDescription;

    private Window editSectionDialog;
    private Textbox editSectionDialog$sectionName;
    private Textbox editSectionDialog$sectionDescription;

    // TODO: make this validator conform the common interface

    public void validationFailure(ValidationResult result, boolean isNewSection) {
        // keeping the signature for testing
    }

    /*
     * private ValidationFailureHandler handler = new
     * ValidationFailureHandler();
     * 
     * public void validationFailure(ValidationResult result, boolean
     * isNewSection) { handler.validationFailure(result, "name",
     * getTextbox(isNewSection)); }
     * 
     * private Textbox getTextbox(boolean isNewSection) { if (isNewSection) {
     * return newSectionDialog$sectionName; } else { return
     * editSectionDialog$sectionName; } }
     */

    @Override
    public void afterCompose() {
        zkInitializer.init();

        presenter.initView(this);
    }

    @Override
    public void showSection(Section section) {
        // TODO move SectionTreeComponent creation to external factory method
        getChildren().add(new ZkSectionTreeComponent(section, presenter));
    }

    @Override
    public void showSections(List<Section> sections) {
        // TODO: Find out what's happening here.
        List<Object> childrenToSave = new ArrayList<Object>();
        for (Object obj : getChildren()) {
            if (!(obj instanceof ZkSectionTreeComponent)) {
                childrenToSave.add(obj);
            }
        }
        getChildren().clear();
        for (Object obj : childrenToSave) {
            getChildren().add((Component) obj);
        }
        for (Section section : sections) {
            // TODO move SectionTreeComponent creation to external factory
            // method
            getChildren().add(new ZkSectionTreeComponent(section, presenter));
        }
    }

    @Override
    public void removeSection(Section section) {
    }

    @Override
    public void openNewSectionDialog() {
        setNewSectionName("");
        setNewSectionDescription("");
        newSectionDialog.setVisible(true);
    }

    public void newSectionDialog() {
        presenter.addNewSection(getNewSectionName(), getNewSectionDescription());
    }

    public void editSectionDialog() {
        presenter.editSection(getEditSectionName(), getEditSectionDescription());
    }

    @Override
    public void closeNewSectionDialog() {
        newSectionDialog.setVisible(false);
        setNewSectionDescription("");
        setNewSectionName("");
    }

    @Override
    public void closeEditSectionDialog() {
        editSectionDialog.setVisible(false);
        editSectionDialog$sectionName.setText("");
        editSectionDialog$sectionDescription.setText("");
    }

    public void closeDeleteSectionDialog() {
    }

    @Override
    public void openEditSectionDialog(String name, String description) {
        setEditSectionName(name);
        setEditSectionDescription(description);
        editSectionDialog.setVisible(true);
    }

    private String getNewSectionName() {
        return newSectionDialog$sectionName.getText();
    }

    private String getNewSectionDescription() {
        return newSectionDialog$sectionDescription.getText();
    }

    private String getEditSectionName() {
        return editSectionDialog$sectionName.getText();
    }

    private String getEditSectionDescription() {
        return editSectionDialog$sectionDescription.getText();
    }

    private void setEditSectionName(String name) {
        editSectionDialog$sectionName.setText(name);
    }

    private void setEditSectionDescription(String description) {
        editSectionDialog$sectionDescription.setText(description);
    }

    private void setNewSectionName(String name) {
        newSectionDialog$sectionName.setText(name);
    }

    private void setNewSectionDescription(String description) {
        newSectionDialog$sectionDescription.setText(description);
    }

    public void openErrorPopupInNewSectionDialog(String label) {
        String message = Labels.getLabel(label);
        newSectionDialog$sectionName.setErrorMessage(message);
    }

    public void openErrorPopupInEditSectionDialog(String label) {
        String message = Labels.getLabel(label);
        editSectionDialog$sectionName.setErrorMessage(message);
    }

    @Override
    public void closeDialogs() {
        closeNewSectionDialog();
        closeEditSectionDialog();
        closeDeleteSectionDialog();
    }

    public void closeNewBranchDialog() {
        // TODO : implement
    }

    public void forwardToBranchPermissionManagement(Branch branch) throws IOException {

    }

    public void openDeleteBranchDialog(Branch branch) {

    }

    public void closeEditBranchDialog() {

    }

    public boolean isDeleteDialogOpen() {
        return false;
        // return deleteSectionDialog.isVisible();
    }

    public boolean isEditDialogOpen() {
        return editSectionDialog.isVisible();
    }

    public boolean isNewDialogOpen() {
        return newSectionDialog.isVisible();
    }

    /**
     * Handle event when child dialog was hiding
     */
    public void onHideDialog() {
        presenter.updateView();
    }

    // UI EVENT HANDLERS

    /**
     * Event which happen when user click on '+' buttton opens dialog for adding
     * new section
     */
    public void onClick$addSectionButton() {
        openNewSectionDialog();
    }

    /**
     * Event which happen when user click on add button in new branch dialog
     * window this cause save new branch
     */
    public void onClick$addButton$newSectionDialog() {
        newSectionDialog();
    }

    /**
     * Event which happen when user click on cancel button in new branch dialog
     * window this cause close new branch dialog
     */
    public void onClick$closeButton$newSectionDialog() {
        closeNewSectionDialog();
    }

    /**
     * Event occurs when the used click on cancel button in the
     * DeleteSectionDialog
     */
    public void onClick$closeButton$deleteSectionDialog() {
        closeDeleteSectionDialog();
    }

    /**
     * Event occurs when the used click on delete button in the
     * DeleteSectionDialog
     */
    public void onClick$deleteButton$deleteSectionDialog() {
    }

    /**
     * Event which happen when user click on add button in new branch dialog
     * window this cause save new branch
     */
    public void onClick$editButton$editSectionDialog() {
        editSectionDialog();
    }

    /**
     * Event which happen when user click on cancel button in new branch dialog
     * window this cause close new branch dialog
     */
    public void onClick$closeButton$editSectionDialog() {
        closeEditSectionDialog();
    }

    // CUSTOM EVENT HANDLER

    /**
     * @deprecated use {@link DialogManager} and
     * {@link PerfomableFactory#deleteSection(Section)} for this
     */
    @Deprecated
    public void openDeleteSectionDialog(Section victim) {
        Events.postEvent(new Event("onOpenDeleteSectionDialog", getDesktop().getPage("sectionDeleteDialog").getFellow(
                "deleteWindow"), victim));
    }

    @Override
    @Deprecated
    public void openNewBranchDialog() {
        Events.postEvent(new Event("onOpenAddDialog", getDesktop().getPage("BranchDialog").getFellow("editWindow")));
    }

    @Override
    @Deprecated
    public void openEditBranchDialog(Branch branch) {
        Events.postEvent(new Event("onOpenEditDialog", getDesktop().getPage("BranchDialog").getFellow("editWindow"),
                branch));
    }

    @Override
    @Deprecated
    public void openModerationDialog(Branch branch) {
        Events.postEvent(new Event("onOpen", getDesktop().getPage("moderatorDialog").getFellow("moderatorWindow"),
                branch));
    }

    @Deprecated
    public void closeModeratorDialog() {
        Events.postEvent(new Event("onCloseModeratorDialog", getDesktop().getPage("moderatorDialog").getFellow(
                "moderatorWindow")));
    }

    // SETTERS

    /**
     * @param presenter instance presenter for view
     */
    public void setPresenter(SectionPresenter presenter) {
        this.presenter = presenter;
    }

}