package com.increff.pos.dto;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.model.editUserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDto {
    @Autowired
    private UserService service;



    @Transactional
    public void add(UserForm form) throws ApiException {
        if(form.getEmail().equals(""))
        {
            throw new ApiException("Email cannot be empty");
        }

        if(form.getPassword().equals(""))
        {
            throw new ApiException("Password cannot be empty");
        }

        if(!EmailValidator.getInstance().isValid(form.getEmail()))
        {
            throw new ApiException("Invalid Email Address");
        }

        if(form.getRole().equals("") || (!form.getRole().equals("supervisor") && !form.getRole().equals("operator")))
        {
            throw new ApiException("Invalid Role");
        }


        UserPojo p = convert(form);

        normalize(p);
        service.add(p);

    }

    @Transactional(rollbackOn = ApiException.class)
    public UserPojo get(String email) throws ApiException {
        return service.get(email);
    }

    public UserData getUserById(int id){
        return convert(service.getById(id));
    }

    @Transactional
    public List<UserData> getAll() {

        List<UserPojo> list = service.getAll();
        List<UserData> list2 = new ArrayList<UserData>();
        for (UserPojo p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    @Transactional
    public void delete(int id) {
        service.delete(id);
    }

    @Transactional
    public void update(int id,editUserForm form) throws ApiException {
        service.update(id,form.getEmail(),form.getRole());
    }

    protected static void normalize(UserPojo p) {
        p.setEmail(p.getEmail().toLowerCase().trim());
        p.setRole(p.getRole().toLowerCase().trim());
    }

    private static UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setRole(p.getRole());
        d.setId(p.getId());
        return d;
    }

    private static UserPojo convert(UserForm f) {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setRole(f.getRole());
        p.setPassword(f.getPassword());
        return p;
    }
}
