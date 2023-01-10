package si.fri.rso.samples.imagecatalog.lib;

import java.util.List;

public class ProductDetail {

    private Integer id;

    private List<StoreList> stores;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<StoreList> getStores() {
        return stores;
    }

    public void setStores(List<StoreList> stores) {
        this.stores = stores;
    }
}
