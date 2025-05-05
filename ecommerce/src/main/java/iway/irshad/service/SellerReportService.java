package iway.irshad.service;

import iway.irshad.entity.Seller;
import iway.irshad.entity.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport(SellerReport sellerReport);

}
