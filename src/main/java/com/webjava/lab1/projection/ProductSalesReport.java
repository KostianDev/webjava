package com.webjava.lab1.projection;

import java.math.BigDecimal;

public interface ProductSalesReport {

  Long getProductId();

  String getProductName();

  String getCategoryName();

  Long getTotalQuantitySold();

  BigDecimal getTotalRevenue();
}
