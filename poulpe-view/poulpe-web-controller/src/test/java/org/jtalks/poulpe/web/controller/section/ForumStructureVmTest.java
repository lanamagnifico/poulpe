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

import org.aspectj.lang.annotation.After;
import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.ComponentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

/**
 * @author stanislav bashkirtsev
 */
public class ForumStructureVmTest {
    @Mock
    private ComponentService componentService;
    @Mock
    private ForumStructureData data;

    private ForumStructureVm vm;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        vm = new ForumStructureVm(componentService);
        vm.setViewData(data);
        vm = spy(vm);
    }

    @AfterMethod
    public void afterMethod(){
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testSave() throws Exception {
        PoulpeSection selectedSection = new PoulpeSection("section", "description");
        doReturn(selectedSection).when(data).getSelectedEntity(PoulpeSection.class);
        doNothing().when(vm).storeNewSection(selectedSection);
        vm.saveSection();
        verify(data).addSelectedSectionToTreeIfNew();
        verify(data).closeDialog();
        verify(vm).storeNewSection(selectedSection);
    }

    @Test
    public void testShowNewSection() throws Exception {
        vm.showNewSectionDialog(true);
        verify(data).showSectionDialog(true);
    }

    @Test
    public void testShowNewBranch() throws Exception {
        vm.showNewBranchDialog(true);
        verify(data).showBranchDialog(true);
    }

    @Test(dataProvider = "provideRandomJcommuneWithSections")
    public void testStoreNewSection(Jcommune jcommune) throws Exception {
        PoulpeSection selectedSection = new PoulpeSection("section", "description");
        doReturn(jcommune).when(data).getRootAsJcommune();

        vm.storeNewSection(selectedSection);
        assertSame(jcommune.getSections().get(jcommune.getSections().size() - 1), selectedSection);
        verify(componentService).saveComponent(jcommune);
    }

    @DataProvider
    public Object[][] provideRandomJcommuneWithSections() {
        Jcommune jcommune = new Jcommune();
        PoulpeSection sectionA = new PoulpeSection("SectionA");
        sectionA.addOrUpdateBranch(new PoulpeBranch("BranchA"));
        sectionA.addOrUpdateBranch(new PoulpeBranch("BranchB"));
        jcommune.addSection(sectionA);
        PoulpeSection sectionB = new PoulpeSection("SectionB");
        sectionB.addOrUpdateBranch(new PoulpeBranch("BranchD"));
        sectionB.addOrUpdateBranch(new PoulpeBranch("BranchE"));
        jcommune.addSection(sectionB);
        return new Object[][]{{jcommune}};
    }
}