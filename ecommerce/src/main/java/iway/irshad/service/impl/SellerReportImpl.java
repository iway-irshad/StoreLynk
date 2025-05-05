package iway.irshad.service.impl;

import iway.irshad.entity.Seller;
import iway.irshad.entity.SellerReport;
import iway.irshad.repository.SellerReportRepository;
import iway.irshad.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerReportImpl implements SellerReportService {

    private final SellerReportRepository sellerReportRepository;

    @Override
    public SellerReport getSellerReport(Seller seller) {
        SellerReport sellerReport = sellerReportRepository.findBySellerId(seller.getId());

        if (sellerReport == null) {
           var newReport = new SellerReport();
           newReport.setSeller(seller);
           return sellerReportRepository.save(newReport);
        }
        return sellerReport;
    }

    @Override
    public SellerReport updateSellerReport(SellerReport sellerReport) {
        return sellerReportRepository.save(sellerReport);
    }
}
