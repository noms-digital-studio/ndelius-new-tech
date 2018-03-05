package injection;

import com.typesafe.config.Config;
import interfaces.DocumentStore;
import play.inject.Injector;
import services.AlfrescoStore;
import services.fakes.InMemoryDocumentStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class DocumentStoreProvider implements Provider<DocumentStore> {

    private final boolean standAloneOperation;
    private Injector injector;

    @Inject
    public DocumentStoreProvider(Config configuration, Injector injector) {
        this.standAloneOperation = configuration.getBoolean("standalone.operation");
        this.injector = injector;
    }

    @Override
    public DocumentStore get() {
        return standAloneOperation ? injector.instanceOf(InMemoryDocumentStore.class) : injector.instanceOf(AlfrescoStore.class);
    }

}
