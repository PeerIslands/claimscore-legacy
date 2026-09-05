package com.claimscore.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider (PROVIDERS) with its specialty codes.
 *
 * @author rmendez
 * $Id: ProviderDTO.java,v 1.3 2009/08/20 17:31:12 rmendez Exp $
 */
public class ProviderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long providerId;
    private String npi;
    private String providerName;
    private String providerTypeCode;
    private String taxId;
    private String addressLine1;
    private String city;
    private String stateCode;
    private String zip;
    private String phone;
    private String statusCode;
    private List<String> specialtyCodes = new ArrayList<String>();

    public ProviderDTO() {
    }

    public List<String> getSpecialtyCodes() {
        return specialtyCodes;
    }

    public void setSpecialtyCodes(List<String> specialtyCodes) {
        this.specialtyCodes = specialtyCodes;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderTypeCode() {
        return providerTypeCode;
    }

    public void setProviderTypeCode(String providerTypeCode) {
        this.providerTypeCode = providerTypeCode;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
