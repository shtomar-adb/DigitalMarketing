package com.digital.marketing;

import com.digital.marketing.controller.AdminController;
import com.digital.marketing.controller.UserController;
import com.digital.marketing.entity.User;
import com.digital.marketing.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@WithMockUser
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DataSource dataSource;

    @Test
    public void testGetUserById() throws Exception {
        when(userRepository.getUserById(anyString())).thenReturn(new User());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/13");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userRepository.getAllUsers()).thenReturn(Arrays.asList(new User()));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}
