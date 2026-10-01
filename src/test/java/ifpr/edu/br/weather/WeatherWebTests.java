package ifpr.edu.br.weather;

import com.sun.net.httpserver.HttpServer;
import ifpr.edu.br.weather.model.Cidade;
import ifpr.edu.br.weather.repository.CidadeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa o CRUD e as telas de clima. A API do OpenWeatherMap é simulada por um
 * servidor HTTP local que devolve JSONs de exemplo.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WeatherWebTests {

    private static final String CLIMA_JSON = """
            {"coord":{"lon":-49.27,"lat":-25.43},
             "weather":[{"description":"chuva leve","icon":"10d"}],
             "main":{"temp":17.4,"feels_like":17.1,"temp_min":16.0,"temp_max":19.2,"humidity":82},
             "wind":{"speed":5.0},"sys":{"country":"BR"},"name":"Curitiba"}
            """;

    private static final String PREVISAO_JSON = """
            {"city":{"timezone":-10800},
             "list":[
               {"dt":1759330800,"main":{"temp":21.5},"weather":[{"description":"nublado","icon":"04d"}]},
               {"dt":1759341600,"main":{"temp":19.0},"weather":[{"description":"céu limpo","icon":"01d"}]}
             ]}
            """;

    private static final HttpServer servidor = criarServidor();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CidadeRepository repository;

    private static HttpServer criarServidor() {
        try {
            HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            s.createContext("/", troca -> {
                String query = troca.getRequestURI().getQuery();
                String path = troca.getRequestURI().getPath();
                int codigo = 200;
                String corpo;
                if (query.contains("q=Inexistente")) {
                    codigo = 404;
                    corpo = "{\"cod\":\"404\",\"message\":\"city not found\"}";
                } else {
                    corpo = path.endsWith("/forecast") ? PREVISAO_JSON : CLIMA_JSON;
                }
                byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
                troca.getResponseHeaders().add("Content-Type", "application/json");
                troca.sendResponseHeaders(codigo, bytes.length);
                troca.getResponseBody().write(bytes);
                troca.close();
            });
            s.start();
            return s;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("openweather.api.url", () -> "http://127.0.0.1:" + servidor.getAddress().getPort());
        registry.add("openweather.api.key", () -> "chave-teste");
    }

    @AfterAll
    static void pararServidor() {
        servidor.stop(0);
    }

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    @Test
    void crudCompleto() throws Exception {
        mockMvc.perform(post("/cidades/salvar")
                        .param("nome", "Curitiba").param("pais", "br").param("apelido", "Casa"))
                .andExpect(redirectedUrl("/cidades"));

        Cidade salva = repository.findAll().getFirst();
        assertThat(salva.getPais()).isEqualTo("BR");
        assertThat(salva.getDataCadastro()).isNotNull();

        mockMvc.perform(get("/cidades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Curitiba")));

        mockMvc.perform(get("/cidades/" + salva.getId() + "/editar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar cidade")));

        mockMvc.perform(post("/cidades/salvar")
                        .param("id", salva.getId().toString())
                        .param("nome", "Curitiba").param("pais", "BR").param("apelido", "Trabalho"))
                .andExpect(redirectedUrl("/cidades"));
        Cidade editada = repository.findById(salva.getId()).orElseThrow();
        assertThat(editada.getApelido()).isEqualTo("Trabalho");
        assertThat(editada.getDataCadastro()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);

        mockMvc.perform(post("/cidades/" + salva.getId() + "/excluir"))
                .andExpect(redirectedUrl("/cidades"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void nomeObrigatorio() throws Exception {
        mockMvc.perform(post("/cidades/salvar").param("nome", " "))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("obrigatório")));
        assertThat(repository.count()).isZero();
    }

    @Test
    void buscaClimaMostraDadosDaApi() throws Exception {
        mockMvc.perform(get("/clima").param("q", "Curitiba"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Curitiba, BR")))
                .andExpect(content().string(containsString("chuva leve")))
                .andExpect(content().string(containsString("17 °C")))
                .andExpect(content().string(containsString("82%")))
                .andExpect(content().string(containsString("18 km/h")))
                .andExpect(content().string(containsString("guarda-chuva")))
                .andExpect(content().string(containsString("nublado")))
                .andExpect(content().string(containsString("Salvar esta cidade")));
    }

    @Test
    void buscaCidadeInexistente() throws Exception {
        mockMvc.perform(get("/clima").param("q", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("não encontrada")));
    }

    @Test
    void climaDeCidadeCadastrada() throws Exception {
        Cidade c = repository.save(new Cidade("Curitiba", "BR", "Casa", "Onde eu moro"));
        mockMvc.perform(get("/cidades/" + c.getId() + "/clima"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Clima em Casa")))
                .andExpect(content().string(containsString("Onde eu moro")))
                .andExpect(content().string(containsString("chuva leve")));
    }
}
