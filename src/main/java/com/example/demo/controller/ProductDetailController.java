package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Carts;
import com.example.demo.entity.Products;
import com.example.demo.entity.RacketTypes;
import com.example.demo.entity.RubberColors;
import com.example.demo.entity.ShoesSizes;
import com.example.demo.entity.Users;
import com.example.demo.service.CartsService;
import com.example.demo.service.ProductDetailService;
import com.example.demo.service.UsersService;

@Controller
public class ProductDetailController {
	@Autowired									//クラスをインスタンス化
	private ProductDetailService productDetailService;    //ProductsService型の変数productsServiceを宣言

	@Autowired
	private UsersService usersService;

    @Autowired
    private CartsService cartsService;
	
	@GetMapping("/products/product_detail/{productId}")							//URLを紐づけ
	public String viewProductDetail(@PathVariable Long productId, Model model) {
        Products product = productDetailService.fetchProductById(productId);
        model.addAttribute("product", product);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();

        Users user = usersService.findByMailAddress(mailAddress);
        
        if (user == null) {
            model.addAttribute("loggedInUserId", -9999L);
        } else {
            Long loggedInUser = user.getId();
            model.addAttribute("loggedInUserId", loggedInUser);
            model.addAttribute("username", user.getUserName());

            // ユーザーと商品に対応するカートがあるかを確認
            cartsService.findByUserIdAndProductId(loggedInUser, productId).ifPresent(cart -> {
                model.addAttribute("initialQuantity", cart.getQuantity());
            });
        }
        
        Long categoryId = product.getCategory().getId();
        if (categoryId == 1) {  // ラケットの場合
            List<RacketTypes> racketTypes = productDetailService.findRacketTypesByProductId(productId);
            model.addAttribute("racketTypes", racketTypes);
        } else if (categoryId == 2) {  // ラバーの場合
            List<RubberColors> rubberColors = productDetailService.findRubberColorsByProductId(productId);
            model.addAttribute("rubberColors", rubberColors);
        } else if (categoryId == 3) {  // シューズの場合
            List<ShoesSizes> shoesSizes = productDetailService.findShoesSizesByProductId(productId);
            model.addAttribute("shoesSizes", shoesSizes);
        }
        
        
        return "productDetail";
    }

    @PostMapping("/productDetail/update")
    public String updateProduct(@ModelAttribute("product") Products product) {
        productDetailService.updateProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/productDetail/delete")
    public String deleteProduct(@ModelAttribute("product") Products product) {
        productDetailService.deleteProduct(product.getId());
        return "redirect:/products";
    }

    @PostMapping("/productDetail/addToCart")
    public String addToCart(@RequestParam("productId") Long productId, 
    						@RequestParam("quantity") int quantity, 
    				        @RequestParam("kindId") Long kindId,
    				        @RequestParam("categoryId") Long categoryId,
    				        Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users user = usersService.findByMailAddress(username);

        if (user != null) {
        	Long userId = user.getId();
            cartsService.findByUserIdAndProductIdAndKindId(userId, productId, kindId).ifPresentOrElse(cart -> {
                cart.setQuantity(quantity);
                cart.setKindId(kindId);
                cart.setCategoryId(categoryId);
                cartsService.saveCart(cart);
            }, () -> {
                Carts newCart = new Carts();
                newCart.setUserId(userId);
                newCart.setProductId(productId);
                newCart.setQuantity(quantity);
                newCart.setKindId(kindId);
                newCart.setCategoryId(categoryId);
                cartsService.saveCart(newCart);
            });
        }

        return "redirect:/cart";
    }
}
