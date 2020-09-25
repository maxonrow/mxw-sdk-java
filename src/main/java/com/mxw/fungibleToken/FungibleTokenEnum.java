package com.mxw.fungibleToken;

public class FungibleTokenEnum {
    public enum FungibleTokenActions {
        TRANSFER("transfer"),
        MINT("mint"),
        BURN("burn"),
        TRANSFER_OWNERSHIP("transferOwnership"),
        ACCEPT_OWNERSHIP("acceptOwnership");

        private final String value;

        FungibleTokenActions(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum FungibleTokenStateFlags {
        FUNGIBLE(0x0001),
        MINT(0x0002),
        BURN(0x0004),
        FROZEN(0x0008),
        APPROVED(0x0010);

        private final int value;

        FungibleTokenStateFlags(final int value) {
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

    public enum FungibleTokenStatusActions {
        APPROVE("APPROVE"),
        APPROVE_TRANSFER_TOKEN_OWNERSHIP("APPROVE_TRANSFER_TOKEN_OWNERSHIP"),
        REJECT_TRANSFER_TOKEN_OWNERSHIP("REJECT_TRANSFER_TOKEN_OWNERSHIP"),
        REJECT("REJECT"),
        FREEZE("FREEZE"),
        UNFREEZE("UNFREEZE");

        private final String value;

        FungibleTokenStatusActions(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
