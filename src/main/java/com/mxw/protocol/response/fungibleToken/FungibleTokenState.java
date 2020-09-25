package com.mxw.protocol.response.fungibleToken;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FungibleTokenState {
    @JsonProperty("Flags")
    private int Flags;

    @JsonProperty("Name")
    private String Name;

    @JsonProperty("Symbol")
    private String Symbol;

    @JsonProperty("Decimals")
    private int Decimals;

    @JsonProperty("TotalSupply")
    private String TotalSupply;

    @JsonProperty("MaxSupply")
    private String MaxSupply;

    @JsonProperty("Owner")
    private String Owner;

    @JsonProperty("NewOwner")
    private String NewOwner;

    @JsonProperty("Metadata")
    private String Metadata;

    public int getFlags() {
        return Flags;
    }

    public void setFlags(int Flags) {
        this.Flags = Flags;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        this.Symbol = symbol;
    }

    public int getDecimals() {
        return Decimals;
    }

    public void setDecimals(int Decimals) {
        this.Decimals = Decimals;
    }

    public String getTotalSupply() {
        return TotalSupply;
    }

    public void setTotalSupply(String TotalSupply) {
        this.TotalSupply = TotalSupply;
    }

    public String getMaxSupply() {
        return MaxSupply;
    }

    public void setMaxSupply(String MaxSupply) {
        this.MaxSupply = MaxSupply;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String Owner) {
        this.Owner = Owner;
    }

    public String getNewOwner() {
        return NewOwner;
    }

    public void setNewOwner(String NewOwner) {
        this.NewOwner = NewOwner;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String Metadata) {
        this.Metadata = Metadata;
    }


}
