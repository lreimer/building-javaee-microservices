package cloud.nativ.javaee.library;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The HATEOS showcase REST resource implementation class.
 */
@ApplicationScoped
@Path("library")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryResource {

    @Context
    private ResourceContext context;

    @Path("books")
    public BookResource books() {
        return context.getResource(BookResource.class);
    }

    @Path("author")
    public AuthorResource author() {
        return context.getResource(AuthorResource.class);
    }


    static URI createBookResourceUri(String isbn, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(LibraryResource.class)
                .path(LibraryResource.class, "books")
                .path(BookResource.class, "book")
                .build(isbn);
    }

    static URI createBooksResourceUri(Integer authorId, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(LibraryResource.class)
                .path(LibraryResource.class, "books")
                .queryParam("authorId", authorId)
                .build();
    }

    static URI createAuthorResourceUri(Integer authorId, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(LibraryResource.class)
                .path(LibraryResource.class, "author")
                .path(AuthorResource.class, "author")
                .build(authorId);
    }

}
