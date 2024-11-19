package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.example.demo.entity.OrderItems;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Payments;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.CartsService;
import com.example.demo.service.OrderItemsService;
import com.example.demo.service.OrdersService;
import com.example.demo.service.PaymentsService;
import com.example.demo.service.ProductDetailService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

import jakarta.transaction.Transactional;

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

    @Autowired
    private OrderItemsService orderItemsService;

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

         // カート内の各商品について注文を作成
            List<Carts> cartItems = cartsService.findByUserId(user.getId());

            // 合計金額の計算
            int totalAmount = 0;
            for (Carts cart : cartItems) {
                Products product = productsService.findProductById(cart.getProductId());
                totalAmount += product.getPrice() * cart.getQuantity();  // 合計金額を計算
            }

            // 注文を作成し、保存
            Orders order = new Orders();
            order.setUserId(user.getId());
            order.setAmount(totalAmount);  // 合計金額を設定
            order.setOrderStatus(0);  // 注文ステータスを適切に設定
            ordersService.saveOrder(order);

            // order_items テーブルに注文アイテムを保存
            for (Carts cart : cartItems) {
                Products product = productsService.findProductById(cart.getProductId());

                OrderItems orderItem = new OrderItems();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setKindId(cart.getKindId());

                orderItemsService.saveOrderItem(orderItem);  // order_items テーブルに保存

                // 在庫を減らす
                productsService.reduceStock(cart.getProductId(), cart.getQuantity());

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
        	List<OrderItems> userOrderItems = orderItemsService.findByUserId(user.getId());
            userOrderItems.forEach(item -> {
                Long kindId = item.getKindId();
                String kindName = "N/A";
                Categories category = item.getProduct().getCategory();

                if ("ラケット".equals(category.getName())) {
                    kindName = ordersService.getRacketTypeName(kindId);
                } else if ("ラバー".equals(category.getName())) {
                    kindName = ordersService.getRubberColorName(kindId);
                } else if ("シューズ".equals(category.getName())) {
                    kindName = ordersService.getShoeSizeName(kindId);
                }
                item.setKindName(kindName); // 表示用のkindNameを設定
            });

            model.addAttribute("orders", userOrderItems);
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
	        // 自分の商品の注文履歴を取得（order_itemsテーブルのproduct_idとproductsテーブルのuser_idを参照）
	    	List<OrderItems> managedOrderItems = orderItemsService.fetchAllOrderItemsByIdDesc().stream()
                .filter(orderItem -> orderItem.getProduct().getUser().getId().equals(user.getId())) // 自分の商品に対する注文のみ
	            .collect(Collectors.toList());

	        managedOrderItems.forEach(orderItem -> {
	            Long kindId = orderItem.getKindId();
	            String kindName = "N/A";
	            Categories category = orderItem.getProduct().getCategory();

	            // 各カテゴリーに応じて種類を設定
	            if ("ラケット".equals(category.getName())) {
	                kindName = ordersService.getRacketTypeName(kindId);
	            } else if ("ラバー".equals(category.getName())) {
	                kindName = ordersService.getRubberColorName(kindId);
	            } else if ("シューズ".equals(category.getName())) {
	                kindName = ordersService.getShoeSizeName(kindId);
	            }
	            orderItem.setKindName(kindName);  // 表示用のkindNameを設定
	        });

	        model.addAttribute("managedOrders", managedOrderItems);
	        model.addAttribute("username", user.getUserName());
	    }

	    return "orderManagement"; // orderManagement.html に対応
	}

	@Transactional
	@PostMapping("/orderManagement/updateStatus")
	public String updateOrderStatus(@RequestParam("orderItemId") Long orderItemId) {
	    // orderItemIdを基に注文アイテムを取得
	    OrderItems orderItem = orderItemsService.findById(orderItemId);

	    // order_statusが0の場合に1に更新
	    if (orderItem.getOrderStatus() == 0) {
	        orderItem.setOrderStatus(1);
	        orderItemsService.saveOrderItem(orderItem);
	    }

	    // 受注管理画面にリダイレクト
	    return "redirect:/orderManagement";
	}
}
