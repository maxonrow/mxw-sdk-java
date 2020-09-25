package com.mxw.protocol.response.nonFungibleToken;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NFTokenState {
    @JsonProperty("Flags")
    private int Flags;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Symbol")
    private String Symbol;
    @JsonProperty("Owner")
    private String Owner;
    @JsonProperty("NewOwner")
    private String NewOwner;
    @JsonProperty("Properties")
    private String Properties;
    @JsonProperty("Metadata")
    private String Metadata;
    @JsonProperty("TotalSupply")
    private String TotalSupply;
    @JsonProperty("TransferLimit")
    private String TransferLimit;
    @JsonProperty("MintLimit")
    private String MintLimit;
    @JsonProperty("EndorserList")
    private String[] EndorserList;

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

    public String getMintLimit() {
        return MintLimit;
    }

    public void setMintLimit(String MintLimit) {
        this.MintLimit = MintLimit;
    }

    public String getTransferLimit() {
        return TransferLimit;
    }

    public void setTransferLimit(String TransferLimit) {
        this.TransferLimit = TransferLimit;
    }

//    public String[] getEndorserList() {
//        return EndorserList;
//    }
//
//    public void setEndorserList(String[] EndorserList) {
//        this.EndorserList = EndorserList;
//    }

    public String getTotalSupply() {
        return TotalSupply;
    }

    public void setTotalSupply(String TotalSupply) {
        this.TotalSupply = TotalSupply;
    }

    public String getProperties() {
        return Properties;
    }

    public void setProperties(String Properties) {
        this.Properties = Properties;
    }

}
