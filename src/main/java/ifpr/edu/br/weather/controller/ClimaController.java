package ifpr.edu.br.weather.controller;

import ifpr.edu.br.weather.api.OpenWeatherApi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Controller
public class ClimaController {

    private final OpenWeatherApi openWeatherApi;

    public ClimaController(OpenWeatherApi openWeatherApi) {
        this.openWeatherApi = openWeatherApi;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/clima";
    }

    // Busca o clima de qualquer cidade na API, sem precisar cadastrar
    @GetMapping("/clima")
    public String buscar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("q", q);
        if (q == null || q.isBlank()) {
            return "clima/busca";
        }
        if (!openWeatherApi.isConfigurada()) {
            model.addAttribute("erro", "Chave da API não configurada (OPENWEATHER_API_KEY).");
            return "clima/busca";
        }
        try {
            model.addAttribute("clima", openWeatherApi.buscarClimaAtual(q.trim()));
            model.addAttribute("previsoes", openWeatherApi.buscarPrevisao(q.trim()));
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("erro", "Cidade \"" + q + "\" não encontrada.");
        } catch (HttpClientErrorException.Unauthorized e) {
            model.addAttribute("erro", "Chave da API inválida.");
        } catch (RestClientException e) {
            model.addAttribute("erro", "Não foi possível consultar a API agora. Tente novamente.");
        }
        return "clima/busca";
    }
}
