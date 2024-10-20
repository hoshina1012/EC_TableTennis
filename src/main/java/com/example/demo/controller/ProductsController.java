package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Categories;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.CategoriesService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

@Controller
public class ProductsController {
	@Autowired									//クラスをインスタンス化
	private ProductsService productsService;    //ProductsService型の変数productsServiceを宣言
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private CategoriesService categoriesService;
	
    @GetMapping("/products")
    public String viewProducts(Model model, 
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 6;  // 1ページに表示する商品数
        Page<Products> productsPage = productsService.fetchProducts(page, pageSize);
        
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("product", new Products());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if(user != null) {
            model.addAttribute("username", user.getUserName());
            model.addAttribute("loggedInUserId", user.getId());
            model.addAttribute("userStatus", user.getUserStatus());
        }

        return "products";
    }
	
    @GetMapping("/products/add")
    public String viewProductsAdd(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null && user.getUserStatus() == 0) {
            return "redirect:/products";
        }

        model.addAttribute("product", new Products());
        
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        
        if (user != null) {
            model.addAttribute("username", user.getUserName());
            model.addAttribute("loggedInUserId", user.getId());
        }

        return "productsAdd";
    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute("product") Products product) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 認証されたユーザー名を取得
        Users loggedInUser = usersService.findByMailAddress(username);
        product.setUser(loggedInUser);
    	
        productsService.saveProduct(product);
        return "redirect:/products";
    }
}
