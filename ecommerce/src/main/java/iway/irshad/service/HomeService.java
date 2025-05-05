package iway.irshad.service;

import iway.irshad.entity.Home;
import iway.irshad.entity.HomeCategory;

import java.util.List;

public interface HomeService {

    Home createHomePageData(List<HomeCategory> allCategories);
}
