package basicRestAssured;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class VerifySchemaTest {

    @Test
    public void VerifyCreateProject() throws Exception {

        JSONObject body = new JSONObject();
        body.put("Content", "Test-Item");

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(ValidationConfiguration.newBuilder()
                        .setDefaultVersion(SchemaVersion.DRAFTV4).freeze()
                ).freeze();

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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("createVerificationSchema.json").using(schemaFactory))
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
                .statusCode(200) // code verification
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getVerificationSchema.json").using(schemaFactory));


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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("updateVerificationSchema.json").using(schemaFactory))
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
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("deleteVerificationSchema.json").using(schemaFactory))
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
                .statusCode(200) // code verification
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getTokenVerificationSchema.json").using(schemaFactory));


        // para hacer el esquema ver la clase 24 de noviembre 1:06:00
        // para hacer la integracion en Jenkins ver la claase 6 de diciembre
    }

}
