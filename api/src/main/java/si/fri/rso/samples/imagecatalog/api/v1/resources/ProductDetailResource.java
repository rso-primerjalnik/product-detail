package si.fri.rso.samples.imagecatalog.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.lib.ProductDetail;
import si.fri.rso.samples.imagecatalog.services.beans.ProductDetailBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;



@ApplicationScoped
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductDetailResource {

    private Logger log = Logger.getLogger(ProductDetailResource.class.getName());

    @Inject
    private ProductDetailBean productDetailBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get details for single product.", summary = "Get details for single product")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product details.",
                    content = @Content(schema = @Schema(implementation = ProductDetail.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Details of product could not be found."
            ),
    })
    @GET
    @Path("/{productId}")
    public Response getSingleProduct(@Parameter(description = "Product ID.", required = true)
                                     @PathParam("productId") Integer productId) {

        ProductDetail productDetail = productDetailBean.getProduct(productId);
        if (productDetail == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(productDetail).build();
    }





}
