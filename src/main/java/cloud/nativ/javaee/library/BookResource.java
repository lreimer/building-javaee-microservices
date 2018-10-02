package cloud.nativ.javaee.library;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cloud.nativ.javaee.library.LibraryResource.createAuthorResourceUri;
import static cloud.nativ.javaee.library.LibraryResource.createBookResourceUri;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {
    @Context
    private UriInfo uriInfo;

    private Map<String, Book> books = new HashMap<>();

    @PostConstruct
    public void initialize() {
        books.put("1234567890", Book.from("1234567890", "Building Webservices with Java EE 8", 1));
        books.put("0345391802", Book.from("0345391802", "The Hitchhiker's Guide to the Galaxy", 2));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray books(@QueryParam("authorId") @DefaultValue("-1") Integer authorId) {
        JsonArrayBuilder all = Json.createArrayBuilder();
        if (authorId == -1) {
            books.values()
                    .forEach(book -> all.add(asJsonObject(book,
                            createBookResourceUri(book.isbn, uriInfo),
                            createAuthorResourceUri(book.authorId, uriInfo))));
        } else {
            books.values().stream()
                    .filter(book -> Objects.equals(book.authorId, authorId))
                    .forEach(book -> all.add(asJsonObject(book,
                            createBookResourceUri(book.isbn, uriInfo),
                            createAuthorResourceUri(book.authorId, uriInfo))));
        }
        return all.build();
    }

    @GET
    @Path("/{isbn}")
    public Response book(@PathParam("isbn") String isbn) {
        Book book = books.get(isbn);
        URI bookUri = createBookResourceUri(isbn, uriInfo);
        URI authorUri = createAuthorResourceUri(book.authorId, uriInfo);
        JsonObject jsonObject = asJsonObject(book, bookUri, authorUri);
        return Response.ok(jsonObject)
                .link(bookUri, "self")
                .link(authorUri, "author")
                .build();
    }

    private JsonObject asJsonObject(Book book, URI bookUri, URI authorUri) {
        return Json.createObjectBuilder()
                .add("isbn", book.isbn)
                .add("title", book.title)
                .add("_links", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder()
                                .add("href", bookUri.toString()))
                        .add("author", Json.createObjectBuilder()
                                .add("href", authorUri.toString())))
                .build();
    }
}
