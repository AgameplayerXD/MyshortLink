package com.xwj.shortlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 短链接不存在跳转控制器
 */
@Controller
public class ShortLinkNotFountController {
    /**
     * 短链接不存在跳转页面
     */
    @RequestMapping("/page/notfound")
    public String notfound() {
        return "notfound";
    }
}
