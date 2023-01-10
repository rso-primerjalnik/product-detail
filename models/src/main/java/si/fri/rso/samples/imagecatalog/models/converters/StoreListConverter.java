package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.StoreList;
import si.fri.rso.samples.imagecatalog.models.entities.StoreListEntity;

public class StoreListConverter {

    public static StoreList makeObject(StoreListEntity entity) {

        StoreList obj = new StoreList();
        obj.setId(entity.getId());
        obj.setStoreId(entity.getStoreId());
        obj.setProductId(entity.getProductId());
        obj.setCena(entity.getCena());

        return obj;
    }

    public static StoreListEntity makeEntity(StoreList obj) {

        StoreListEntity entity = new StoreListEntity();
        entity.setStoreId(obj.getStoreId());
        entity.setProductId(obj.getProductId());
        entity.setCena(obj.getCena());

        return entity;
    }
}
