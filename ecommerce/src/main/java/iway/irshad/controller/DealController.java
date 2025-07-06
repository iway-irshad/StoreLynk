package iway.irshad.controller;

import iway.irshad.entity.Deal;
import iway.irshad.response.ApiResponse;
import iway.irshad.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/deals")

public class DealController {

    private final DealService dealService;

    @GetMapping
    public ResponseEntity<List<Deal>> getAllDeals(

    ) {
        List<Deal>  getDeals = dealService.getDeals();
        return new ResponseEntity<>(getDeals, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Deal> createDeal(
            @RequestBody Deal deals
    ) {
        Deal createdDeal = dealService.createDeal(deals);
        return new ResponseEntity<>(createdDeal, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Deal> updateDeal(
            @PathVariable Long id,
            @RequestBody Deal deal
    ) throws Exception {
        Deal updatedDeal = dealService.updateDeal(deal, id);
        return ResponseEntity.ok(updatedDeal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDeal(
            @PathVariable Long id
    ) throws Exception {
        dealService.deleteDeal(id);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Deal deleted");
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }
}
