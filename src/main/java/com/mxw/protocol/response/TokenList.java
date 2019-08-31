package com.mxw.protocol.response;

public class TokenList {

    public TokenList(){

    }

    private String[] fungible;

    private String[] nonFungible;

    public String[] getFungible() {
        return fungible;
    }

    public void setFungible(String[] fungible) {
        this.fungible = fungible;
    }

    public String[] getNonFungible() {
        return nonFungible;
    }

    public void setNonFungible(String[] nonFungible) {
        this.nonFungible = nonFungible;
    }
}
