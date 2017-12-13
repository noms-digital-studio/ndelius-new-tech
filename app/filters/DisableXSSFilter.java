package filters;

import play.mvc.EssentialAction;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import java.util.concurrent.Executor;

public class DisableXSSFilter extends EssentialFilter {
    private final Executor executor;

    @Inject
    public DisableXSSFilter(Executor executor) {
        super();
        this.executor = executor;
    }

    @Override
    public EssentialAction apply(EssentialAction next) {
        return EssentialAction.of(
                request -> next.apply(request).
                map(result -> result.withHeader("X-XSS-Protection", "0"), executor));
    }
}
