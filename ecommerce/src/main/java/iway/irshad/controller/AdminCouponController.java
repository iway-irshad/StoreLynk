package iway.irshad.controller;

import iway.irshad.entity.Cart;
import iway.irshad.entity.Coupon;
import iway.irshad.entity.User;
import iway.irshad.service.CouponService;
import iway.irshad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class AdminCouponController {

    private final UserService userService;
    private final CouponService couponService;

    @PostMapping("/apply")
    public ResponseEntity<Cart> applyCoupon(
            @RequestParam String apply,
            @RequestParam String code,
            @RequestParam double orderValue,
            @RequestHeader("Authorization") String jwtToken
    ) throws Exception{
        User user = userService.findUserByJwtToken(jwtToken);
        Cart cart;

        if (apply.equals("true")) {
            cart = couponService.applyCoupon(code, orderValue, user);
        } else {
            cart = couponService.removeCoupon(code, user);
        }
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon newCoupon = couponService.createCoupon(coupon);
        return new ResponseEntity<>(newCoupon, HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) throws Exception {
        couponService.deleteCoupon(id);
        return new ResponseEntity<>("Coupon deleted Successfully", HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.findAllCoupons();
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }
}
