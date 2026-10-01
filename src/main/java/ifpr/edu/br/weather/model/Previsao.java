package ifpr.edu.br.weather.model;

import java.time.LocalDateTime;

/**
 * Um item da previsão dos próximos dias retornada pela API do OpenWeatherMap.
 */
public class Previsao {

    private LocalDateTime dataHora;
    private double temperatura;
    private String descricao;
    private String icone;

    public Previsao(LocalDateTime dataHora, double temperatura, String descricao, String icone) {
        this.dataHora = dataHora;
        this.temperatura = temperatura;
        this.descricao = descricao;
        this.icone = icone;
    }

    public String getIconeUrl() {
        return "https://openweathermap.org/img/wn/" + icone + "@2x.png";
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getIcone() {
        return icone;
    }
}
