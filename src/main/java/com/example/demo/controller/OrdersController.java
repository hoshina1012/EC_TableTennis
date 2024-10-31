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
import com.example.demo.entity.Categories;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Payments;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.CartsService;
import com.example.demo.service.OrdersService;
import com.example.demo.service.PaymentsService;
import com.example.demo.service.ProductDetailService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

@Controller
public class OrdersController {
    @Autowired
    private CartsService cartsService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ProductDetailService productDetailService;
    
    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ProductsService productsService;

    @GetMapping("/order")
    public String checkout(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null) {
            List<Carts> cartItems = cartsService.findByUserId(user.getId());
            int totalAmount = 0;
            model.addAttribute("username", user.getUserName()); // user_name をモデルに追加

            for (Carts cart : cartItems) {
                Products product = productDetailService.fetchProductById(cart.getProductId());
                cart.setProductName(product.getName());
                cart.setProductPrice(product.getPrice());
                cart.setKindName(cartsService.getKindName(cart.getCategoryId(), cart.getKindId())); 
                totalAmount += cart.getProductPrice() * cart.getQuantity();
            }

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
        } else {
            model.addAttribute("cartItems", null);
        }

        return "order";
    }

    @PostMapping("/order/submit")
    public String submitOrder(
        @RequestParam("cardNumber") Long cardNumber,
        @RequestParam("cardKind") int cardKind,
        @RequestParam("expirationYear") int expirationYear,
        @RequestParam("expirationMonth") int expirationMonth,
        @RequestParam("cardHolder") String cardHolder,
        @RequestParam("securityCode") int securityCode,
        @RequestParam("lastName") String lastName,
        @RequestParam("firstName") String firstName,
        @RequestParam("zipCode1") int zipCode1,
        @RequestParam("zipCode2") int zipCode2,
        @RequestParam("address") String address,
        Model model
    ) {
        // サーバー側のバリデーション
        if (!validateCardInfo(cardNumber, cardKind, expirationYear, expirationMonth, cardHolder, securityCode, lastName, firstName, zipCode1, zipCode2, address)) {
            // バリデーションが失敗した場合、エラーメッセージをモデルに追加して/orderページに戻る
            model.addAttribute("errorMessage", "入力情報に誤りがあります。再度確認してください。");
            return "redirect:/order";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null) {
            Payments payment = new Payments();
            payment.setUserId(user.getId());
            payment.setCardNumber(cardNumber);
            payment.setCardKind(cardKind);
            payment.setExpirationYear(expirationYear);
            payment.setExpirationMonth(expirationMonth);
            payment.setCardHolder(cardHolder);
            payment.setSecurityCode(securityCode);
            payment.setFirstName(firstName);
            payment.setLastName(lastName);
            payment.setZipCode1(zipCode1);
            payment.setZipCode2(zipCode2);
            payment.setAddress(address);

            paymentsService.savePayment(payment);

            // カート内の各商品についてOrdersエンティティを作成し、保存
            List<Carts> cartItems = cartsService.findByUserId(user.getId());
            for (Carts cart : cartItems) {
                // 在庫を減らす
                productsService.reduceStock(cart.getProductId(), cart.getQuantity());

                Orders order = new Orders();
                order.setUserId(user.getId());

                // Productオブジェクトを取得してセット
                Products product = productsService.findProductById(cart.getProductId());
                order.setProduct(product);

                order.setQuantity(cart.getQuantity());
                order.setOrderStatus(0); // 注文ステータスを適切に設定
                order.setKindId(cart.getKindId());
                ordersService.saveOrder(order);

                // カートアイテムを削除
                cartsService.deleteCartById(cart.getId());
            }

            // トップページにリダイレクト
            return "redirect:/ordered";
        }

        // エラーの場合、注文ページに戻る
        return "redirect:/order";
    }

    private boolean validateCardInfo(Long cardNumber, int cardKind, int expirationYear, int expirationMonth, String cardHolder, int securityCode, String lastName, String firstName, int zipCode1, int zipCode2, String address) {
        // ここでサーバー側のバリデーションを実施
        if (cardNumber.toString().length() != 16 || cardKind < 1 || cardKind > 3 || expirationYear < 0 || expirationYear > 99 || expirationMonth < 1 || expirationMonth > 12 || !cardHolder.matches("^[A-Z\\s]+$") || String.valueOf(securityCode).length() != 3 || lastName.isEmpty() || firstName.isEmpty() || String.valueOf(zipCode1).length() != 3 || String.valueOf(zipCode2).length() != 4 || address.isEmpty()) {
            return false;
        }
        return true;
    }

	
	@GetMapping("/ordered")
	public String viewOrdered(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
		return "ordered";
	}
	
	@GetMapping("/orderHistory")
    public String viewOrderHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null) {
            List<Orders> orders = ordersService.findByUserId(user.getId());
            
            orders.forEach(order -> {
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
            
            model.addAttribute("orders", orders);
            model.addAttribute("username", user.getUserName());
        }

        return "orderHistory"; // orderHistory.html に対応
    }
	
	@GetMapping("/orderManagement")
	public String viewOrderManagement(Model model) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String mailAddress = auth.getName();
	    Users user = usersService.findByMailAddress(mailAddress);

	    if (user != null) {
	        // ユーザーが販売者の注文を取得
	        List<Orders> managedOrders = ordersService.findOrdersBySellerId(user.getId());
	        
	        managedOrders.forEach(order -> {
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
	        model.addAttribute("username", user.getUserName());
	    }

	    return "orderManagement"; // orderManagement.html に対応
	}
	
	@PostMapping("/orderManagement/updateStatus")
	public String updateOrderStatus(@RequestParam("orderId") Long orderId) {
	    Orders order = ordersService.findOrderById(orderId);
	    if (order != null && order.getOrderStatus() == 0) {
	        order.setOrderStatus(1); // ステータスを「発送済」に更新
	        ordersService.saveOrder(order);
	    }
	    return "redirect:/orderManagement";
	}


}
