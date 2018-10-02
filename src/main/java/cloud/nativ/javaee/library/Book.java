package cloud.nativ.javaee.library;

import lombok.Data;

@Data
public class Book {
    String isbn;
    String title;
    Integer authorId;

    public static Book from(String isbn, String title, Integer authorId) {
        Book book = new Book();
        book.isbn = isbn;
        book.title = title;
        book.authorId = authorId;
        return book;
    }
}