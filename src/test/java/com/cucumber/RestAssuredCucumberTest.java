package com.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestAssuredCucumberTest {
    JSONObject body = new JSONObject();
    JsonSchemaFactory schemaFactory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(ValidationConfiguration.newBuilder()
                    .setDefaultVersion(SchemaVersion.DRAFTV4).freeze()
            ).freeze();

    static int idItem;

    @When("Creating an item with the name {string}")
    public void createItem(String nameItem) {
        body.put("Content", nameItem);
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
                .body("Content", equalTo(nameItem));

        idItem = response.then().extract().path("Id");
    }

    @When("Getting the item by ID")
    public void gettingTheItemWithTheName() {
        Response response = given()
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

    }

    @When("Updating the item with the name {string}")
    public void updatingTheItemWithTheName(String nameUpdated) {

        body.put("Content", nameUpdated);
        Response response = given()
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
                .body("Content", equalTo(nameUpdated));
    }

    @When("Deleting the item {string}")
    public void deletingTheItemByID(String name) {
        Response response = given()
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
                .body("Content", equalTo(name));
    }

    @When("Getting the authentication token for the item")
    public void gettingTheAuthenticationTokenForTheItem() {
        Response response = given()
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
    }
}
