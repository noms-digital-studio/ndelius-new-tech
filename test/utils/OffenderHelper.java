package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.Offender;
import interfaces.OffenderApi.OffenderAddress;
import lombok.val;
import services.DeliusOffenderApi;

import java.util.ArrayList;

public class OffenderHelper {
    public static Offender anOffender() {
        val offender = new Offender();
        offender.setFirstName("Jimmy");
        offender.setSurname("Fizz");
        offender.setMiddleNames(ImmutableList.of("Jammy", "Fred"));
        return offender;
    }

    public static Offender anOffenderWithEmptyContactDetails() {
        val offender = anOffender();
        offender.setContactDetails(new DeliusOffenderApi.ContactDetails());

        return offender;
    }

    public static Offender anOffenderWithEmptyAddressList() {
        val offender = anOffenderWithEmptyContactDetails();
        offender.getContactDetails().setAddresses(new ArrayList<>());

        return offender;
    }

    public static Offender anOffenderWithMultipleAddresses() {
        val offender = anOffenderWithEmptyAddressList();

        OffenderAddress address1 = new OffenderAddress();
        address1.setCounty("Yorkshire");
        address1.setFrom("2018-01-22");

        OffenderAddress address2 = new OffenderAddress();
        address2.setCounty("Cheshire");
        address2.setFrom("1980-06-03");
        address2.setTo("2018-01-21");

        offender.getContactDetails().getAddresses().add(address1);
        offender.getContactDetails().getAddresses().add(address2);

        return offender;
    }

    public static Offender anOffenderWithAddressListWithNoFromDate() {
        val offender = anOffenderWithEmptyAddressList();

        OffenderAddress address1 = new OffenderAddress();
        address1.setCounty("Yorkshire");

        OffenderAddress address2 = new OffenderAddress();
        address2.setCounty("Cheshire");
        address2.setTo("2018-01-21");

        offender.getContactDetails().getAddresses().add(address1);
        offender.getContactDetails().getAddresses().add(address2);

        return offender;
    }
}
