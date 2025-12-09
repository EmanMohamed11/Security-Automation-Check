package api_requests;

import Configuration.Config;
import Utilities.Shared_Methods;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BankInfo {

    public static String valid() {
        return """
            {
              "bankInfo": {
                "beneficiaryName": "Test User",
                "bankName": "NBG",
                "ibanNumber": "EG123456789012345678901256789",
                "accountNumber": "123456789012"
              },
              "paymentInfoOtp": "111"
            }
            """;
    }

    public static String sqlInjection() {
        return """
            {
              "bankInfo": {
                "beneficiaryName": "\\" OR \\"1\\"=\\"1",
                "bankName": "NBG",
                "ibanNumber": "EG123456789012345678901256789",
                "accountNumber": "123456789012"
              },
              "paymentInfoOtp": "111"
            }
            """;
    }

    public static String xss() {
        return """
            {
              "bankInfo": {
                "beneficiaryName": "<script>alert(1)</script>",
                "bankName": "NBG",
                "ibanNumber": "EG123456789012345678901256789",
                "accountNumber": "123456789012"
              },
              "paymentInfoOtp": "111"
            }
            """;
    }

    public static String longInput() {
        String longName = "A".repeat(5000);
        return """
            {
              "bankInfo": {
                "beneficiaryName": "%s",
                "bankName": "NBG",
                "ibanNumber": "\\"EG\\"123456789012345678901256789",
                "accountNumber": "123456789012"
              },
              "paymentInfoOtp": "111"
            }
            """.formatted(longName);
    }

    public static String missingFields() {
        return """
            {
              "bankInfo": {},
              "paymentInfoOtp": ""
            }
            """;
    }


    // Helper methods for sending the API request


    public static Response sendAddBankInfo(Object body) {
        return RestAssured
                .given().log().all()
                .header("Authorization", Shared_Methods.getToken())
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .header("x-device-id", "01K0ZH74759AR8ER1NZSS478R6")
                .header("X-DEVICE-FINGERPRINT", "1iwjpzb")
                .body(body)
                .post(Config.BASE_URL+Config.BANK_INFO_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendAddBankInfoNoAuth(Object body) {
        return RestAssured
                .given().log().all()
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .body(body)
                .post(Config.BASE_URL+Config.BANK_INFO_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendAddBankInfoInvalidToken(Object body) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "INVALID_TOKEN_123")
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .body(body)
                .post(Config.BASE_URL+Config.BANK_INFO_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendAddBankInfoExpiredToken(Object body) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "EXPIRED_TOKEN_ABC999")
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .body(body)
                .post(Config.BASE_URL+Config.BANK_INFO_ENDPOINT)
                .then().log().all().extract().response();
    }
}

