package test_Cases;

import Configuration.Config;
import Utilities.Shared_Methods;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static api_requests.ForgetPassword.*;

public class ForgetPasswordSecurityTest {

    Shared_Methods method = new Shared_Methods();


    @Test
    public void validEmail() {
        Response res = sendForgetPassWithAuth(Config.VALID_EMAIL);
        method.checkSecurity(res);
        Assert.assertEquals(res.statusCode(), 200);
    }



    @Test
    public void invalidEmailFormat() {
        Response res = sendForgetPassWithAuth("not-an-email");
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422,
                "API must not accept invalid email");
    }

    @Test
    public void sqlInjectionAttempt() {
        Response res = sendForgetPassWithAuth("\" OR \"1\"=\"1");
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500,
                "API must not crash with SQL injection payload");
    }

    @Test
    public void xssInjectionAttempt() {
        Response res = sendForgetPassWithAuth("<script>alert(1)</script>");
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 200,
                "API must not accept XSS payload as valid email");
    }

    @Test
    public void veryLongEmailInput() {
        String longEmail = "a".repeat(5000) + "@test.com";
        Response res = sendForgetPassWithAuth(longEmail);
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500,
                "API must not crash on long input");
    }

    @Test
    public void noTokenProvided() {
        Response res = sendForgetPassNoAuth("test@test.com");
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403,
                "API should NOT allow requests without token");
    }

    @Test
    public void invalidTokenProvided() {
        Response res = sendForgetPassInvalidToken("test@test.com");
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403,
                "API should reject invalid token");
    }

    @Test(priority = 8)
    public void checkRateLimit (){
        boolean rateLimitHit = false;
        Response lastResponse = null;

        // Hit the API multiple times very fast WITH AUTH
        for (int i = 1; i <= 15; i++) {

            lastResponse = sendForgetPassWithAuth(Config.VALID_EMAIL);

            System.out.println("Request # " + i +
                    " → Status: " + lastResponse.getStatusCode());

            if (lastResponse.getStatusCode() == 429) {
                rateLimitHit = true;
                break;
            }
        }

        Assert.assertTrue(rateLimitHit,
                "Rate limit was NOT triggered after multiple rapid requests!");

        Assert.assertEquals(lastResponse.getStatusCode(), 429,
                "Expected 429 Too Many Requests.");

        Assert.assertTrue(lastResponse.asString().toLowerCase().contains("rate"),
                "Response should contain indication of rate limit.");
    }
}
