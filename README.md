# Qubit Demo

Beispielprojekt zum GEDOPLAN-Blogbeitrag
[JPA-Abfragen als Java-Lambdas: Die Quarkus-Extension Qubit im Praxistest](https://gedoplan.de/jpa-abfragen-java-lambdas-quarkus-qubit/).

Eine kleine REST-API für einen Buchkatalog, die [Qubit](https://github.com/quarkiverse/quarkus-qubit)
1.0.0 zeigt – eine Quarkus-Extension, die Lambda-Ausdrücke auf Panache-Entities
schon beim Build in JPA Criteria Queries übersetzt.

## Starten

Voraussetzungen: JDK 25 und Maven.

```bash
mvn quarkus:dev
```

Die Anwendung nutzt eine H2-In-Memory-Datenbank, Docker ist nicht nötig. Sie startet unter
<http://localhost:8080>.

## Endpunkte ausprobieren

```bash
# Alle Bücher
curl localhost:8080/api/books

# Filtern
curl localhost:8080/api/books/in-stock
curl localhost:8080/api/books/by-author/Isaac%20Asimov
curl localhost:8080/api/books/by-author-ci/isaac%20asimov

# Sortieren und paginieren
curl localhost:8080/api/books/sorted-by-price
curl localhost:8080/api/books/page/0/5

# Projektion auf ein Feld oder ein DTO
curl localhost:8080/api/books/titles
curl localhost:8080/api/books/summaries

# Aggregation
curl localhost:8080/api/books/stats/average-price
curl localhost:8080/api/books/stats/count-in-stock
```

## Was man sich ansehen sollte

- `Book.java` – eine gewöhnliche Panache-Entity.
- `BookRepository.java` – eine leere Klasse; `QubitRepository<Book, Long>` zu
  implementieren reicht, um die Fluent-Query-API zu bekommen.
- `BookResource.java` – eine Abfrage pro Endpunkt, jeweils aufgebaut aus `where`,
  `select`, `sortedBy`, `skip`/`limit` oder einer Aggregation.

## Stolperstein: geerbte Panache-Methoden am Repository

`QubitRepository` erweitert Panaches `PanacheRepositoryBase`. Deshalb kompilieren
`bookRepository.listAll()`, `findById()`, `count()`, `find()` oder `deleteById()` –
scheitern aber zur Laufzeit mit einer irreführenden
`IllegalStateException: ... did you forget to annotate your entity with @Entity?`
(`Book` ist annotiert). Die Qubit-API am selben Repository funktioniert, ebenso die
statischen Methoden der Entity (`Book.listAll()`, `Book.findById()`) und klassisches
JPA über den `EntityManager` (`em.find(Book.class, id)`).

`InheritedPanacheMethodsTest` zeigt beide Seiten:

```bash
mvn test -Dtest=InheritedPanacheMethodsTest
```

Um den Fehler in der laufenden Anwendung zu sehen, in `BookResource.getAll()`
`return bookRepository.listAll();` eintragen und `curl localhost:8080/api/books`
aufrufen (HTTP 500).
