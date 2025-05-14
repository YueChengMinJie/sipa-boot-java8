package com.sipa.boot.java8.common.oauth2.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.sipa.boot.java8.common.oauth2.enumerate.EVerifyType;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
public class SipaBootUser extends User {
    private String id;

    private String tenantId;

    private Long sequence;

    private EVerifyType verifyType = EVerifyType.NORMAL;

    public SipaBootUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public EVerifyType getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(EVerifyType verifyType) {
        this.verifyType = verifyType;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}
