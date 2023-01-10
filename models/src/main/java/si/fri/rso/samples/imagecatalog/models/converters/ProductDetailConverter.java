package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.ProductDetail;
import si.fri.rso.samples.imagecatalog.lib.StoreList;
import si.fri.rso.samples.imagecatalog.models.entities.ProductDetailEntity;
import si.fri.rso.samples.imagecatalog.models.entities.StoreListEntity;

import java.util.List;

public class ProductDetailConverter {

    public static ProductDetail makeObject(ProductDetailEntity entity) {
        ProductDetail obj = new ProductDetail();
        obj.setId(entity.getId());
        List<StoreList> storeLists = entity.getStores().stream().map(StoreListConverter::makeObject).toList();
        obj.setStores(storeLists);

        return obj;
    }

    public static ProductDetailEntity makeEntity(ProductDetail obj) {
        ProductDetailEntity entity = new ProductDetailEntity();
        List<StoreListEntity> storeListEntities = obj.getStores().stream().map(StoreListConverter::makeEntity).toList();
        entity.setStores(storeListEntities);

        return entity;
    }

}
