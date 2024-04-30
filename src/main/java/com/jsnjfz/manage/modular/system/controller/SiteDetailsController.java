package com.jsnjfz.manage.modular.system.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import com.jsnjfz.manage.core.common.node.MenuNode;
import com.jsnjfz.manage.modular.system.model.Category;
import com.jsnjfz.manage.modular.system.model.Site;
import com.jsnjfz.manage.modular.system.service.impl.CategoryServiceImpl;
import com.jsnjfz.manage.modular.system.service.impl.SiteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

/**
 * @Author kevin不会写代码
 * @Date 2024/04/11 14:29
 * 网站跳转控制器
 */
@Controller
@RequestMapping("/siteDetails")
public class SiteDetailsController extends BaseController {

    private static String PREFIX = "/siteDetails/";

    @Autowired
    private SiteServiceImpl siteService;

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 跳转到置顶详情页面
     */
    @GetMapping("/{id}")
    public String siteUpdate(@PathVariable Integer id, Model model) {
        List<MenuNode> menus = categoryService.getCatogryNode(new HashMap<>());
        List<MenuNode> titles = MenuNode.buildTitle(menus);
        List<Category> categorySiteList = categoryService.getCatogrySite(null);
        model.addAttribute("categorySiteList", categorySiteList);
        model.addAttribute("titles", titles);
        Site site = siteService.get(id);
        model.addAttribute(site);
        String url = PREFIX + id + ".html";
        return url;
    }
}
