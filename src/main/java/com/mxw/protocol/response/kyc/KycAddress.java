package com.mxw.protocol.response.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"country", "dob", "id", "idExpiry", "idType", "seed"})
public class KycAddress {

    private String country;
    private int dob;
    private String id;
    private int idExpiry;
    private String idType;
    private String seed;

    public KycAddress(String country, String idType, String id, int idExpiry, int dob, String seed){
        this.setCountry(country);
        this.setIdType(idType);
        this.setId(id);
        this.setIdExpiry(idExpiry);
        this.setDob(dob);
        this.setSeed(seed);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("idExpiry")
    public int getIdExpiry() {
        return idExpiry;
    }

    public void setIdExpiry(int idExpiry) {
        this.idExpiry = idExpiry;
    }

    @JsonProperty("idType")
    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
