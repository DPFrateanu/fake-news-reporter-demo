package com.automatica.fakenews.controller;

import com.automatica.fakenews.dto.ReportForm;
import com.automatica.fakenews.model.FakeNewsReport;
import com.automatica.fakenews.service.FakeNewsReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private FakeNewsReportService reportService;

    @GetMapping("/")
    public String home(Model model) {
        List<FakeNewsReport> approvedReports = reportService.getApprovedReports();
        List<FakeNewsReport> rejectedReports = reportService.getRejectedReports();
        model.addAttribute("approvedReports", approvedReports);
        model.addAttribute("rejectedReports", rejectedReports);
        return "index";
    }

    @GetMapping("/reports/{id}")
    public String viewReport(@PathVariable("id") Long id, Model model) {
        FakeNewsReport report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "report-details";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<FakeNewsReport> approvedReports = reportService.getApprovedReports();
        List<FakeNewsReport> rejectedReports = reportService.getRejectedReports();
        model.addAttribute("approvedReports", approvedReports);
        model.addAttribute("rejectedReports", rejectedReports);
        return "reports";
    }

    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("reportForm", new ReportForm());
        return "report-form";
    }

    @PostMapping("/report")
    public String submitReport(@Valid @ModelAttribute("reportForm") ReportForm reportForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "report-form";
        }

        FakeNewsReport report = new FakeNewsReport();
        report.setNewsSource(reportForm.getNewsSource());
        report.setUrl(reportForm.getUrl());
        report.setCategory(reportForm.getCategory());
        report.setDescription(reportForm.getDescription());

        reportService.saveReport(report);
        redirectAttributes.addFlashAttribute("successMessage", 
            "Thank you! Your report has been submitted and is pending approval.");
        
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
