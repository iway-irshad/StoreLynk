package iway.irshad.controller;

import iway.irshad.entity.Home;
import iway.irshad.entity.HomeCategory;
import iway.irshad.service.HomeCategoryService;
import iway.irshad.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeCategoryController {

    private final HomeCategoryService homeCategoryService;
    private final HomeService homeService;

    @PostMapping("/home/categories")
    public ResponseEntity<Home> createHomeCategories(
            @RequestBody List<HomeCategory> homeCategories
    ) {
        List<HomeCategory> categories = homeCategoryService.createCategories(homeCategories);

        Home home = homeService.createHomePageData(categories);
        return new ResponseEntity<>(home, HttpStatus.ACCEPTED);
    }

    @GetMapping("/admin/home-category")
    public ResponseEntity<List<HomeCategory>> getHomeCategories(
    ) throws Exception {
        List<HomeCategory> homeCategories = homeCategoryService.getAllHomeCategories();
        return new ResponseEntity<>(homeCategories, HttpStatus.OK);
    }

    @PatchMapping("/admin/home-category/{id}")
    public ResponseEntity<HomeCategory> updateHomeCategory(
            @PathVariable Long id,
            @RequestBody HomeCategory homeCategory
    ) throws Exception {
        HomeCategory updateCategory = homeCategoryService.updateHomeCategory(homeCategory, id);
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);
    }
}
