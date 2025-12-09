package api_requests;

import Configuration.Config;
import Utilities.Shared_Methods;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static Utilities.Shared_Methods.futureUniqueDate;

public class Pickup {



    public static String valid() {
        return """
                {
                  "businessLocationId": "MFqXsoFhxO",
                  "contactPerson": {
                    "_id": "_sCFBrHGi",
                    "name": "test name",
                    "email": "amira.mosa+991^@bosta.co",
                    "phone": "+201055592829"
                  },
                  "scheduledDate": "%s",
                  "numberOfParcels": "3",
                  "hasBigItems": false,
                  "repeatedData": {
                    "repeatedType": "One Time"
                  },
                  "creationSrc": "Web"
                }
                """.formatted(futureUniqueDate());
    }


    public static String invalidEmail() {
        return valid().replace("amira.mosa+991^@bosta.co", "not-an-email");
    }

    public static String sqlInjection() {
        return valid().replace("test name", "\" OR \"1\"=\"1");
    }

    public static String xssInjection() {
        return valid().replace("test name", "<script>alert(1)</script>");
    }

    public static String longName() {
        return valid().replace("test name", "a".repeat(5000));
    }

    public static Response sendPickup(Object body) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json, text/plain, */*")
                .header("content-type", "application/json")
                .header("Authorization", Shared_Methods.getToken())
                .header("x-device-id", "01JV70TKSFGV9Z1QWEYV3N5APC")
                .header("x-device-fingerprint", "1hgtilh")
                .body(body)
                .post(Config.BASE_URL + Config.PICKUP_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendPickupInvalidToken(Object body) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json, text/plain, */*")
                .header("content-type", "application/json")
                .header("Authorization", "INVALID_TOKEN_123")
                .body(body)
                .post(Config.BASE_URL + Config.PICKUP_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendPickupNoAuth(Object body) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json, text/plain, */*")
                .header("content-type", "application/json")
                .header("x-device-id", "01JV70TKSFGV9Z1QWEYV3N5APC")
                .header("x-device-fingerprint", "1hgtilh")
                .body(body)
                .post(Config.BASE_URL + "/pickups")
                .then().log().all().extract().response();
    }

    public static Response sendPickupExpiredToken(Object body) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json, text/plain, */*")
                .header("content-type", "application/json")
                .header("Authorization", "EXPIRED_TOKEN_ABC123")
                .body(body)
                .post(Config.BASE_URL + Config.PICKUP_ENDPOINT)
                .then().log().all().extract().response();
    }
}





