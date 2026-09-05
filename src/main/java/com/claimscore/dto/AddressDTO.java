package com.claimscore.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Policy holder address (PH_ADDRESSES).
 *
 * @author jkowalczyk
 * $Id: AddressDTO.java,v 1.2 2008/11/05 08:13:40 jkowalczyk Exp $
 */
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long addressId;
    private String addressTypeCode;
    private String line1;
    private String line2;
    private String city;
    private String stateCode;
    private String zip;
    private Date effectiveDate;
    private Date endDate;

    public AddressDTO() {
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddressTypeCode() {
        return addressTypeCode;
    }

    public void setAddressTypeCode(String addressTypeCode) {
        this.addressTypeCode = addressTypeCode;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
