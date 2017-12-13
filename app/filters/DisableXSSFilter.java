package filters;

import akka.util.ByteString;
import javax.inject.Inject;
import play.libs.streams.Accumulator;
import play.mvc.EssentialAction;
import play.mvc.EssentialFilter;
import play.mvc.Result;

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
        return EssentialAction.of(request -> {
            Accumulator<ByteString, Result> accumulator = next.apply(request);
            return accumulator.map(result -> result.withHeader("X-XSS-Protection", "0"), executor);
        });
    }
}
