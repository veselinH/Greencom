package bg.greencom.greencomwebapp.model.entity;

import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Additional_packages")
public class AdditionalPackageEntity extends BaseEntity{

    private AdditionalPackageEnum name;
    private BigDecimal price;

    public AdditionalPackageEntity() {
    }

    @Enumerated(EnumType.STRING)
    public AdditionalPackageEnum getName() {
        return name;
    }

    public AdditionalPackageEntity setName(AdditionalPackageEnum name) {
        this.name = name;
        return this;
    }

    @Column(nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public AdditionalPackageEntity setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
