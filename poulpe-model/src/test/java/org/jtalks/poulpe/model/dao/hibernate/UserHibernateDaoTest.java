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
package org.jtalks.poulpe.model.dao.hibernate;

import com.google.common.collect.Sets;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.poulpe.model.dao.UserDao;
import org.jtalks.poulpe.model.entity.PoulpeUser;
import org.jtalks.poulpe.pages.Pages;
import org.jtalks.poulpe.test.fixtures.TestFixtures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Vyacheslav Zhivaev
 * @author Alexey Grigorev
 * @author maxim reshetov
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/poulpe/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    static final String NO_FILTER = "";

    // SUT
    @Autowired
    private UserDao dao;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @BeforeMethod
    public void setUp() throws Exception {
        session = sessionFactory.getCurrentSession();
    }

    @Test
    public void testSave() {
        PoulpeUser user = TestFixtures.user();

        saveAndEvict(user);
        PoulpeUser savedUser = (PoulpeUser) session.get(PoulpeUser.class, user.getId());

        assertReflectionEquals(user, savedUser);
    }

    @Test
    public void testSaveIdGeneration() {
        PoulpeUser user = TestFixtures.user();
        long initialId = 0;
        user.setId(initialId);

        saveAndEvict(user);

        assertNotSame(user.getId(), initialId, "ID is not created");
    }

    @Test
    public void testGetByUsername() {
        PoulpeUser user = TestFixtures.user();
        saveAndEvict(user);

        User actual = dao.getByUsername(user.getUsername());
        assertReflectionEquals(actual, user);
    }

    @Test
    public void findPoulpeUsersPaginated_withPagination() {
        String startsWith = "SomeString";
        givenMoreThanOnePage(startsWith);

        int limit = 10;
        List<PoulpeUser> users = dao.findPoulpeUsersPaginated(startsWith, Pages.paginate(1, limit));

        assertEquals(users.size(), limit);
    }

    private void givenMoreThanOnePage(String startsWith) {
        int n = 20;

        while (n > 0) {
            PoulpeUser user = TestFixtures.user(startsWith + n);
            saveAndEvict(user);
            n--;
        }
    }

    @Test
    public void findPoulpeUsersPaginated_noFilterAndNoPagination() {
        List<PoulpeUser> users = TestFixtures.usersListOf(3);
        saveAndEvict(users);
        List<PoulpeUser> actual = dao.findPoulpeUsersPaginated(NO_FILTER, Pages.NONE);

        assertContainsSameElements(actual, users);
    }

    @Test
    public void findPoulpeUsersPaginated_noFilterAndAllOnFirstPage() {
        List<PoulpeUser> users = TestFixtures.usersListOf(3);
        saveAndEvict(users);

        List<PoulpeUser> actual = dao.findPoulpeUsersPaginated(NO_FILTER, Pages.paginate(0, 10));

        assertContainsSameElements(actual, users);
    }

    @Test
    public void findPoulpeUsersPaginated_noFilterAndMoreThanOnePage() {
        List<PoulpeUser> users = TestFixtures.usersListOf(13);
        saveAndEvict(users);

        int limit = 10;
        List<PoulpeUser> actual = dao.findPoulpeUsersPaginated(NO_FILTER, Pages.paginate(0, limit));

        assertEquals(actual.size(), limit);
    }

    @Test
    public void getAllUsersCount() {
        int count = 13;
        List<PoulpeUser> users = TestFixtures.usersListOf(count);
        saveAndEvict(users);

        int actual = dao.countUsernameMatches(NO_FILTER);
        assertEquals(actual, count);
    }

    @Test
    public void testGetAllUsersIncludesInGroups() {
        Group group = TestFixtures.groupWithUsers();
        saveAndEvict(group);
        List<PoulpeUser> actual = dao.getUsersInGroups(Arrays.asList(group));
        assertEquals(actual, group.getUsers());
    }


    @Test
    public void testFindUsersNotInGroups_withoutFilterUsername() {
        Group firstGroup = TestFixtures.groupWithUsers();
        Group secondGroup = TestFixtures.groupWithUsers();
        saveAndEvict(firstGroup);
        saveAndEvict(secondGroup);
        List<PoulpeUser> usersNotInFirstGroup = dao.findUsersNotInGroups(NO_FILTER, Arrays.asList(firstGroup), Pages.paginate(0, 1000));
        assertTrue(secondGroup.getUsers().containsAll(usersNotInFirstGroup));
    }

    @Test
    public void testFindUsersNotInGroups_withFilterUsername() {
        Group firstGroup = TestFixtures.groupWithUsers();
        Group secondGroup = TestFixtures.groupWithUsers();
        saveAndEvict(firstGroup);
        saveAndEvict(secondGroup);
        String filter = secondGroup.getUsers().get(0).getUsername();
        List<PoulpeUser> usersNotInFirstGroup = dao.findUsersNotInGroups(filter, Arrays.asList(firstGroup), Pages.paginate(0, 1000));
        for (PoulpeUser poulpeUser : usersNotInFirstGroup) {
            assertTrue(poulpeUser.getUsername().contains(filter));
        }
        assertTrue(secondGroup.getUsers().containsAll(usersNotInFirstGroup));
    }


    @Test
    public void testFindUsersNotInGroups_withLimit() {
        Group firstGroup = TestFixtures.groupWithUsers();
        Group secondGroup = TestFixtures.groupWithUsers();
        saveAndEvict(firstGroup);
        saveAndEvict(secondGroup);
        int limit = 2;
        List<PoulpeUser> actual = dao.findUsersNotInGroups(NO_FILTER, Arrays.asList(firstGroup), Pages.paginate(0, limit));
        assertEquals(actual.size(), limit);
    }

    private void saveAndEvict(Group group) {
        saveAndEvict((Iterable<PoulpeUser>) (Object) group.getUsers());
        session.save(group);
        session.flush();
        session.clear();
    }

    private void saveAndEvict(PoulpeUser user) {
        dao.saveOrUpdate(user);
        session.evict(user);
    }

    private void saveAndEvict(Iterable<PoulpeUser> users) {
        for (PoulpeUser user : users) {
            saveAndEvict(user);
        }
    }

    // TODO: move away from here
    public static <T> void assertContainsSameElements(Iterable<T> first, Iterable<T> second) {
        Set<T> set1 = Sets.newLinkedHashSet(first);
        Set<T> set2 = Sets.newLinkedHashSet(second);
        assertEquals(set1, set2);
    }
}
