package exception_handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExHandlerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSuccess() throws Exception {
        mockMvc.perform(get("/test/test").param("signal", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testResourceNotFound() throws Exception {
        mockMvc.perform(get("/test/test").param("signal", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1002));
    }

    @Test
    void testRequestValidationFailed() throws Exception {
        mockMvc.perform(get("/test/test").param("signal", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003));
    }

    @Test
    void testIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/test").param("signal", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1003));
    }
}
