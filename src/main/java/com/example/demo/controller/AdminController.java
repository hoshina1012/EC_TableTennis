package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Helps;
import com.example.demo.entity.OrderItems;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Payments;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.service.HelpsService;
import com.example.demo.service.OrderItemsService;
import com.example.demo.service.OrdersService;
import com.example.demo.service.PaymentsService;
import com.example.demo.service.ProductsService;
import com.example.demo.service.UsersService;

@Controller
public class AdminController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private HelpsService helpsService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderItemsService orderItemsService;

    @Autowired
    private ProductsService productsService;
    
    @Autowired
    private PaymentsService paymentsService;


    @GetMapping("/admin")
    public String viewAdminPage(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        // ユーザーが存在し、IDが1であれば管理画面を表示
        if (user != null && (user.getId() == 1 || user.getUserAuthority() == 1)) {
            model.addAttribute("username", user.getUserName());

            return "admin"; 
        }

        return "redirect:/"; 
    }
    
    @GetMapping("/admin/user")
    public String viewAdminUserPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null && (user.getId() == 1 || user.getUserAuthority() == 1)) {
            model.addAttribute("username", user.getUserName());
            
            int pageSize = 5; // 1ページあたりのユーザー数
            Page<Users> usersPage = usersService.fetch0Users2(page, pageSize);
            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());

            // すべてのユーザーを取得してモデルに追加
            List<Users> usersList = usersService.fetch0Users();
            model.addAttribute("usersList", usersList);
            
            List<Integer> productCounts = usersList.stream()
                    .map(u -> u.getProducts().size())
                    .collect(Collectors.toList());
            model.addAttribute("productCounts", productCounts);
            
            Map<Long, Long> orderCounts = usersList.stream()
                    .collect(Collectors.toMap(
                            Users::getId,
                            u -> ordersService.countOrdersByUserId(u.getId())
                    ));
            model.addAttribute("orderCounts", orderCounts);
            
            Map<Long, Long> helpCounts = usersList.stream()
                    .collect(Collectors.toMap(
                    		Users::getId,
                    		u -> helpsService.countHelpsByUserId(u.getId())
                    ));
            model.addAttribute("helpCounts", helpCounts);

            return "adminUser"; // ユーザー一覧ページのビュー名
        }

        return "redirect:/";
    }
    
    @GetMapping("/admin/seller")
    public String viewAdminSellerPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null && (user.getId() == 1 || user.getUserAuthority() == 1)) {
            model.addAttribute("username", user.getUserName());
            
            int pageSize = 5; // 1ページあたりのユーザー数
            Page<Users> usersPage = usersService.fetch1Users2(page, pageSize);
            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());

            // すべてのユーザーを取得してモデルに追加
            List<Users> usersList = usersService.fetch1Users();
            model.addAttribute("usersList", usersList);
            
            List<Integer> productCounts = usersList.stream()
                    .map(u -> u.getProducts().size())
                    .collect(Collectors.toList());
            model.addAttribute("productCounts", productCounts);
            
            Map<Long, Long> receivedOrderCounts = usersList.stream()
                    .collect(Collectors.toMap(
                            Users::getId,
                            u -> orderItemsService.countOrdersByProductOwnerId(u.getId())
                    ));
            model.addAttribute("receivedOrderCounts", receivedOrderCounts);
            
            Map<Long, Long> helpCounts = usersList.stream()
                    .collect(Collectors.toMap(
                    		Users::getId,
                    		u -> helpsService.countHelpsByUserId(u.getId())
                    ));
            model.addAttribute("helpCounts", helpCounts);

            return "adminSeller"; // ユーザー一覧ページのビュー名
        }

        return "redirect:/";
    }
    
    @PostMapping("/admin/toggleAuthority")
    public ResponseEntity<String> toggleAuthority(@RequestParam Long userId, @RequestParam int newAuthority) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users currentUser = usersService.findByMailAddress(mailAddress);

        // 管理者チェック
        if (currentUser != null && currentUser.getId() == 1) {
            Users user = usersService.findById(userId);
            if (user != null) {
                user.setUserAuthority(newAuthority);
                usersService.save(user); // ユーザー情報を保存
                return ResponseEntity.ok("User authority updated");
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }

    @GetMapping("/admin/product")
    public String viewAdminProductPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        model.addAttribute("username", user.getUserName());

        int pageSize = 5; // 1ページあたりの商品の数
        Page<Products> productsPage = productsService.fetchProducts2(page, pageSize);
        
        Map<Long, Long> totalSalesMap = productsPage.getContent().stream()
                .collect(Collectors.toMap(
                    Products::getId,
                    product -> ordersService.sumQuantityByProductId(product.getId())
                ));
        
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("totalSalesMap", totalSalesMap);

        return "adminProduct"; // 商品一覧ページのビュー名
    }
    
    @GetMapping("/admin/order")
    public String viewAdminOrdersPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        if (user != null && (user.getId() == 1 || user.getUserAuthority() == 1)) {
            model.addAttribute("username", user.getUserName());

            int pageSize = 5; // 1ページあたりの注文数
            Page<Orders> ordersPage = ordersService.fetchOrders2(page, pageSize);

            model.addAttribute("ordersPage", ordersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", ordersPage.getTotalPages());
            
            Map<Long, String> userNameMap = ordersPage.getContent().stream()
                    .collect(Collectors.toMap(
                        Orders::getId,
                        order -> order.getUser().getUserName()
                    ));
            model.addAttribute("userNameMap", userNameMap);

            return "adminOrder"; // Thymeleaf テンプレートの名前
        }

        return "redirect:/"; // 管理者でない場合はトップページにリダイレクト
    }

    @GetMapping("/admin/help")
    public String viewAdminHelpPage(Model model) {
        // 現在の認証情報を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);

        // ユーザーが存在し、IDが1であれば管理画面を表示
        if (user != null && (user.getId() == 1 || user.getUserAuthority() == 1)) {
            model.addAttribute("username", user.getUserName());

            // すべてのお問い合わせデータを取得してモデルに追加
            List<Helps> helpsList = helpsService.findAllHelpsByIdAsc();
            model.addAttribute("helpsList", helpsList);

            return "adminHelp"; // 管理画面のビュー名
        }

        // 管理者でない場合、トップページにリダイレクト
        return "redirect:/";
    }
    
    @PostMapping("/admin/updateHelpStatus")
    public ResponseEntity<String> updateHelpStatus(@RequestParam Long helpId, @RequestParam int newStatus) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users currentUser = usersService.findByMailAddress(mailAddress);

        // 管理者チェック
        if (currentUser != null && (currentUser.getId() == 1 || currentUser.getUserAuthority() == 1)) {
            Helps help = helpsService.findById(helpId);
            if (help != null) {
                help.setStatus(newStatus);
                helpsService.saveHelp(help); // ステータスを保存
                return ResponseEntity.ok("Help status updated");
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }
    
    @GetMapping("/admin/order_detail/{orderId}")
    public String viewOrderDetailPage(@PathVariable Long orderId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users currentUser = usersService.findByMailAddress(mailAddress);

        if (currentUser != null && currentUser.getId() == 1) {
            model.addAttribute("username", currentUser.getUserName());

            Orders order = ordersService.findOrderById(orderId);
            if (order != null) {
                // 注文情報
                model.addAttribute("order", order);

                // 関連する order_items を取得
                List<OrderItems> orderItems = orderItemsService.findByOrderId(orderId);
                model.addAttribute("orderItems", orderItems);

                // 種類名のマップを取得
                Map<Long, String> typeNames = orderItemsService.getTypeNamesByOrderItems(orderItems);
                model.addAttribute("typeNames", typeNames);
                
                // 支払情報
                Optional<Payments> paymentOptional = paymentsService.findById(orderId);
                if (paymentOptional.isPresent()) {
                    Payments payment = paymentOptional.get();

                    // カード番号の最後の4桁のみ取得
                    String cardNumberStr = String.valueOf(payment.getCardNumber()); // LongをStringに変換
                    String maskedCardNumber = "**** **** **** " + cardNumberStr.substring(cardNumberStr.length() - 4);
                    model.addAttribute("maskedCardNumber", maskedCardNumber);

                    // カード名義人
                    model.addAttribute("cardHolder", payment.getCardHolder());

                    // 支払日
                    LocalDate paymentDate = payment.getCreatedAt().toLocalDateTime().toLocalDate(); // Timestamp → LocalDateTime → LocalDate
                    model.addAttribute("paymentDate", paymentDate);
                }

                return "orderDetail"; // 注文詳細ページのビュー名
            } else {
                return "redirect:/admin/order"; // 注文が存在しない場合、注文一覧ページにリダイレクト
            }
        }

        return "redirect:/"; // 管理者でない場合、トップページにリダイレクト
    }

}
