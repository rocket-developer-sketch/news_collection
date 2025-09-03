package com.ddi.assessment.news.view.schedule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/view/schedule")
public class ScheduleViewController {

    @GetMapping
    public String listPage() {
        return "schedule/list";
    }

    @GetMapping("/new")
    public String newPage(Model model) {
        return "schedule/setting";
    }

}
