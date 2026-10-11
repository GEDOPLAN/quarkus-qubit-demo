package de.gedoplan.showcase.qubit;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QubitRepository extends Panache's PanacheRepositoryBase, so methods like listAll() or findById()
 * compile on BookRepository - but they fail at runtime. The Qubit query API on the same repository,
 * the static Panache methods on the entity, and plain JPA via the EntityManager work fine.
 */
@QuarkusTest
class InheritedPanacheMethodsTest {

  @Inject
  BookRepository bookRepository;

  @Inject
  EntityManager em;

  // --- Inherited Panache methods on the repository: compile, but fail at runtime ---

  @Test
  void repositoryListAllFails() {
    assertFailsWithMisleadingMessage(() -> bookRepository.listAll());
  }

  @Test
  void repositoryFindByIdFails() {
    assertFailsWithMisleadingMessage(() -> bookRepository.findById(1L));
  }

  @Test
  void repositoryCountFails() {
    assertFailsWithMisleadingMessage(() -> bookRepository.count());
  }

  @Test
  void repositoryFindFails() {
    assertFailsWithMisleadingMessage(() -> bookRepository.find("author", "Isaac Asimov").list());
  }

  @Test
  @TestTransaction
  void repositoryDeleteByIdFails() {
    assertFailsWithMisleadingMessage(() -> bookRepository.deleteById(2L));
  }

  // --- What works instead ---

  @Test
  void entityListAllWorks() {
    assertEquals(14, Book.listAll().size());
  }

  @Test
  void entityFindByIdWorks() {
    Book book = Book.findById(1L);
    assertEquals("Dune", book.title);
  }

  @Test
  void qubitWhereByIdWorksOnRepository() {
    Long id = 1L;
    List<Book> books = bookRepository.where(b -> b.id.equals(id)).toList();
    assertEquals(1, books.size());
    assertEquals("Dune", books.get(0).title);
  }

  @Test
  void qubitCountWorksOnRepository() {
    assertEquals(11, bookRepository.where(b -> b.inStock).count());
  }

  @Test
  void qubitSortedByIdListsAllBooks() {
    assertEquals(14, bookRepository.sortedBy(b -> b.id).toList().size());
  }

  @Test
  void plainJpaFindWorks() {
    assertEquals("Dune", em.find(Book.class, 1L).title);
  }

  @Test
  void plainJpaCountWorks() {
    assertEquals(14L, em.createQuery("select count(b) from Book b", Long.class).getSingleResult());
  }

  private static void assertFailsWithMisleadingMessage(Executable call) {
    IllegalStateException e = assertThrows(IllegalStateException.class, call);
    // Book *is* annotated with @Entity - the message points in the wrong direction.
    assertTrue(e.getMessage().contains("did you forget to annotate your entity with @Entity?"),
        e.getMessage());
  }
}
