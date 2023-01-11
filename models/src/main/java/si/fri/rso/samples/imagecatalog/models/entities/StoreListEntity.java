package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "store_list")
@NamedQueries(value =
        {
                @NamedQuery(name = "StoreListEntity.getAll",
                        query = "SELECT storelist FROM StoreListEntity storelist"),

                @NamedQuery(name = "StoreListEntity.find",
                        query = "SELECT storelist FROM StoreListEntity storelist WHERE storelist.storeId = :storeId AND storelist.productId = :productId")
        }
        )

public class StoreListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "cena")
    private Integer cena;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }
}
