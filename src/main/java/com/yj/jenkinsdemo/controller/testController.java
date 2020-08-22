package com.yj.jenkinsdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class testController {
    @RequestMapping(value = {"", "/index"})
    public String index(Model model) {
        model.addAttribute("msg", "hello");
        return "index";
    }
}
