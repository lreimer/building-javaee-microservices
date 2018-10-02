package cloud.nativ.javaee.library;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static cloud.nativ.javaee.library.LibraryResource.createAuthorResourceUri;
import static cloud.nativ.javaee.library.LibraryResource.createBooksResourceUri;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResource {
    @Context
    private UriInfo uriInfo;

    private Map<Integer, String> authors = new HashMap<>();

    @PostConstruct
    public void initialize() {
        authors.put(1, "M.Leander Reimer");
        authors.put(2, "Douglas Adams");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray authors() {
        JsonArrayBuilder all = Json.createArrayBuilder();
        authors.forEach((i, s) -> all.add(asJsonObject(i, s,
                createAuthorResourceUri(i, uriInfo),
                createBooksResourceUri(i, uriInfo))));
        return all.build();
    }

    @GET
    @Path("/{authorId}")
    public Response author(@PathParam("authorId") Integer authorId) {
        String author = authors.get(authorId);
        URI autorUri = createAuthorResourceUri(authorId, uriInfo);
        URI booksUri = createBooksResourceUri(authorId, uriInfo);

        JsonObject jsonObject = asJsonObject(authorId, author, autorUri, booksUri);
        return Response.ok(jsonObject)
                .link(autorUri, "self")
                .link(booksUri, "books")
                .build();
    }

    private JsonObject asJsonObject(Integer authorId, String name, URI autorUri, URI booksUri) {
        return Json.createObjectBuilder()
                .add("id", authorId)
                .add("name", name)
                .add("_links", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder()
                                .add("href", autorUri.toString()))
                        .add("books", Json.createObjectBuilder()
                                .add("href", booksUri.toString())))
                .build();
    }
}
