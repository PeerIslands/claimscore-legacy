package com.claimscore.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Policy holder (member) with addresses and the active plan.
 *
 * @author jkowalczyk
 * $Id: PolicyHolderDTO.java,v 1.8 2012/04/17 14:05:33 jkowalczyk Exp $
 */
public class PolicyHolderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long policyHolderId;
    private String memberNumber;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private Date birthDate;
    private String genderCode;
    private String ssnLast4;
    private String statusCode;
    private Long activePlanId;
    private String activePlanCode;
    private List<AddressDTO> addresses = new ArrayList<AddressDTO>();

    public PolicyHolderDTO() {
    }

    public String getFullName() {
        StringBuffer sb = new StringBuffer();
        if (lastName != null) {
            sb.append(lastName);
        }
        sb.append(", ");
        if (firstName != null) {
            sb.append(firstName);
        }
        if (middleInitial != null && middleInitial.trim().length() > 0) {
            sb.append(" ").append(middleInitial.trim()).append(".");
        }
        return sb.toString();
    }

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    public Long getPolicyHolderId() {
        return policyHolderId;
    }

    public void setPolicyHolderId(Long policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getSsnLast4() {
        return ssnLast4;
    }

    public void setSsnLast4(String ssnLast4) {
        this.ssnLast4 = ssnLast4;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getActivePlanId() {
        return activePlanId;
    }

    public void setActivePlanId(Long activePlanId) {
        this.activePlanId = activePlanId;
    }

    public String getActivePlanCode() {
        return activePlanCode;
    }

    public void setActivePlanCode(String activePlanCode) {
        this.activePlanCode = activePlanCode;
    }
}
