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
import com.example.demo.entity.Colors;
import com.example.demo.entity.Products;
import com.example.demo.entity.RacketTypes;
import com.example.demo.entity.RubberColors;
import com.example.demo.entity.ShoesSizes;
import com.example.demo.entity.Sizes;
import com.example.demo.entity.Types;
import com.example.demo.entity.Users;
import com.example.demo.repository.RacketTypesRepository;
import com.example.demo.repository.RubberColorsRepository;
import com.example.demo.repository.ShoesSizesRepository;
import com.example.demo.service.CategoriesService;
import com.example.demo.service.ColorsService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.SizesService;
import com.example.demo.service.TypesService;
import com.example.demo.service.UsersService;

@Controller
public class ProductsController {
	@Autowired									//クラスをインスタンス化
	private ProductsService productsService;    //ProductsService型の変数productsServiceを宣言
    
    @Autowired
    private UsersService usersService;
    
    @Autowired
    private CategoriesService categoriesService;
    
    @Autowired
    private TypesService typesService;
    
    @Autowired
    private ColorsService colorsService;
    
    @Autowired
    private SizesService sizesService;
    
    @Autowired
    private RacketTypesRepository racketTypesRepository;
    
    @Autowired
    private RubberColorsRepository rubberColorsRepository;
    
    @Autowired
    private ShoesSizesRepository shoesSizesRepository;
	
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
	
    @GetMapping("/products/racket")
    public String viewProductRackets(Model model, 
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 6;  // 1ページに表示する商品数
        Page<Products> productsPage = productsService.fetchRackets(page, pageSize);
        
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

        return "productRackets";
    }
	
    @GetMapping("/products/rubber")
    public String viewProductRubbers(Model model, 
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 6;  // 1ページに表示する商品数
        Page<Products> productsPage = productsService.fetchRubbers(page, pageSize);
        
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

        return "productRubbers";
    }
	
    @GetMapping("/products/shoes")
    public String viewProductShoes(Model model, 
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 6;  // 1ページに表示する商品数
        Page<Products> productsPage = productsService.fetchShoes(page, pageSize);
        
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

        return "productShoes";
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
        
        List<Types> types = typesService.findAll();
        List<Colors> colors = colorsService.findAll();
        List<Sizes> sizes = sizesService.findAll();
        
        model.addAttribute("types", types);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);
        
        if (user != null) {
            model.addAttribute("username", user.getUserName());
            model.addAttribute("loggedInUserId", user.getId());
        }

        return "productsAdd";
    }

    @PostMapping("/product/save")
    public String saveProduct(
        @ModelAttribute("product") Products product,
        @RequestParam(value = "selectedTypes", required = false) List<Long> selectedTypes, // ラケットのタイプが選択された場合
        @RequestParam(value = "selectedColors", required = false) List<Long> selectedColors, // ラバーの色が選択された場合
        @RequestParam(value = "selectedSizes", required = false) List<Long> selectedSizes // シューズのサイズが選択された場合
    ) {
        // 現在の認証されたユーザーを取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 認証されたユーザー名を取得
        Users loggedInUser = usersService.findByMailAddress(username);
        
        // 商品にユーザー情報を設定
        product.setUser(loggedInUser);
        
        // 商品情報を保存
        Products savedProduct = productsService.saveProduct(product);

        // ラケットの場合、racket_typesテーブルにデータを保存
        if (selectedTypes != null && product.getCategory().getId() == 1) {
            for (Long typeId : selectedTypes) {
                RacketTypes racketType = new RacketTypes();
                racketType.setProductId(savedProduct.getId()); // 保存された商品のID
                racketType.setTypeId(typeId); // 選択されたタイプのID
                racketTypesRepository.save(racketType); // データベースに保存
            }
        }

        // ラバーの場合、rubber_colorsテーブルにデータを保存
        if (selectedColors != null && product.getCategory().getId() == 2) {
            for (Long colorId : selectedColors) {
                RubberColors rubberColor = new RubberColors();
                rubberColor.setProductId(savedProduct.getId()); // 保存された商品のID
                rubberColor.setColorId(colorId); // 選択された色のID
                rubberColorsRepository.save(rubberColor); // データベースに保存
            }
        }

        // シューズの場合、shoes_sizesテーブルにデータを保存
        if (selectedSizes != null && product.getCategory().getId() == 3) {
            for (Long sizeId : selectedSizes) {
                ShoesSizes shoesSize = new ShoesSizes();
                shoesSize.setProductId(savedProduct.getId()); // 保存された商品のID
                shoesSize.setSizeId(sizeId); // 選択されたサイズのID
                shoesSizesRepository.save(shoesSize); // データベースに保存
            }
        }

        // 商品リストページにリダイレクト
        return "redirect:/products";
    }

}
