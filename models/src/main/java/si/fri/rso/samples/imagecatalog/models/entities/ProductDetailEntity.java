package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "product_detail")
@NamedQueries(value =
        {
                @NamedQuery(name = "ProductDetailEntity.getAll",
                        query = "SELECT productdetail FROM ProductDetailEntity productdetail")
        })
public class ProductDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id")
    private Integer productId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "store_id")
    private List<StoreListEntity> stores;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<StoreListEntity> getStores() {
        return stores;
    }

    public void setStores(List<StoreListEntity> stores) {
        this.stores = stores;
    }
}