package si.fri.rso.samples.imagecatalog.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.rso.samples.imagecatalog.lib.ProductDetail;
import si.fri.rso.samples.imagecatalog.models.converters.ProductDetailConverter;
import si.fri.rso.samples.imagecatalog.models.entities.ProductDetailEntity;


@RequestScoped
public class ProductDetailBean {

    private Logger log = Logger.getLogger(ProductDetailBean.class.getName());

    @Inject
    private EntityManager em;

    public List<ProductDetail> getProductDetails() {
        TypedQuery<ProductDetailEntity> query = em.createNamedQuery("ProductDetailEntity.getAll", ProductDetailEntity.class);
        List<ProductDetailEntity> resultList = query.getResultList();
        return resultList.stream().map(ProductDetailConverter::makeObject).collect(Collectors.toList());
    }

    public ProductDetail getProduct(Integer id) {

        ProductDetailEntity productDetailEntity = em.find(ProductDetailEntity.class, id);

        if (productDetailEntity == null) {
            throw new NotFoundException();
        }

        return ProductDetailConverter.makeObject(productDetailEntity);
    }

    public ProductDetail createProductDetial(ProductDetail productDetail) {

        ProductDetailEntity productEntity = ProductDetailConverter.makeEntity(productDetail);

        try {
            TrBegin();
            em.persist(productEntity);
            TrCommit();
        }
        catch (Exception e) {
            TrRollback();
        }

        if (productEntity.getId() == null) {
            throw new RuntimeException("No entity");
        }

        return ProductDetailConverter.makeObject(productEntity);
    }

    public ProductDetail putProductDetail(Integer id, ProductDetail productDetail) {

        ProductDetailEntity c = em.find(ProductDetailEntity.class, id);

        if (c == null) {
            return null;
        }

        ProductDetailEntity updatedProductEntity = ProductDetailConverter.makeEntity(productDetail);

        try {
            TrBegin();
            updatedProductEntity.setId(c.getId());
            updatedProductEntity = em.merge(updatedProductEntity);
            TrCommit();
        }
        catch (Exception e) {
            TrRollback();
        }

        return ProductDetailConverter.makeObject(updatedProductEntity);
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
