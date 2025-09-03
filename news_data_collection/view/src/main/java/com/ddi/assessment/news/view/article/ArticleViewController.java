package com.ddi.assessment.news.view.article;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/article")
public class ArticleViewController {

    @GetMapping
    public String listPage() {
        return "article/list";
    }

}
