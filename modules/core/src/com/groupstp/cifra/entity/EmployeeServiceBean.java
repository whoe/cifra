package com.groupstp.cifra.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.app.UserSessions;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(EmployeeService.NAME)
public class EmployeeServiceBean implements EmployeeService {
    @Inject
    private DataManager dataManager;

    public Employee getCurrentUserEmployee() {
        User u = AppBeans.get(UserSessionSource.class).getUserSession().getUser();
        LoadContext<Employee> lc = new LoadContext<>(Employee.class);
        lc.setQueryString("select e from cifra$Employee e where e.user.id=:user").
                setParameter("user", u.getId());
        Employee  e = dataManager.load(lc);
        if(e!=null)
            return e;
        e = new Employee();
        e.setUser(u);
        e.setName(u.getLastName()+" "+u.getFirstName()+" "+u.getMiddleName());
        dataManager.commit(e);
        return e;
    }
}