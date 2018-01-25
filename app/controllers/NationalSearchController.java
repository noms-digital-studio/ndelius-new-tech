package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.OffenderApiLogon;
import interfaces.OffenderSearch;
import lombok.val;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NationalSearchController extends Controller {

    private final views.html.nationalSearch template;
    private final OffenderSearch offenderSearch;
    private final Duration userTokenValidDuration;
    private final String paramsSecretKey;
    private final OffenderApiLogon offenderApiLogon;


    @Inject
    public NationalSearchController(
            Config configuration,
            views.html.nationalSearch template,
            OffenderSearch offenderSearch,
            OffenderApiLogon offenderApiLogon) {
        this.template = template;
        this.offenderSearch = offenderSearch;
        this.offenderApiLogon = offenderApiLogon;
        paramsSecretKey = configuration.getString("params.secret.key");
        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");
    }

    public CompletionStage<Result> index(String encryptedUsername, String encryptedEpochRequestTimeMills) {
        val username = Encryption.decrypt(encryptedUsername, paramsSecretKey);
        val epochRequestTime = Encryption.decrypt(encryptedEpochRequestTimeMills, paramsSecretKey);

        if (username == null || epochRequestTime == null) {
            Logger.error(String.format("Request did not receive user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
            return CompletableFuture.supplyAsync(() -> badRequest("no 'user' or 't' supplied"));
        }

        if (Duration.between(toLocalDateTime(epochRequestTime), LocalDateTime.now()).compareTo(userTokenValidDuration) > 0) {
            Logger.warn(String.format(
                    "Request not authorised because time currently is %s but token time %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    toLocalDateTime(epochRequestTime).format(DateTimeFormatter.ISO_DATE_TIME)));
            return CompletableFuture.supplyAsync(Results::unauthorized);
        }


        return offenderApiLogon.logon(username).thenApply((bearerToken) -> {
            session("offenderApiBearerToken", bearerToken);
            return ok(template.render());
        }).exceptionally((e) -> {
            Logger.error("Unable to logon to offender API", e);
            return internalServerError();
        });
    }

    public CompletionStage<Result> searchOffender(String searchTerm, int pageSize, int pageNumber) {
        return offenderSearch.search(searchTerm, pageSize, pageNumber).thenApply(JsonHelper::okJson);
    }

    private LocalDateTime toLocalDateTime(String epochRequestTime) {
        return Instant.ofEpochMilli(Long.valueOf(epochRequestTime)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
