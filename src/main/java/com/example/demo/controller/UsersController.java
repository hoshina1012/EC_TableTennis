package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Categories;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.OrdersService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

@Controller
public class UsersController {
	@Autowired									//クラスをインスタンス化
	private ProductsService productsService;  
	@Autowired
	private UsersService usersService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
	@GetMapping("/users")
	public String viewUsers(Model model) {
		List<Users> users = usersService.fetchUsers();
		model.addAttribute("users", users);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }

		return "users";
	}
	
	@GetMapping("/signUp")
	public String viewSignUp(Model model) {
		List<Users> users = usersService.fetchUsers();
		model.addAttribute("users", users);
		model.addAttribute("user", new Users());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
        
		return "signUp";
	}

	@PostMapping("/signUp/save")
	public String saveSignUp(@ModelAttribute("user") Users user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
	    if (usersService.emailExists(user.getMailAddress())) {
	        result.addError(new FieldError("user", "mailAddress", "このメールアドレスは既に使われています"));
	    }

	    if (result.hasErrors()) {
	        return "signUp";
	    }

	    // 新規登録に成功した場合、メッセージを設定してリダイレクト
	    usersService.saveUser(user);
	    redirectAttributes.addFlashAttribute("successMessage", "会員登録に成功しました！まずはログインしましょう！");
	    return "redirect:/login";
	}
	
	@GetMapping("/signUp/seller")
	public String viewSignUpSeller(Model model) {
		List<Users> users = usersService.fetchUsers();
		model.addAttribute("users", users);
		model.addAttribute("user", new Users());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
        
		return "signUpSeller";
	}

	@PostMapping("/signUp/seller/save")
	public String saveSignUpSeller(@ModelAttribute("user") Users user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
	    if (usersService.emailExists(user.getMailAddress())) {
	        result.addError(new FieldError("user", "mailAddress", "このメールアドレスは既に使われています"));
	    }

	    if (result.hasErrors()) {
	        return "signUpSeller";
	    }
	    
	    user.setUserStatus(1);

	    // 新規登録に成功した場合、メッセージを設定してリダイレクト
	    usersService.saveUser(user);
	    redirectAttributes.addFlashAttribute("successMessage", "会員登録に成功しました！まずはログインしましょう！");
	    return "redirect:/login";
	}

    @GetMapping("/login")
    public String viewLogin(Model model) {
        model.addAttribute("user", new Users());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
        
        return "login";
    }

    @PostMapping("/login/save")
    public String loginUser(@ModelAttribute("user") Users user, BindingResult result, Model model) {
        Users existingUser = usersService.findByMailAddress(user.getMailAddress());

        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            result.addError(new FieldError("user", "mailAddress", "メールアドレスまたはパスワードに誤りがあります"));
        }

        if (result.hasErrors()) {
            return "login";
        }

        return "redirect:/users/" + existingUser.getId();
    }

    @GetMapping("/users/{userId}")
    public String viewUser(@PathVariable Long userId, 
            			   @RequestParam(defaultValue = "0") int page, 
            			   Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String mailAddress = auth.getName();
        Users loggedInUser = usersService.findByMailAddress(mailAddress);

        // ログイン中のユーザーとアクセスしようとしているユーザーのIDを比較
        if (loggedInUser.getId() != 1 && loggedInUser != null && !loggedInUser.getId().equals(userId)) {
            return "redirect:/users/" + loggedInUser.getId(); // ログイン中のユーザーのページにリダイレクト
        }

        Users user = usersService.fetchUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("username", loggedInUser.getUserName());
        model.addAttribute("loggedInUserId", user.getId());
        model.addAttribute("userStatus", user.getUserStatus());

        // ログイン中のユーザーIDと一致する商品のみを取得
        Page<Products> userProductsPage = productsService.fetchProductsByUserId(userId, page, 6);
        model.addAttribute("products", userProductsPage.getContent());
        model.addAttribute("totalPages", userProductsPage.getTotalPages());
        model.addAttribute("currentPage", page);

        // ユーザーの注文履歴を取得
        List<Orders> userOrders = ordersService.findByUserId(userId);
        model.addAttribute("orders", userOrders);

        // ログイン中のユーザーが販売した商品の注文データを取得
        List<Orders> managedOrders = ordersService.fetchOrders().stream()
                .filter(order -> order.getProduct().getUser().getId().equals(userId))  // 修正箇所
                .collect(Collectors.toList());
        model.addAttribute("managedOrders", managedOrders);
        
        List<Orders> userOrders2 = ordersService.findByUserId(userId);
        userOrders2.forEach(order -> {
            Long kindId = order.getKindId();
            String kindName = "N/A";
            Categories category = order.getProduct().getCategory();
            if ("ラケット".equals(category.getName())) {
                kindName = ordersService.getRacketTypeName(kindId);
            } else if ("ラバー".equals(category.getName())) {
                kindName = ordersService.getRubberColorName(kindId);
            } else if ("シューズ".equals(category.getName())) {
                kindName = ordersService.getShoeSizeName(kindId);
            }
            order.setKindName(kindName);  // 表示用のkindNameを追加
        });
        model.addAttribute("orders", userOrders);
        
        List<Orders> managedOrders2 = ordersService.fetchOrders().stream()
                .filter(order -> order.getProduct().getUser().getId().equals(userId))
                .collect(Collectors.toList());
        managedOrders2.forEach(order -> {
            Long kindId = order.getKindId();
            String kindName = "N/A";
            Categories category = order.getProduct().getCategory();

            if ("ラケット".equals(category.getName())) {
                kindName = ordersService.getRacketTypeName(kindId);
            } else if ("ラバー".equals(category.getName())) {
                kindName = ordersService.getRubberColorName(kindId);
            } else if ("シューズ".equals(category.getName())) {
                kindName = ordersService.getShoeSizeName(kindId);
            }
            order.setKindName(kindName);  // 表示用のkindNameを設定
        });
        model.addAttribute("managedOrders", managedOrders);

        return "userPage";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") Users user, BindingResult result, Model model) {
    	Users existingUser = usersService.findById(user.getId());

        existingUser.setUserName(user.getUserName());
        existingUser.setMailAddress(user.getMailAddress());

        usersService.saveUser(existingUser);

        // 更新後、ユーザー詳細ページにリダイレクト
        return "redirect:/users/" + user.getId();
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/"; // トップページのビュー名
    }
}
