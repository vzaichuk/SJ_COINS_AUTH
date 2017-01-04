package com.softjourn.coin.auth.controller;


import com.softjourn.coin.auth.entity.Role;
import com.softjourn.coin.auth.entity.User;
import com.softjourn.coin.auth.service.AdminService;
import com.softjourn.coin.auth.utility.OAuthHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
// Creates embedded database
// Spring Boot can auto-configure embedded H2, HSQL and Derby databases
@AutoConfigureTestDatabase
@AutoConfigureRestDocs("target/generated-snippets")
@AutoConfigureMockMvc(secure = false)
public class AdminControllerTest {

    private final Role testRole = new Role("ROLE_TEST");
    private final User testUser = new User("ldap_test", "full_name"
            , "email@email", new HashSet<Role>() {{
        add(testRole);
    }});

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OAuthHelper authHelper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdminService adminService;

    @Before
    public void setUp() throws Exception {
        when(adminService.getAdmins()).thenReturn(new ArrayList<User>() {{
            add(testUser);
        }});
    }

    @Test
    public void getAll_WithoutRole_UnauthorizedRequest() throws Exception {
        mvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/v1/admin")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_WithRoleUser_Forbidden() throws Exception {
        RequestPostProcessor bearerToken = authHelper.withUser("test", "ROLE_USER");

        mvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/v1/admin")
                        .with(bearerToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_WithRoleSuperUser_AllUser() throws Exception {
        RequestPostProcessor bearerToken = authHelper.withUser("test", "ROLE_SUPER_ADMIN");

        mvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/v1/admin")
                        .with(bearerToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll_WithRoleUserManager_AllUser() throws Exception {

        RequestPostProcessor bearerToken = authHelper.withUser("test", "ROLE_USER_MANAGER");

        FieldDescriptor[] role = new FieldDescriptor[]{
                fieldWithPath("authority").description("Role name with prefix 'ROLE_'"),
                fieldWithPath("superRole").description("Bool value. Is this role super role?")
        };

        FieldDescriptor[] user = new FieldDescriptor[]{
                fieldWithPath("ldapId").description("vpupkin"),
                fieldWithPath("fullName").description("Vasuliy Pupkin"),
                fieldWithPath("email").description("vpupkin@softjoun.com"),
                fieldWithPath("authorities").description("ROLE_ADMIN")

        };

        mvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/v1/admin")
                        .with(bearerToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get_all_admins", preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("[]").description("An array of users"))
                                .andWithPrefix("[].", user)
                                .andWithPrefix("[].authorities.[].", role)));

    }
}

