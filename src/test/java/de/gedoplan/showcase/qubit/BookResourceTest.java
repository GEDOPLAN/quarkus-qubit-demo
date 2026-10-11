package de.gedoplan.showcase.qubit;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class BookResourceTest {

  @Test
  void listsAllBooks() {
    given()
        .when().get("/api/books")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
  }

  @Test
  void filtersInStockBooks() {
    given()
        .when().get("/api/books/in-stock")
        .then()
        .statusCode(200)
        .body("inStock", everyItem(is(true)));
  }

  @Test
  void filtersByAuthor() {
    given()
        .when().get("/api/books/by-author/{name}", "Isaac Asimov")
        .then()
        .statusCode(200)
        .body("size()", is(2));
  }

  @Test
  void sortsByPriceAscending() {
    given()
        .when().get("/api/books/sorted-by-price")
        .then()
        .statusCode(200)
        .body("price[0]", is(9.49f));
  }

  @Test
  void paginates() {
    given()
        .when().get("/api/books/page/0/5")
        .then()
        .statusCode(200)
        .body("size()", is(5));
  }

  @Test
  void projectsTitles() {
    given()
        .when().get("/api/books/titles")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
  }

  @Test
  void projectsSummaries() {
    given()
        .when().get("/api/books/summaries")
        .then()
        .statusCode(200)
        .body("[0].title", notNullValue());
  }

  @Test
  void computesAveragePrice() {
    double average = given()
        .when().get("/api/books/stats/average-price")
        .then()
        .statusCode(200)
        .extract().body().as(Double.class);
    assertTrue(average > 0.0);
  }

  @Test
  void filtersByAuthorCaseInsensitive() {
    given()
        .when().get("/api/books/by-author-ci/{name}", "isaac asimov")
        .then()
        .statusCode(200)
        .body("size()", is(2));
  }

  @Test
  void countsInStock() {
    given()
        .when().get("/api/books/stats/count-in-stock")
        .then()
        .statusCode(200);
  }
}
