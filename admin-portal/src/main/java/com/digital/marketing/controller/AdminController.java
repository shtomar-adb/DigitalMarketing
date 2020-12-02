package com.digital.marketing.controller;

import com.digital.marketing.auth.model.User;
import com.digital.marketing.auth.service.UserService;
import com.digital.marketing.entity.Campaign;
import com.digital.marketing.entity.Segment;
import com.digital.marketing.model.Run;
import com.digital.marketing.repository.CampaignRepository;
import com.digital.marketing.repository.SegmentRepository;
import com.digital.marketing.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private SegmentRepository segmentRepository;

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("user/signup");

        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
    public ModelAndView createUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = adminService.findUserByEmail(user.getEmail());

        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if (bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            adminService.saveUser(user);
            model.addObject("msg", "User has been registered successfully!");
            model.addObject("user", new User());
            model.setViewName("user/signup");
        }

        return model;
    }

    @RequestMapping(value = {"/home/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/home");
        return model;
    }

    @RequestMapping(value = {"/home/users"}, method = RequestMethod.GET)
    public ModelAndView users() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        List<com.digital.marketing.entity.User> users = userRepository.getAllUsers();
        model.addObject("users", users);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/users");
        return model;
    }

    @RequestMapping(value = {"/home/campaigns"}, method = RequestMethod.GET)
    public ModelAndView campaigns() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/campaigns");
        return model;
    }

    @RequestMapping(value = {"/campaigns/run-campaigns"}, method = RequestMethod.POST)
    public ModelAndView runCampaigns(HttpServletRequest httpServletRequest, @Valid Run run, BindingResult bindingResult) throws Exception {
        String campaignId = httpServletRequest.getParameter("campaign");
        String segmentId = httpServletRequest.getParameter("segment");

        var values = new HashMap<String, String>() {{
            put("campaign_id", campaignId);
            put("segment_id", segmentId);
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://avnefi4sek.execute-api.us-west-2.amazonaws.com/Testing/posttest"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        String msg;

        if (response.statusCode() == 200) {
            msg = "Campaign initiated successfully!";
        } else {
            msg = "An error occurred! Please contact admin!";
        }

        model.addObject("msg", msg);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("campaigns/run-campaigns");
        return model;
    }

    @RequestMapping(value = {"/home/run-campaigns"}, method = RequestMethod.GET)
    public ModelAndView selectCampaigns() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        List<Campaign> campaigns = campaignRepository.getAllCampaigns().stream()
                .sorted(Comparator.comparing(Campaign::getId).reversed())
                .collect(Collectors.toList());
        List<Segment> segments = segmentRepository.getAllSegments().stream()
                .sorted(Comparator.comparing(Segment::getId).reversed())
                .collect(Collectors.toList());

        model.addObject("run", new Run());
        model.addObject("segments", segments);
        model.addObject("campaigns", campaigns);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("campaigns/run-campaigns");
        return model;
    }

    @RequestMapping(value = {"/campaigns"}, method = RequestMethod.POST)
    public String createCampaign(@Valid Campaign campaign, BindingResult bindingResult) {
        List<Campaign> campaigns = campaignRepository.getAllCampaigns();
        Campaign c = campaigns.stream().max(Comparator.comparing(Campaign::getId))
                .orElseThrow(NoSuchElementException::new);
        campaign.setId(c.getId() + 1);
        campaignRepository.save(campaign);
        return "redirect:/campaigns";
    }

    @RequestMapping(value = {"/campaigns"}, method = RequestMethod.GET)
    public ModelAndView listAllCampaigns() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        List<Campaign> campaigns = campaignRepository.getAllCampaigns().stream()
                .sorted(Comparator.comparing(Campaign::getId).reversed())
                .collect(Collectors.toList());

        model.addObject("campaigns", campaigns);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("campaigns/view-campaigns");
        return model;
    }

    @RequestMapping(value = {"/campaigns/create"}, method = RequestMethod.GET)
    public ModelAndView createCampaign() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("campaigns/create-campaign");
        return model;
    }

    @RequestMapping(value = {"/segments"}, method = RequestMethod.GET)
    public ModelAndView listAllSegments() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        List<Segment> segments = segmentRepository.getAllSegments().stream()
                .sorted(Comparator.comparing(Segment::getId).reversed())
                .collect(Collectors.toList());

        model.addObject("segments", segments);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("segments/view-segments");
        return model;
    }

    private ArrayList<String> getUpdatedDeviceTokens(String id) {
        List<com.digital.marketing.entity.User> users = userRepository.getAllUsers();
        List<com.digital.marketing.entity.User> filteredUsers = null;
        if (Integer.valueOf(id) == 1) {
            filteredUsers = users.stream().filter(user -> Integer.valueOf(user.getAge()) > 18).collect(Collectors.toList());
        } else if (Integer.valueOf(id) == 2) {
            filteredUsers = users.stream().filter(user -> user.getGender().equals("Female")).collect(Collectors.toList());
        } else if (Integer.valueOf(id) == 3) {
            filteredUsers = users.stream().filter(user -> StringUtils.substringBetween(user.getDob(), "/", "/").equals("12")).collect(Collectors.toList());
        } else if (Integer.valueOf(id) == 4) {
            filteredUsers = users.stream().filter(user -> StringUtils.substringBetween(user.getDob(), "/", "/").equals("01")).collect(Collectors.toList());
        } else if (Integer.valueOf(id) == 5) {
            filteredUsers = users.stream().filter(user -> user.getGender().equals("Male")).collect(Collectors.toList());
        } else if (Integer.valueOf(id) == 6) {
            filteredUsers = users.stream().filter(user -> Integer.valueOf(user.getAge()) >= 13 && (Integer.valueOf(user.getAge()) <= 19)).collect(Collectors.toList());
        }
        ArrayList<String> deviceTokens = new ArrayList<>();
        for (com.digital.marketing.entity.User user : filteredUsers) {
            deviceTokens.add(user.getToken());
        }

        return deviceTokens;
    }

    @RequestMapping(value = {"/segments/update/{id}"}, method = RequestMethod.GET)
    public ModelAndView updateSegments(@PathVariable String id) {
        ArrayList<String> updatedDeviceTokens = getUpdatedDeviceTokens(id);
        Segment segment = segmentRepository.getSegmentById(Integer.valueOf(id));
        segment.setDevices(updatedDeviceTokens);
        segmentRepository.save(segment);
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        List<Segment> segments = segmentRepository.getAllSegments().stream()
                .sorted(Comparator.comparing(Segment::getId).reversed())
                .collect(Collectors.toList());

        model.addObject("msg", "Segment Updated Successfully!");
        model.addObject("segments", segments);
        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("segments/view-segments");
        return model;
    }

    @RequestMapping(value = {"/home/segments"}, method = RequestMethod.GET)
    public ModelAndView segments() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = adminService.findUserByEmail(auth.getName());

        model.addObject("userName", user.getFirstname() + " " + user.getLastname());
        model.setViewName("home/segments");
        return model;
    }

    @RequestMapping(value = {"/access_denied"}, method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }

}
