package com.increff.pos.controller;


import com.increff.pos.model.InfoData;
import com.increff.pos.model.LoginForm;
import com.increff.pos.model.SignupForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@PropertySource(value="file:./admin.properties")
public class SignupController {

    @Value("${adminList}")
    private String adminList;

    @Autowired
    private UserService service;

    @Autowired
    private InfoData info;

    @ApiOperation(value = "Signs up a user")
    @RequestMapping(path = "/session/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView Signup(HttpServletRequest req, SignupForm form) throws ApiException {
        UserPojo p = service.get(form.getEmail());
        info.setMessage("");

        if (p != null) {
            info.setMessage("User already exist with given credentials please login instead");
            return new ModelAndView("redirect:/site/login");
        }

        UserPojo user = new UserPojo();


        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());


        String []admins = adminList.split(",");
        Boolean flag = false;

        for(String admin:admins)
        {
            if(admin.equals(form.getEmail()))
            {
                user.setRole("admin");
                flag = true;
                break;
            }

        }

        if(!flag)
        {
            user.setRole("operator");
        }



        service.add(user);

        // Create authentication object
        Authentication authentication = convert(user);
        // Create new session
        HttpSession session = req.getSession(true);
        // Attach Spring SecurityContext to this new session
        SecurityUtil.createContext(session);
        // Attach Authentication object to the Security Context
        SecurityUtil.setAuthentication(authentication);

        return new ModelAndView("redirect:/ui/home");



    }

    private static Authentication convert(UserPojo p) {
        // Create principal
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(p.getEmail());
        principal.setId(p.getId());

        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(p.getRole()));
        // you can add more roles if required

        // Create Authentication
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
                authorities);
        return token;
    }

}
