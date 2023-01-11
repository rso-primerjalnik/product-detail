package si.fri.rso.samples.imagecatalog.services.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.rso.samples.imagecatalog.lib.ProductDetail;
import si.fri.rso.samples.imagecatalog.lib.Store;
import si.fri.rso.samples.imagecatalog.lib.StoreList;
import si.fri.rso.samples.imagecatalog.models.converters.ProductDetailConverter;
import si.fri.rso.samples.imagecatalog.models.converters.StoreListConverter;
import si.fri.rso.samples.imagecatalog.models.entities.ProductDetailEntity;
import si.fri.rso.samples.imagecatalog.models.entities.StoreListEntity;


@RequestScoped
public class ProductDetailBean {

    private Logger log = Logger.getLogger(ProductDetailBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;
    private String baseUrl;
    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://store-catalog-service:8080";
    }

    public List<ProductDetail> getProductDetails() {
        TypedQuery<ProductDetailEntity> query = em.createNamedQuery("ProductDetailEntity.getAll", ProductDetailEntity.class);
        List<ProductDetailEntity> resultList = query.getResultList();
        return resultList.stream().map(ProductDetailConverter::makeObject).collect(Collectors.toList());
    }

    public ProductDetail getProductInStore(Integer productId) {

        ProductDetailEntity productDetailEntity = em.find(ProductDetailEntity.class, productId);

        if (productDetailEntity == null) {
            throw new NotFoundException();
        }

        return ProductDetailConverter.makeObject(productDetailEntity);
    }


    public ProductDetail createProductDetail(StoreList storeList, Integer productId) {

        StoreListEntity storeListEntity = new StoreListEntity();

        ProductDetailEntity productDetailEntity = em.find(ProductDetailEntity.class, productId);

        storeListEntity.setProductId(storeList.getProductId());

        storeListEntity.setStoreId(storeList.getStoreId());

        storeListEntity.setCena(storeList.getCena());

        productDetailEntity.getStores().add(storeListEntity);

        try {
            TrBegin();
            em.persist(productDetailEntity);
            TrCommit();
        }
        catch (Exception e) {
            TrRollback();
        }

        if (productDetailEntity.getId() == null) {
            throw new RuntimeException("No entity");
        }

        return ProductDetailConverter.makeObject(productDetailEntity);
    }

    public StoreList putProductDetail(Integer productId, StoreList storeList) {

        StoreListEntity storeListEntity = null;

        try {
            storeListEntity = em.createNamedQuery("StoreListEntity.find", StoreListEntity.class)
                    .setParameter("storeId", storeList.getStoreId())
                    .setParameter("productId", productId)
                    .getSingleResult();
        }

        catch (Exception e) {}

        if (storeListEntity == null) {

            return null;
        }

        StoreListEntity storeListEntity1 = storeListEntity;
        storeListEntity1.setCena(storeList.getCena());


        try {
            TrBegin();
            em.persist(storeListEntity1);
            //em.persist(storeListEntity);
            TrCommit();
        }
        catch (Exception e) {
            TrRollback();
        }

        return StoreListConverter.makeObject(storeListEntity1);
    }

    public StoreList setAdditionalDataForStores(StoreList storeList) {
        Store store = httpClient.target(baseUrl + "/v1/products/filter")
                .queryParam("filter", "id:EQ:" + storeList.getStoreId())
                .request()
                .get(new GenericType<>(){});

        if(store != null) {
            storeList.setName(store.getName());
            storeList.setUrl(store.getUrl());
        }
        return storeList;
    }

    public boolean deleteProductDetail(Integer id) {

        ProductDetailEntity productEntity = em.find(ProductDetailEntity.class, id);

        if (productEntity != null) {
            try {
                TrBegin();
                em.remove(productEntity);
                TrCommit();
            }
            catch (Exception e) {
                TrRollback();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void TrBegin() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void TrCommit() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void TrRollback() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
