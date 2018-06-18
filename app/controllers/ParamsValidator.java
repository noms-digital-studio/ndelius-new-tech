package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;
import lombok.val;
import play.Logger;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.unauthorized;

public class ParamsValidator {

    private final Duration userTokenValidDuration;
    private final Function<String, String> decrypter;

    @Inject
    public ParamsValidator(Config configuration) {
        val paramsSecretKey = configuration.getString("params.secret.key");
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);
        userTokenValidDuration = configuration.getDuration("params.user.token.valid.duration");
    }

    Optional<Result> invalidCredentials(String encryptedUsername, String encryptedEpochRequestTimeMills, String username) {

        val epochRequestTime = decrypter.apply(encryptedEpochRequestTimeMills);

        if (username == null || epochRequestTime == null) {
            Logger.error(String.format("Request did not receive user (%s) or t (%s)", encryptedUsername, encryptedEpochRequestTimeMills));
            return Optional.of(badRequest("one or both of 'user' or 't' not supplied"));
        }

        val timeNowInstant = Instant.now();
        val epochRequestInstant = Instant.ofEpochMilli(Long.valueOf(epochRequestTime));

        if (Math.abs(timeNowInstant.toEpochMilli() - epochRequestInstant.toEpochMilli()) > userTokenValidDuration.toMillis()) {
            Logger.warn(String.format(
                "Request not authorised because time currently is %s but token time %s",
                timeNowInstant.toString(),
                epochRequestInstant.toString()));

            return Optional.of(unauthorized());
        }

        return Optional.empty();
    }

}
