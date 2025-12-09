package api_requests;

import Configuration.Config;
import Utilities.Shared_Methods;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ForgetPassword {

    // Helper method to send request
    public static Response sendForgetPassWithAuth(String email) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization" , Shared_Methods.getToken())
                .body("{\"email\":\"" + email + "\"}")
                .post(Config.BASE_URL+Config.FORGET_PASSWORD_ENDPOINT)
                .then().log().all().extract().response();

    }


    public static Response sendForgetPassNoAuth(String email) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .body("{\"email\":\"" + email + "\"}")
                .post(Config.BASE_URL+Config.FORGET_PASSWORD_ENDPOINT)
                .then().log().all().extract().response();
    }

    public static Response sendForgetPassInvalidToken(String email) {
        return RestAssured
                .given().log().all()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "INVALID_TOKEN_123")
                .body("{\"email\":\"" + email + "\"}")
                .post("/users/forget-password")
                .then().log().all().extract().response();
    }




}






