package Utilities;

import Configuration.Config;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;


public class Shared_Methods {


    public static  Response post(String endpoint){
        return RestAssured
                .given().log().all()
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .post(endpoint).then().log().all().extract().response();
    }


    public static String getToken(){
        Response response = post(Config.BASE_URL+Config.AUTH_ENDPOINT);
        return response.jsonPath().getString("token");
    }






    public void checkSecurity(Response res) {

        String body = res.getBody().asString();

        // Sensitive data exposure
        if (body.toLowerCase().contains("password")
                || body.toLowerCase().contains("stack")
                || body.toLowerCase().contains("exception"))
        {
            System.out.println("⚠ SECURITY WARNING: Sensitive info leaked in response!");
        }

        // HTML means possible XSS
        if (body.contains("<") && body.contains(">")) {
            System.out.println("⚠ SECURITY WARNING: HTML content detected (Possible XSS)");
        }

        // Too much information (server type / version)
        String server = res.getHeader("Server");
        if (server != null && !server.isEmpty()) {
            System.out.println("⚠ SECURITY WARNING: Server header exposed: " + server);
        }

        // Status code suspicious
        if (res.statusCode() >= 500) {
            System.out.println("⚠ SECURITY WARNING: Server crashed (5xx). Potential vulnerability!");
        }

        if (res.statusCode() == 429) {
            System.out.println("ℹ Rate limit active (good)");
        }
    }

    public static String futureUniqueDate() {

        int year = ThreadLocalRandom.current().nextInt(2027, 2031);


        int month = ThreadLocalRandom.current().nextInt(1, 13);


        int day = ThreadLocalRandom.current().nextInt(1, LocalDate.of(year, month, 1).lengthOfMonth() + 1);

        LocalDate future = LocalDate.of(year, month, day);
        return future.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    }

