package services;

import interfaces.OffenderApiLogon;

public class DeliusOffenderApi implements OffenderApiLogon {
    @Override
    public String logon(String username) {
        return "charlie";
    }
}
