package com.computershop.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EndpointHealthCheckTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public endpoints should respond without error (2xx or 3xx)")
    void publicEndpointsShouldBeReachable() throws Exception {
        String[] publicPaths = {
                "/",
                "/products",
                "/categories",
                "/about",
                "/login",
                "/register"
        };

        for (String path : publicPaths) {
            mockMvc.perform(get(path))
                    .andExpect(result -> {
                        int code = result.getResponse().getStatus();
                        if (code >= 400) {
                            throw new AssertionError("Expected <2xx/3xx> for " + path + " but was <" + code + ">");
                        }
                    });
        }
    }

    @Test
    @DisplayName("Protected user/admin endpoints should redirect to login when not authenticated")
    void protectedEndpointsShouldRedirectToLoginWhenAnonymous() throws Exception {
        String[] protectedPaths = {
                "/profile",
                "/orders",
                "/user/profile",
                "/user/orders",
                "/admin",
                "/admin/dashboard",
                "/admin/categories",
                "/admin/orders"
        };

        for (String path : protectedPaths) {
            mockMvc.perform(get(path))
                    .andExpect(status().is3xxRedirection());
        }
    }
}

