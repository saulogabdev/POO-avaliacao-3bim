package ifpr.edu.br.weather.api;

import ifpr.edu.br.weather.model.Clima;
import ifpr.edu.br.weather.model.Previsao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por consultar a API do OpenWeatherMap
 * (https://openweathermap.org/api) e transformar o JSON recebido
 * em objetos do sistema (Clima e Previsao).
 */
@Component
public class OpenWeatherApi {

    private final RestClient restClient;
    private final String chave;

    public OpenWeatherApi(@Value("${openweather.api.url:https://api.openweathermap.org/data/2.5}") String url,
                          @Value("${openweather.api.key:}") String chave) {
        this.chave = chave;
        this.restClient = RestClient.create(url);
    }

    public boolean isConfigurada() {
        return chave != null && !chave.isBlank();
    }

    /**
     * Busca o tempo atual de uma cidade.
     * Endpoint: GET /weather?q={cidade}&units=metric&lang=pt_br&appid={chave}
     */
    public Clima buscarClimaAtual(String cidade) {
        JsonNode json = restClient.get()
                .uri("/weather?q={q}&units=metric&lang=pt_br&appid={chave}", cidade, chave)
                .retrieve()
                .body(JsonNode.class);

        Clima clima = new Clima();
        clima.setCidade(json.path("name").asString());
        clima.setPais(json.path("sys").path("country").asString());
        clima.setTemperatura(json.path("main").path("temp").asDouble());
        clima.setSensacaoTermica(json.path("main").path("feels_like").asDouble());
        clima.setTemperaturaMinima(json.path("main").path("temp_min").asDouble());
        clima.setTemperaturaMaxima(json.path("main").path("temp_max").asDouble());
        clima.setUmidade(json.path("main").path("humidity").asInt());
        clima.setVelocidadeVento(json.path("wind").path("speed").asDouble());
        clima.setDescricao(json.path("weather").path(0).path("description").asString());
        clima.setIcone(json.path("weather").path(0).path("icon").asString());
        clima.setLatitude(json.path("coord").path("lat").asDouble());
        clima.setLongitude(json.path("coord").path("lon").asDouble());
        return clima;
    }

    /**
     * Busca a previsão dos próximos dias de uma cidade. A API devolve um item
     * a cada 3 horas; aqui guardamos apenas o horário do meio-dia de cada dia.
     * Endpoint: GET /forecast?q={cidade}&units=metric&lang=pt_br&appid={chave}
     */
    public List<Previsao> buscarPrevisao(String cidade) {
        JsonNode json = restClient.get()
                .uri("/forecast?q={q}&units=metric&lang=pt_br&appid={chave}", cidade, chave)
                .retrieve()
                .body(JsonNode.class);

        int fusoHorario = json.path("city").path("timezone").asInt();
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(fusoHorario);

        List<Previsao> previsoes = new ArrayList<>();
        for (JsonNode item : json.path("list")) {
            LocalDateTime dataHora = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(item.path("dt").asLong()), offset);

            if (dataHora.getHour() >= 11 && dataHora.getHour() <= 13) {
                previsoes.add(new Previsao(
                        dataHora,
                        item.path("main").path("temp").asDouble(),
                        item.path("weather").path(0).path("description").asString(),
                        item.path("weather").path(0).path("icon").asString()));
            }
        }
        return previsoes;
    }
}
