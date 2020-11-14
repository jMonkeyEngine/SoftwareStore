package com.jayfella.website.controller.http;

import com.jayfella.website.license.LicenseConditions;
import com.jayfella.website.license.LicenseLimitations;
import com.jayfella.website.license.LicensePermissions;
import com.jayfella.website.license.OpenSourceLicense;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/legal")
public class LegalController {

    @GetMapping("/cookies")
    public String getCookiesPage() {
        return "/legal/cookies.html";
    }

    @GetMapping("/terms")
    public String getTermsOfService() {
        return "/legal/tos.html";
    }

    @GetMapping("/license/opensource")
    public String getOpenSourcLicenseChooser(Model model) {

        model.addAttribute("licenses", OpenSourceLicense.values());
        model.addAttribute("licenseTypes", OpenSourceLicense.values());
        model.addAttribute("licensePermissions", LicensePermissions.values());
        model.addAttribute("licenseConditions", LicenseConditions.values());
        model.addAttribute("licenseLimitations", LicenseLimitations.values());

        // return "/choose-license.html";

        return "/legal/license/os-license-chooser.html";
    }

}
