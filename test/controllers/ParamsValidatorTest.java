package controllers;

import com.typesafe.config.Config;
import helpers.Encryption;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.mvc.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParamsValidatorTest {

    private ParamsValidator paramsValidator;

    @Mock
    private Config configuration;

    @Before
    public void setup() {
        when(configuration.getString("params.secret.key")).thenReturn("ThisIsASecretKey");
        when(configuration.getDuration("params.user.token.valid.duration")).thenReturn(Duration.ofHours(1));
        paramsValidator = new ParamsValidator(configuration);
    }

    @Test
    public void returns400WhenUsernameIsNull() {
        val encryptedUser = Encryption.encrypt("", "ThisIsASecretKey");
        val encryptedTime = encryptTimePlusMinutesDrift(0);

        Optional<Result> result = paramsValidator.invalidCredentials(
            encryptedUser,
            encryptedTime,
            null);

        assertThat(result.get().status()).isEqualTo(400);
    }

    @Test
    public void returns401WhenTimeDriftIsGreaterThanExpected() {
        val encryptedUser = Encryption.encrypt("roger.bobby", "ThisIsASecretKey");
        val encryptedTime = encryptTimePlusMinutesDrift(61);

        Optional<Result> result = paramsValidator.invalidCredentials(
            encryptedUser,
            encryptedTime,
            "roger.bobby");

        assertThat(result.get().status()).isEqualTo(401);
    }

    @Test
    public void returns401WhenTimeDriftIsSmallerThanExpected() {
        val encryptedUser = Encryption.encrypt("roger.bobby", "ThisIsASecretKey");
        val encryptedTime = encryptTimePlusMinutesDrift(-61);

        Optional<Result> result = paramsValidator.invalidCredentials(
            encryptedUser,
            encryptedTime,
            "roger.bobby");

        assertThat(result.get().status()).isEqualTo(401);
    }

    @Test
    public void returnsAnEmptyOptionalWhenCredsAreValid() {
        val encryptedUser = Encryption.encrypt("roger.bobby", "ThisIsASecretKey");
        val encryptedTime = encryptTimePlusMinutesDrift(0);

        Optional<Result> result = paramsValidator.invalidCredentials(
            encryptedUser,
            encryptedTime,
            "roger.bobby");

        assertThat(result.isPresent()).isFalse();
    }

    private String encryptTimePlusMinutesDrift(int driftInMinutes) {
        return Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli() + (1000 * 60 * driftInMinutes)), "ThisIsASecretKey");
    }
}