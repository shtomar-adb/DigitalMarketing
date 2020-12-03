package com.digital.marketing;

import com.digital.marketing.auth.service.UserService;
import com.digital.marketing.controller.AdminController;
import com.digital.marketing.repository.CampaignRepository;
import com.digital.marketing.repository.SegmentRepository;
import com.digital.marketing.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AdminController.class)
@WithMockUser(username="admin",roles={"USER","ADMIN"})
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService adminService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CampaignRepository campaignRepository;

    @MockBean
    private SegmentRepository segmentRepository;

    @MockBean
    private DataSource dataSource;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/login");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void testSignup() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/signup");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}
