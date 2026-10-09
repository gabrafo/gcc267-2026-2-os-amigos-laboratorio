package br.ufla.gcc267.laboratorio.status;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void healthRetornaStatusUp() {
        assertThat(mvc.get().uri("/api/health"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.status").isEqualTo("UP");
    }
}
