package basicRestAssured;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class CreateProjectTest {

    @Test
    public void VerifyCreateProject() throws Exception {

        JSONObject body = new JSONObject();
        body.put("Content", "Test-Item");

        // CREATE
        Response response = given()
                .auth()
                .preemptive()
                .basic("nel@gmail.com","12345")
                .body(body.toString())
                .log().all()
        .when()
                .post("https://todo.ly/api/items.json");

        response.then()
                .log().all()
                .statusCode(200) // code verification
                .body("Content", equalTo("Test-Item"));


        int idItem = response.then().extract().path("Id");

        // GET
        response = given()
                .auth()
                .preemptive()
                .basic("nel@gmail.com","12345")
                .body(body.toString())
                .log().all()
        .when()
                .get("https://todo.ly/api/items/"+idItem+".json");

        response.then()
                .log().all()
                .statusCode(200); // code verification


        // UPDATE

        body.put("Content", "Test-ItemUpdated");
        response = given()
                .auth()
                .preemptive()
                .basic("nel@gmail.com","12345")
                .body(body.toString())
                .log().all()
        .when()
                .put("https://todo.ly/api/items/"+idItem+".json");

        response.then()
                .log().all()
                .statusCode(200) // code verification
                .body("Deleted", equalTo(false))
                .body("Content", equalTo("Test-ItemUpdated"));

        // DELETE
        response = given()
                .auth()
                .preemptive()
                .basic("nel@gmail.com","12345")
                .body(body.toString())
                .log().all()
        .when()
                .delete("https://todo.ly/api/items/"+idItem+".json");

        response.then()
                .log().all()
                .statusCode(200) // code verification
                .body("Deleted", equalTo(true))
                .body("Content", equalTo("Test-ItemUpdated"));

        // GET AUTHENTICATION TOKEN
        response = given()
                .auth()
                .preemptive()
                .basic("nel@gmail.com","12345")
                .body(body.toString())
                .log().all()
        .when()
                .get("https://todo.ly/api/authentication/token.json");

        response.then()
                .log().all()
                .statusCode(200); // code verification

    }

}
