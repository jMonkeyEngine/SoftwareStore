package com.jayfella.website.database.entity.page.embedded;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class PaymentData {

    private BigDecimal price = new BigDecimal(5.0);
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    private long purchaseCount = 0;
    public long getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(long purchaseCount) { this.purchaseCount = purchaseCount; }

    public void copyTo(PaymentData paymentData) {
        paymentData.setPrice(price);
        paymentData.setPurchaseCount(purchaseCount);
    }

}
