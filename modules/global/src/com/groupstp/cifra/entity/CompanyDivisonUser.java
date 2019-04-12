package com.groupstp.cifra.entity;

import javax.persistence.Entity;

import com.haulmont.cuba.core.entity.annotation.Extends;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.cuba.security.entity.User;
import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("cifra$CompanyDivisionUser")
@Entity(name = "cifra$CompanyDivisonUser")
@Extends(User.class)
public class CompanyDivisonUser extends User {
    private static final long serialVersionUID = -5099292807986458452L;

    @JoinTable(name = "CIFRA_COMPANY_DIVISON_USER_COMPANY_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_DIVISON_USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "COMPANY_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    protected List<Company> companies;

    @JoinTable(name = "CIFRA_COMPANY_DIVISON_USER_DIVISION_LINK",
        joinColumns = @JoinColumn(name = "COMPANY_DIVISON_USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "DIVISION_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    protected List<Division> divisions;

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    public List<Division> getDivisions() {
        return divisions;
    }


}