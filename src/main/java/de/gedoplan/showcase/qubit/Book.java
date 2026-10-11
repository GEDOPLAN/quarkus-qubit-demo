package de.gedoplan.showcase.qubit;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Book extends PanacheEntity {

  public String title;
  public String author;
  public String genre;
  public BigDecimal price;
  public LocalDate publishedDate;
  public boolean inStock;

  public Book() {
  }

  public Book(String title, String author, String genre, BigDecimal price, LocalDate publishedDate,
      boolean inStock) {
    this.title = title;
    this.author = author;
    this.genre = genre;
    this.price = price;
    this.publishedDate = publishedDate;
    this.inStock = inStock;
  }
}
