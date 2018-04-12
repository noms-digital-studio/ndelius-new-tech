package helpers;

import org.junit.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtHelperTest {

    public static final String FAKE_USER_BEARKER_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbj1mYWtlLnVzZXIsY249VXNlcnMsZGM9bW9qLGRjPWNvbSIsInVpZCI6ImZha2UudXNlciIsImV4cCI6MTUxNzYzMTkzOX0=.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ";;

    @Test
    public void principalIsExtractedFromJwtSubject() {

        assertThat(JwtHelper.principal(
                generateToken("{\"sub\":\"cn=fake.user,cn=Users,dc=moj,dc=com\",\"uid\":\"fake.user\",\"exp\":1517631939}")))
                .isEqualTo("cn=fake.user,cn=Users,dc=moj,dc=com");
    }

    @Test
    public void jwtTokensWithNonStringElementsAreParsed() {

        assertThat(JwtHelper.principal(
                generateToken("{\"sub\":\"cn=fake.user,cn=Users,dc=moj,dc=com\",\"uid\":\"fake.user\",\"exp\":1517631939, \"probationAreaCodes\": [\"A00\"]}")))
                .isEqualTo("cn=fake.user,cn=Users,dc=moj,dc=com");
    }

    private String generateToken(String body) {
        return String.format("eyJhbGciOiJIUzUxMiJ9.%s.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ", Base64.getEncoder().encodeToString(body.getBytes()));
    }
}
