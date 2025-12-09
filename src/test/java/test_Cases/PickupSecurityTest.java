package test_Cases;

import Utilities.Shared_Methods;
import api_requests.Pickup;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static api_requests.Pickup.*;

public class PickupSecurityTest {

    Shared_Methods method = new Shared_Methods();

    @Test
    public void validPickup() {
        Response res = sendPickup(Pickup.valid());
        method.checkSecurity(res);
        Assert.assertEquals(res.statusCode(), 200);
    }

    @Test
    public void invalidEmail() {
        Response res = sendPickup(Pickup.invalidEmail());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422);
    }

    @Test
    public void sqlInjectionAttempt() {
        Response res = sendPickup(Pickup.sqlInjection());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500);
    }

    @Test
    public void xssInjectionAttempt() {
        Response res = sendPickup(Pickup.xssInjection());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 200);
    }

    @Test
    public void longNameOverflow() {
        Response res = sendPickup(Pickup.longName());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500);
    }

    @Test
    public void noTokenProvided() {
        Response res = sendPickupNoAuth(Pickup.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    @Test
    public void invalidTokenProvided() {
        Response res = sendPickupInvalidToken(Pickup.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    @Test
    public void expiredTokenProvided() {
        Response res = sendPickupExpiredToken(Pickup.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    // RATE LIMIT TEST
    @Test(priority = 9)
    public void rateLimitTest() throws InterruptedException {

        boolean rateLimitHit = false;
        Response lastResponse = null;

        for (int i = 1; i <= 20; i++) {

            lastResponse = sendPickup(Pickup.valid());

            System.out.println("Request #" + i +
                    " → Status: " + lastResponse.getStatusCode());

            if (lastResponse.getStatusCode() == 429) {
                rateLimitHit = true;
                break;
            }

            // to hit server fast
            Thread.sleep(100);
        }

        Assert.assertTrue(rateLimitHit, "Rate limit NOT triggered!");
        Assert.assertEquals(lastResponse.getStatusCode(), 429);
    }
}
