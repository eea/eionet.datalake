package eionet.datalake.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

/**
 * Test the file operations.
 */
public class ITFileOpsController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
    }

    /**
     * Since it is protected, it will redirect to login.
     */
    @Test
    public void testUploadForm() throws Exception {
        this.mockMvc.perform(get("/fileupload"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * This will succeed.
     */
    @Test
    public void authenticatedUploadForm() throws Exception {
        this.mockMvc.perform(get("/fileupload").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(view().name("fileupload"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * No authentication.
     */
    @Test
    public void failUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10"))
                .andExpect(status().isForbidden());
    }

    /**
     * This upload is expected to succeed.
     */
    @Test
    public void goodUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10").with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("fileupload"))
                .andExpect(flash().attributeCount(2));
    }

    /**
     * This upload is expected to succeed.
     */
    @Test
    public void goodAjaxUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("ajaxupload", "1")
                .param("fileTTL", "10")
                .with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/xml"));
    }
    /**
     * This upload attempt has no CSRF token.
     */
    @Test
    public void noCSRFUpload() throws Exception {
        mockMvc.perform(fileUpload("/fileupload")
                .file("file", "ABCDEF".getBytes("UTF-8"))
                .param("fileTTL", "10").with(user("admin").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Attempt to download a non-existent file.
     */
    @Test
    public void downloadNotFound() throws Exception {
        mockMvc.perform(get("/datasets/no-such-file/download"))
            .andExpect(status().isNotFound())
            .andExpect(view().name("filenotfound"));
    }

}
