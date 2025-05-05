package iway.irshad.service.impl;

import iway.irshad.entity.Cart;
import iway.irshad.entity.Coupon;
import iway.irshad.entity.User;
import iway.irshad.repository.CartRepository;
import iway.irshad.repository.CouponRepository;
import iway.irshad.repository.UserRepository;
import iway.irshad.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public Cart applyCoupon(String code, double orderValue, User user) throws Exception {

        Coupon coupon = couponRepository.findByCode(code);
        Cart cart = cartRepository.findByUserId(user.getId());

        if (coupon == null) {
            throw new Exception("Coupon not valid");
        }
        if (user.getUsedCoupon().contains(coupon)) {
            throw new Exception("Coupon already is used");
        }
        if (orderValue < coupon.getMinimumOrdervalue()) {
            throw new Exception("Coupon order value is less than minimum order value" + coupon.getMinimumOrdervalue());
        }
        if (coupon.isActive() && LocalDate.now().isAfter(coupon.getValidityStartDate())
        && LocalDate.now().isBefore(coupon.getValidityEndDate())
        ) {
            user.getUsedCoupon().add(coupon);
            userRepository.save(user);

            double discountPrice = ( cart.getTotalSellingPrice() * coupon.getDiscountPercent() ) / 100;

            cart.setTotalSellingPrice(cart.getTotalSellingPrice() - discountPrice);
            cart.setCouponCode(code);
            cartRepository.save(cart);
            return cart;
        }

        throw new Exception("Coupon not valid");
    }

    @Override
    public Cart removeCoupon(String code, User user) throws Exception {
        Coupon coupon = couponRepository.findByCode(code);

        if (coupon == null) {
            throw new Exception("Coupon not found...");
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        double discountPrice = ( cart.getTotalSellingPrice() * coupon.getDiscountPercent() ) / 100;

        cart.setTotalSellingPrice(cart.getTotalSellingPrice() + discountPrice);
        cart.setCouponCode(null);

        return cartRepository.save(cart);
    }

    @Override
    public Coupon findCouponById(Long id) throws Exception {
        return couponRepository.findById(id).orElseThrow(()->
                new Exception("coupon not found"));
    }

    @Override
    @PreAuthorize("hasRole ('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole ('ADMIN')")
    public void deleteCoupon(Long id) throws Exception {
        findCouponById(id);
        couponRepository.deleteById(id);
    }
}
