package test_Cases;

import Utilities.Shared_Methods;
import api_requests.BankInfo;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static api_requests.BankInfo.*;

public class BankInfoSecurityTest {


    Shared_Methods method = new Shared_Methods();



    @Test(priority = 1)
    public void validBankInfo() {
        Response res = sendAddBankInfo(BankInfo.valid());
        method.checkSecurity(res);
        Assert.assertEquals(res.statusCode(), 200);
    }

    @Test
    public void sqlInjectionTest() {
        Response res = sendAddBankInfo(BankInfo.sqlInjection());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500);
    }

    @Test
    public void xssTest() {
        Response res = sendAddBankInfo(BankInfo.xss());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 200);
    }

    @Test
    public void longInputTest() {
        Response res = sendAddBankInfo(BankInfo.longInput());
        method.checkSecurity(res);
        Assert.assertNotEquals(res.statusCode(), 500);
    }

    @Test
    public void missingFieldsTest() {
        Response res = sendAddBankInfo(BankInfo.missingFields());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 400 || res.statusCode() == 422);
    }

    @Test
    public void noTokenProvidedTest() {
        Response res = sendAddBankInfoNoAuth(BankInfo.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    @Test
    public void invalidTokenProvidedTest() {
        Response res = sendAddBankInfoInvalidToken(BankInfo.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    @Test
    public void expiredTokenProvidedTest() {
        Response res = sendAddBankInfoExpiredToken(BankInfo.valid());
        method.checkSecurity(res);
        Assert.assertTrue(res.statusCode() == 401 || res.statusCode() == 403);
    }

    @Test(priority = 9)
    public void checkRateLimit (){
        boolean rateLimitHit = false;
        Response lastResponse = null;

        // Hit the API multiple times very fast WITH AUTH
        for (int i = 1; i <= 15; i++) {

            lastResponse = sendAddBankInfo(BankInfo.valid());

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
