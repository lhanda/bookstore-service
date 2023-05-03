package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.configurations.RestConfig;
import com.example.demo.configurations.SpringSecurityConfig;
import com.example.demo.data.models.Author;
import com.example.demo.data.models.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
  classes = {
    BookstoreServiceApplication.class,
    RestConfig.class,
    SpringSecurityConfig.class,
  },
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class BookstoreServiceApplicationTests {

  private static final ObjectMapper om = new ObjectMapper();

  private static String BOOKS_ENDPOINT = "http://localhost:8080/books/";
  private static String AUTHORS_ENDPOINT = "http://localhost:8080/authors/";

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  @Order(1)
  public void addNewBookWithUser() throws Exception {
    Book testBook = this.getNewBook();
    ResponseEntity<Book> bookResponseEntity =
      this.testRestTemplate.withBasicAuth("user", "password")
        .postForEntity(BOOKS_ENDPOINT, testBook, Book.class);
    this.printJSON(bookResponseEntity.getBody());

    assertEquals(
      MediaType.APPLICATION_JSON,
      bookResponseEntity.getHeaders().getContentType()
    );
    assertEquals(HttpStatus.CREATED, bookResponseEntity.getStatusCode());
  }

  @Test
  @Order(2)
  public void findBookByTitleWithUser() {
    String urlTemplate = UriComponentsBuilder
      .fromHttpUrl(BOOKS_ENDPOINT + "search/findByTitle")
      .queryParam("title", "{title}")
      .encode()
      .toUriString();

    Map<String, String> params = new HashMap<>();
    params.put("title", this.getNewBook().getTitle());

    ResponseEntity<Book> response =
      this.testRestTemplate.withBasicAuth("user", "password")
        .exchange(
          urlTemplate,
          HttpMethod.GET,
          this.getDefaultHttpEntity(),
          Book.class,
          params
        );
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(this.getNewBook().getTitle(), response.getBody().getTitle());
  }

  @Test
  @Order(3)
  public void addNewAuthorWithUser() {
    Author ben = this.getNewAuthor();
    ResponseEntity<Author> authorResponseEntity =
      this.testRestTemplate.withBasicAuth("user", "password")
        .postForEntity(AUTHORS_ENDPOINT, ben, Author.class);
    this.printJSON(authorResponseEntity.getBody());

    assertEquals(
      MediaType.APPLICATION_JSON,
      authorResponseEntity.getHeaders().getContentType()
    );
    assertEquals(HttpStatus.CREATED, authorResponseEntity.getStatusCode());
  }

  @Test
  @Order(4)
  public void updateAuthorBookLinkWithAdmin() {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.add("Content-Type", "text/uri-list");
    HttpEntity<String> bookHttpEntity = new HttpEntity<>(
      BOOKS_ENDPOINT + "/0000000022",
      requestHeaders
    );
    ResponseEntity<Book> updateAuthorBookLinkResponse =
      this.testRestTemplate.withBasicAuth("admin", "password")
        .exchange(
          AUTHORS_ENDPOINT + "/7/book",
          HttpMethod.PUT,
          bookHttpEntity,
          Book.class
        );
    assertEquals(
      HttpStatus.NO_CONTENT,
      updateAuthorBookLinkResponse.getStatusCode()
    );
  }

  @Test
  @Order(5)
  public void findBookByAuthorsNameWithUser() {
    String urlTemplate = UriComponentsBuilder
      .fromHttpUrl(BOOKS_ENDPOINT + "search/findByAuthorsName")
      .queryParam("authorName", "{authorName}")
      .encode()
      .toUriString();

    Map<String, String> params = new HashMap<>();
    params.put("authorName", this.getNewAuthor().getName());

    ResponseEntity<Book> response =
      this.testRestTemplate.withBasicAuth("user", "password")
        .exchange(
          urlTemplate,
          HttpMethod.GET,
          this.getDefaultHttpEntity(),
          Book.class,
          params
        );
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(this.getNewBook().getTitle(), response.getBody().getTitle());

    ResponseEntity<Book> getAuthorBookLinkResponse =
      this.testRestTemplate.withBasicAuth("user", "password")
        .getForEntity(AUTHORS_ENDPOINT + "/7/book", Book.class);
    assertEquals(HttpStatus.OK, getAuthorBookLinkResponse.getStatusCode());
    assertEquals(
      getAuthorBookLinkResponse.getBody().getTitle(),
      this.getNewBook().getTitle()
    );
  }

  @Test
  @Order(6)
  public void deleteNewBookWithUser() {
    ResponseEntity<Book> deleteBookResponseEntity =
      this.testRestTemplate.withBasicAuth("user", "password")
        .exchange(
          BOOKS_ENDPOINT + this.getNewBook().getIsbn(),
          HttpMethod.DELETE,
          this.getDefaultHttpEntity(),
          Book.class
        );
    assertEquals(
      HttpStatus.FORBIDDEN,
      deleteBookResponseEntity.getStatusCode()
    );
  }

  @Test
  @Order(7)
  public void deleteNewBookWithAdmin() {
    ResponseEntity<Book> deleteBookResponseEntity =
      this.testRestTemplate.withBasicAuth("admin", "password")
        .exchange(
          BOOKS_ENDPOINT + this.getNewBook().getIsbn(),
          HttpMethod.DELETE,
          this.getDefaultHttpEntity(),
          Book.class
        );
    assertEquals(
      HttpStatus.NO_CONTENT,
      deleteBookResponseEntity.getStatusCode()
    );
  }

  private HttpEntity<?> getDefaultHttpEntity() {
    return new HttpEntity<>(this.getDefaultHeaders());
  }

  private HttpHeaders getDefaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  private void printJSON(Object object) {
    String result;
    try {
      result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      log.info(result);
    } catch (JsonProcessingException jpe) {
      log.error("Error processing object {} to Json!", object.toString(), jpe);
    }
  }

  private Author getNewAuthor() {
    return Author
      .builder()
      .name("Ben")
      .birthday(Date.valueOf("1988-08-08"))
      .build();
  }

  private Book getNewBook() {
    Book testBook = Book
      .builder()
      .isbn("0000000022")
      .title("Test Book")
      .year(2022)
      .price(22)
      .genre("thriller")
      .build();
    return testBook;
  }
}
