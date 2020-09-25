package com.mxw.nonFungibleToken;

public class NonFungibleTokenEnum {
    public enum NFTokenActions {
        TRANSFER("transfer"), MINT("mint"), BURN("burn"), TRANSFER_OWNERSHIP("transferOwnership"),
        ACCEPT_OWNERSHIP("acceptOwnership"), ENDORSE("endorse"), UPDATE_NFT_ENDORSER_LIST("updateNFTEndorserList");

        private final String value;

        NFTokenActions(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum NFTokenStateFlags {
        NON_FUNGIBLE(0x0001), MINT(0x0002), BURN(0x0004), FROZEN(0x0008), APPROVED(0x0010), transferable(0x0020),
        MODIFIABLE(0x0040), COMMON(0x0080);

        private final int value;

        NFTokenStateFlags(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public enum NonFungibleTokenStatusActions {
        APPROVE("APPROVE"),
        APPROVE_TRANSFER_TOKEN_OWNERSHIP("APPROVE_TRANSFER_TOKEN_OWNERSHIP"),
        REJECT_TRANSFER_TOKEN_OWNERSHIP("REJECT_TRANSFER_TOKEN_OWNERSHIP"),
        REJECT("REJECT"),
        FREEZE("FREEZE"),
        UNFREEZE("UNFREEZE");

        private final String value;

        NonFungibleTokenStatusActions(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
