package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Carts;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.CartsService;
import com.example.demo.service.ProductDetailService;
import com.example.demo.service.UsersService;

@Controller
public class CartsController {
    @Autowired
    private CartsService cartsService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping("/cart")
    public String viewCart(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null) {
            List<Carts> cartItems = cartsService.findByUserId(user.getId());
            model.addAttribute("username", user.getUserName());
            int totalAmount = 0;

            for (Carts cart : cartItems) {
                Products product = productDetailService.fetchProductById(cart.getProductId());
                cart.setProductName(product.getName());
                cart.setProductPrice(product.getPrice());
                cart.setKindName(cartsService.getKindName(cart.getCategoryId(), cart.getKindId())); // 種類の設定
                totalAmount += cart.getProductPrice() * cart.getQuantity();
            }

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
        } else {
            model.addAttribute("cartItems", null);
        }

        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("cartId") Long cartId) {
        cartsService.deleteCartById(cartId);
        return "redirect:/cart";
    }
    
    @PostMapping("/cart/updateQuantity")
    public String updateCartQuantity(@RequestParam("cartId") Long cartId, @RequestParam("quantity") int quantity) {
        Carts cart = cartsService.findCartById(cartId);
        if (cart != null) {
            cart.setQuantity(quantity);
            cartsService.saveCart(cart);
        }
        return "redirect:/cart";
    }
}
