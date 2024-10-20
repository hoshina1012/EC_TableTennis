package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

@Controller
public class TopController {
	@Autowired									//クラスをインスタンス化
	private ProductsService productsService;   

    @Autowired
    private UsersService usersService;
	
	@GetMapping("/")
	public String viewTop(Model model) {
		List<Products> products = productsService.fetchAllProducts();
		model.addAttribute("products", products);
		model.addAttribute("product", new Products());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
        
		return "top";
	}
}
