package injection;

import com.typesafe.config.Config;
import interfaces.OffenderApiLogon;
import play.inject.Injector;
import services.DeliusOffenderApi;
import services.FakeOffenderApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class OffenderApiLogonProvider implements Provider<OffenderApiLogon> {

    private final boolean standAloneOperation;
    private Injector injector;

    @Inject
    public OffenderApiLogonProvider(Config configuration, Injector injector) {
        this.standAloneOperation = configuration.getBoolean("standalone.operation");
        this.injector = injector;
    }

    @Override
    public OffenderApiLogon get() {

        if (standAloneOperation) {
            return injector.instanceOf(FakeOffenderApi.class);
        }

        return injector.instanceOf(DeliusOffenderApi.class);
    }
}
