package ifpr.edu.br.weather.model;

/**
 * Condições do tempo atuais de uma cidade, montadas a partir da resposta da
 * API do OpenWeatherMap. Não é uma entidade: os dados vêm da API a cada consulta.
 */
public class Clima {

    private String cidade;
    private String pais;
    private double temperatura;
    private double sensacaoTermica;
    private double temperaturaMinima;
    private double temperaturaMaxima;
    private int umidade;
    private double velocidadeVento;
    private String descricao;
    private String icone;
    private double latitude;
    private double longitude;

    /** Velocidade do vento convertida de m/s (unidade da API) para km/h. */
    public double getVentoKmh() {
        return velocidadeVento * 3.6;
    }

    public String getIconeUrl() {
        return "https://openweathermap.org/img/wn/" + icone + "@2x.png";
    }

    /**
     * Dica simples baseada nos dados recebidos da API.
     */
    public String getRecomendacao() {
        String desc = descricao == null ? "" : descricao.toLowerCase();
        if (desc.contains("chuva") || desc.contains("garoa") || desc.contains("trovoada")) {
            return "Leve um guarda-chuva!";
        }
        if (temperatura >= 30) {
            return "Está muito quente: beba bastante água e use protetor solar.";
        }
        if (temperatura <= 12) {
            return "Está frio: leve um bom agasalho.";
        }
        if (getVentoKmh() >= 40) {
            return "Ventos fortes: cuidado ao sair.";
        }
        return "Tempo tranquilo, aproveite o dia!";
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getSensacaoTermica() {
        return sensacaoTermica;
    }

    public void setSensacaoTermica(double sensacaoTermica) {
        this.sensacaoTermica = sensacaoTermica;
    }

    public double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public void setTemperaturaMinima(double temperaturaMinima) {
        this.temperaturaMinima = temperaturaMinima;
    }

    public double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public int getUmidade() {
        return umidade;
    }

    public void setUmidade(int umidade) {
        this.umidade = umidade;
    }

    public double getVelocidadeVento() {
        return velocidadeVento;
    }

    public void setVelocidadeVento(double velocidadeVento) {
        this.velocidadeVento = velocidadeVento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
