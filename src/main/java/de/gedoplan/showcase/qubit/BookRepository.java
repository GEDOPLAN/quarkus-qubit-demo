package de.gedoplan.showcase.qubit;

import io.quarkiverse.qubit.QubitRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookRepository implements QubitRepository<Book, Long> {
}
