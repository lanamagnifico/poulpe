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
package org.jtalks.poulpe.service.transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.security.acl.AclManager;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.poulpe.model.dao.ComponentDao;
import org.jtalks.poulpe.model.dao.UserDao;
import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.model.entity.ComponentType;
import org.jtalks.poulpe.model.entity.PoulpeUser;
import org.jtalks.poulpe.model.logic.UserBanner;
import org.jtalks.poulpe.pages.Pages;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Class for testing {@code TransactionalUserService} functionality.
 *
 * @author Guram Savinov
 * @author Vyacheslav Zhivaev
 * @author maxim reshetov
 */
public class TransactionalUserServiceTest {
    private static final String username = "username";
    // sut
    private TransactionalUserService userService;

    // dependencies
    private UserDao userDao;

    final String searchString = "searchString";
    private ComponentDao componentDaoMock;
    private AclManager aclManagerMock;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDao = mock(UserDao.class);
        componentDaoMock = mock(ComponentDao.class);
        aclManagerMock = mock(AclManager.class);
        userService = new TransactionalUserService(userDao, mock(UserBanner.class), aclManagerMock, componentDaoMock);
    }

    @Test
    public void getAll() {
        String NO_FILTER = "";
        userService.getAll();
        verify(userDao).findPoulpeUsersPaginated(NO_FILTER, Pages.NONE);
    }

    @Test
    public void countUsernameMatches() {
        userService.countUsernameMatches(searchString);
        verify(userDao).countUsernameMatches(searchString);
    }

    @Test
    public void findUsersPaginated() {
        int page = 1, limit = 10;
        userService.findUsersPaginated(searchString, page, limit);
        verify(userDao).findPoulpeUsersPaginated(searchString, Pages.paginate(page, limit));
    }

    @Test
    public void testGetUsersByUsernameWord() {
        userService.withUsernamesMatching(searchString);
        verify(userDao).findPoulpeUsersPaginated(searchString, Pages.NONE);
    }

    @Test
    public void testUpdateUser() {
        PoulpeUser user = user();
        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    public void TestFindUsersNotInList(){
        List<PoulpeUser> users = new ArrayList<PoulpeUser>();
        users.add(user());
        userService.findUsersNotInList(searchString,users);
        verify(userDao).findUsersNotInList(searchString,users, Pages.NONE);
    }

    @Test
    public void testGetAllBannedUsers() {
        List<PoulpeUser> bannedUsers = userService.getAllBannedUsers();
        assertEquals(bannedUsers, new ArrayList<PoulpeUser>());
    }

    @Test
    public void testGetNonBannedByUsername() {
        List<PoulpeUser> nonBannedUsers = userService.loadNonBannedUsersByUsername(searchString, Pages.paginate(0, 1000));
        assertEquals(nonBannedUsers, new ArrayList<PoulpeUser>());
    }

    @Test
    public void accessNotAllowedBecauseNoComponentTypeRegistered() {
        ComponentType componentType = mock(ComponentType.class);

        when(componentDaoMock.getByType(eq(componentType))).thenReturn(null);

        assertFalse(userService.accessAllowedToComponentType(username, componentType));
    }

    @Test
    public void accessNotAllowedBecauseNoGroupPermissionsAssociatedWithComponent() {
        ComponentType componentType = mock(ComponentType.class);
        Component component = mock(Component.class);

        when(componentDaoMock.getByType(eq(componentType))).thenReturn(component);
        when(aclManagerMock.getGroupPermissionsOn(eq(component))).thenReturn(Collections.<GroupAce>emptyList());

        assertFalse(userService.accessAllowedToComponentType(username, componentType));
    }

    @Test
    public void accessNotAllowedBecausePermissionNotFound() {
        ComponentType componentType = mock(ComponentType.class);
        Component component = mock(Component.class);

        when(userDao.getByUsername(eq(username))).thenReturn(createPoulpeUserWithPredefinedNameAndGroups(Collections.<Group>emptyList()));
        when(componentDaoMock.getByType(eq(componentType))).thenReturn(component);
        when(aclManagerMock.getGroupPermissionsOn(eq(component))).thenReturn(Collections.<GroupAce>emptyList());

        assertFalse(userService.accessAllowedToComponentType(username, componentType));
    }

    @Test
    public void accessNotAllowedBecausePermissionIsNotGranting() {
        long groupId = 42L;

        ComponentType componentType = mock(ComponentType.class);
        Component component = mock(Component.class);
        GroupAce groupAce = mock(GroupAce.class);

        Group group = createGroupWithId(groupId);
        when(groupAce.getGroupId()).thenReturn(groupId);

        when(userDao.getByUsername(eq(username))).thenReturn(createPoulpeUserWithPredefinedNameAndGroups(singletonList(group)));
        when(componentDaoMock.getByType(eq(componentType))).thenReturn(component);
        when(aclManagerMock.getGroupPermissionsOn(eq(component))).thenReturn(singletonList(groupAce));
        when(groupAce.isGranting()).thenReturn(false);

        assertFalse(userService.accessAllowedToComponentType(username, componentType));
    }

    @Test
    public void accessNotAllowedBecauseAtLeastOneOfTheGroupsIsRestrictiong(){
        long groupId = 42L;
        long groupId2 = 666L;

        ComponentType componentType = mock(ComponentType.class);
        Component component = mock(Component.class);
        GroupAce groupAce = mock(GroupAce.class);
        GroupAce groupAce2 = mock(GroupAce.class);

        Group group = createGroupWithId(groupId);
        Group group2 = createGroupWithId(groupId2);

        when(groupAce.getGroupId()).thenReturn(groupId);
        when(groupAce2.getGroupId()).thenReturn(groupId2);

        when(userDao.getByUsername(eq(username))).thenReturn(createPoulpeUserWithPredefinedNameAndGroups(asList(group, group2)));
        when(componentDaoMock.getByType(eq(componentType))).thenReturn(component);
        when(aclManagerMock.getGroupPermissionsOn(eq(component))).thenReturn(asList(groupAce, groupAce2));
        when(groupAce.isGranting()).thenReturn(true);
        when(groupAce2.isGranting()).thenReturn(false);

        assertFalse(userService.accessAllowedToComponentType(username, componentType));
    }

    @Test
    public void accessAllowed() {
        long groupId = 42L;

        ComponentType componentType = mock(ComponentType.class);
        Component component = mock(Component.class);
        GroupAce groupAce = mock(GroupAce.class);
        Group group = createGroupWithId(groupId);

        when(groupAce.getGroupId()).thenReturn(groupId);
        when(userDao.getByUsername(eq(username))).thenReturn(createPoulpeUserWithPredefinedNameAndGroups(singletonList(group)));
        when(componentDaoMock.getByType(eq(componentType))).thenReturn(component);
        when(aclManagerMock.getGroupPermissionsOn(eq(component))).thenReturn(singletonList(groupAce));
        when(groupAce.isGranting()).thenReturn(true);

        assertTrue(userService.accessAllowedToComponentType(username, componentType));
    }

    private Group createGroupWithId(long groupId) {
        Group group = new Group();
        group.setId(groupId);
        return group;
    }

    private PoulpeUser createPoulpeUserWithPredefinedNameAndGroups(List<Group> groups) {
        PoulpeUser user = new PoulpeUser();
        user.setUsername(username);
        user.setGroups(groups);
        return user;
    }


    private static PoulpeUser user() {
        return new PoulpeUser(RandomStringUtils.randomAlphanumeric(10), "username@mail.com", "PASSWORD", "salt");
    }

}
