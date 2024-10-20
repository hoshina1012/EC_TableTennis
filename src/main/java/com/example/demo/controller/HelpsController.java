package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Helps;
import com.example.demo.entity.Users;
import com.example.demo.service.HelpsService;
import com.example.demo.service.UsersService;

import jakarta.validation.Valid;

@Controller
public class HelpsController {

    @Autowired
    private HelpsService helpsService;

    @Autowired
    private UsersService usersService;

    @GetMapping("/help")
    public String viewHelpForm(Model model) {
        model.addAttribute("help", new Helps());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users user = usersService.findByMailAddress(mailAddress);
        
        if(user != null) {
        	model.addAttribute("username", user.getUserName());
        }
        
        return "help";
    }

    @PostMapping("/help/save")
    public String saveHelp(@Valid @ModelAttribute("help") Helps help, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "help";
        }

        // ログイン中のユーザーを取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mailAddress = auth.getName();
        Users loggedInUser = usersService.findByMailAddress(mailAddress);

        // userIdをHelpsエンティティに設定
        if (loggedInUser != null) {
            help.setUserId(loggedInUser.getId());
        }

        // Helpsエンティティを保存
        helpsService.saveHelp(help);

        // 成功メッセージをフラッシュ属性に追加
        redirectAttributes.addFlashAttribute("successMessage", "お問い合わせを送信しました");

        return "redirect:/help";
    }
}
