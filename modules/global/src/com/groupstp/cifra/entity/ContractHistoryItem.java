package com.groupstp.cifra.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.security.entity.User;

import java.util.Date;

@NamePattern("%s переместил с этапа %s на этап %s %s|performer,from,to,when")
@MetaClass(name = "cifra$ContractHistoryItem")
public class ContractHistoryItem extends BaseUuidEntity {
    private static final long serialVersionUID = 3895362103840983628L;

    @MetaProperty
    protected String from;

    @MetaProperty
    protected String to;

    @MetaProperty
    protected User performer;

    @MetaProperty
    protected Date when;

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public User getPerformer() {
        return performer;
    }

    public void setPerformer(User performer) {
        this.performer = performer;
    }


}