package de.gedoplan.showcase.qubit;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

  @Inject
  BookRepository bookRepository;

  @GET
  public List<Book> getAll() {
    return bookRepository.sortedBy(b -> b.id).toList();
  }

  @GET
  @Path("/in-stock")
  public List<Book> getInStock() {
    return bookRepository.where(b -> b.inStock).toList();
  }

  @GET
  @Path("/by-author/{name}")
  public List<Book> getByAuthor(@PathParam("name") String name) {
    return bookRepository.where(b -> b.author.equals(name)).toList();
  }

  @GET
  @Path("/by-author-ci/{name}")
  public List<Book> getByAuthorCaseInsensitive(@PathParam("name") String name) {
    return bookRepository.where(b -> b.author.toLowerCase().equals(name.toLowerCase())).toList();
  }

  @GET
  @Path("/sorted-by-price")
  public List<Book> getSortedByPrice() {
    return bookRepository.sortedBy(b -> b.price).toList();
  }

  @GET
  @Path("/page/{offset}/{limit}")
  public List<Book> getPage(@PathParam("offset") int offset, @PathParam("limit") int limit) {
    return bookRepository.sortedBy(b -> b.id)
        .skip(offset)
        .limit(limit)
        .toList();
  }

  @GET
  @Path("/titles")
  public List<String> getTitles() {
    return bookRepository.select(b -> b.title).toList();
  }

  @GET
  @Path("/summaries")
  public List<BookSummary> getSummaries() {
    return bookRepository.select(b -> new BookSummary(b.title, b.author)).toList();
  }

  @GET
  @Path("/stats/average-price")
  public Double getAveragePrice() {
    return bookRepository.avg(b -> b.price).getSingleResult();
  }

  @GET
  @Path("/stats/count-in-stock")
  public long countInStock() {
    return bookRepository.where(b -> b.inStock).count();
  }
}
