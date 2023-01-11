package si.fri.rso.samples.imagecatalog.lib;

import java.util.List;

public class ProductDetail {

    private Integer productId;

    private List<StoreList> stores;

    public Integer getId() {
        return productId;
    }

    public void setId(Integer id) {
        this.productId = id;
    }

    public List<StoreList> getStores() {
        return stores;
    }

    public void setStores(List<StoreList> stores) {
        this.stores = stores;
    }
}
