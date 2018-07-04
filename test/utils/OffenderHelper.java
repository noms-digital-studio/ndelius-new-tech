package utils;

import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi.ContactDetails;
import interfaces.OffenderApi.Offender;
import interfaces.OffenderApi.OffenderAddress;

import java.util.ArrayList;

public class OffenderHelper {
    public static Offender anOffender() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            null
        );
    }

    public static Offender anOffenderWithEmptyContactDetails() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            new ContactDetails(null));
    }

    public static Offender anOffenderWithEmptyAddressList() {
        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            new ContactDetails(new ArrayList<>()));
    }

    public static Offender anOffenderWithMultipleAddresses() {

        OffenderAddress address1 = new OffenderAddress(
            "Big Building",
            "7",
            "High Street",
            "Nether Edge",
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            "2000-06-12",
            null
        );

        OffenderAddress address2 = new OffenderAddress(
            "Big Building",
            "7",
            "High Street",
            "Nether Edge",
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            "1980-01-01",
            "2000-06-12"
        );

        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            new ContactDetails(ImmutableList.of(address1, address2)));
    }

    public static Offender anOffenderWithAddressListWithNoFromDate() {
        OffenderAddress address1 = new OffenderAddress(
            "Big Building",
            "7",
            "High Street",
            "Nether Edge",
            "Sheffield",
            "Yorkshire",
            "S10 1LE",
            null,
            null
        );

        return new Offender(
            "Jimmy",
            "Fizz",
            ImmutableList.of("Jammy", "Fred"),
            null,
            null,
            new ContactDetails(ImmutableList.of(address1)));
    }
}
