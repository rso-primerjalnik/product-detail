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
import si.fri.rso.samples.imagecatalog.lib.Store;
import si.fri.rso.samples.imagecatalog.lib.StoreList;
import si.fri.rso.samples.imagecatalog.services.beans.ProductDetailBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



@ApplicationScoped
@Path("/product-detail")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductDetailResource {

    private Logger log = Logger.getLogger(ProductDetailResource.class.getName());

    @Inject
    private ProductDetailBean productDetailBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all product details.", summary = "Get all product details")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of product details.",
                    content = @Content(schema = @Schema(implementation = ProductDetail.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getProductDetails() {

        List<ProductDetail> productDetails = productDetailBean.getProductDetails();

        List<ProductDetail> newProductDetails = new ArrayList<>();

        for (ProductDetail pd : productDetails) {

            List<StoreList> listofstores = pd.getStores();
            List<StoreList> newlistofstores = new ArrayList<>();

            for (StoreList sl : listofstores) {
                StoreList newsl = productDetailBean.setAdditionalDataForStores(sl);
                newlistofstores.add(newsl);
            }

            pd.setStores(newlistofstores);
            newProductDetails.add(pd);
        }

        return Response.status(Response.Status.OK).entity(newProductDetails).build();
    }

    @Operation(description = "Get product detail by id.", summary = "Get product detail by id")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "product detail information.",
                    content = @Content(
                            schema = @Schema(implementation = ProductDetail.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "product detail not found."
            )})
    @GET
    @Path("/{productDetailId}")
    public Response getProductDetailById(@Parameter(description = "product detail ID.", required = true)
                                        @PathParam("productDetailId") Integer productDetailId) {

        ProductDetail pd = productDetailBean.getProductInStore(productDetailId);

        if (pd == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<StoreList> listofstores = pd.getStores();
        List<StoreList> newlistofstores = new ArrayList<>();

        for (StoreList sl : listofstores) {
            StoreList newsl = productDetailBean.setAdditionalDataForStores(sl);
            newlistofstores.add(newsl);
        }

        pd.setStores(newlistofstores);

        return Response.status(Response.Status.OK).entity(pd).build();
    }


    @Operation(description = "Add product detail.", summary = "Add product detail")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "product detail successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error. Missing parameters.")
    })
    @POST
    @Path("/{productDetailId}")
    public Response createProductDetail(
            @Parameter(description = "Product detail ID.", required = true)
            @PathParam("productDetailId") Integer productDetailId,
            @RequestBody(
            description = "DTO object with store list data.",
            required = true, content = @Content(
            schema = @Schema(implementation = StoreList.class))) StoreList storeList) {

        if (storeList.getStoreId() == null || storeList.getCena() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            ProductDetail productDetail = productDetailBean.createProductDetail(storeList, productDetailId);
            return Response.status(Response.Status.CREATED).entity(productDetail).build();
        }
    }

    @Operation(description = "Insert store into detail.", summary = "Insert store into detail")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product detail items successfully changed."
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad request. Missing product data."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Product detail or product not found."
            )
    })
    @PUT
    @Path("/{productDetailId}")
    public Response putProductDetail(@Parameter(description = "Product detail ID.", required = true)
                                    @PathParam("productDetailId") Integer productDetailId,
                                    @RequestBody(
                                            description = "DTO object with product detail store data.",
                                            required = true, content = @Content(
                                            schema = @Schema(implementation = StoreList.class)))
                                    StoreList storeList) {

        if (storeList.getStoreId() == null)
            return Response.status(Response.Status.BAD_REQUEST).build();

        StoreList updatedStoreList = null;
        try {
            updatedStoreList = productDetailBean.putProductDetail(productDetailId, storeList);
        }
        catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (updatedStoreList == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        StoreList newStoreList = productDetailBean.setAdditionalDataForStores(updatedStoreList);

        return Response.status(Response.Status.OK).entity(newStoreList).build();
    }


    @Operation(description = "Delete product detail.", summary = "Delete product detail")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Product detail successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Product detail not found."
            )
    })
    @DELETE
    @Path("/{productDetailId}")
    public Response deleteProductDetail(@Parameter(description = "Product detail ID.", required = true)
                                       @PathParam("productDetailId") Integer productDetailId) {

        boolean deleted = productDetailBean.deleteProductDetail(productDetailId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
