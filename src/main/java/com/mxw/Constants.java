package com.mxw;
import static com.mxw.crypto.Bip32ECKeyPair.HARDENED_BIT;

public interface Constants {

    String AddressPrefix = "mxw";
    String kycAddressPrefix = "kyc";

    String AddressZero = "mxw000000000000000000000000000000000000000";
    String HashZero = "0x0000000000000000000000000000000000000000000000000000000000000000";

    int[] DefaultHDPath = {44 | HARDENED_BIT, 376 | HARDENED_BIT, 0 | HARDENED_BIT, 0, 0};

    String Power18UnitName = "cin";
    String Power16UnitName = "kcin";
    String Power13UnitName = "Mcin";
    String Power10UnitName = "Gcin";
    String Power7UnitName = "Tcin";
    String Power4UnitName = "Jcin";
    String Power1UnitName = "mxw";

    int UnitDecimals = 18;
    String UnitName = Power1UnitName;
    String SmallestUnitName = Power18UnitName;
}
