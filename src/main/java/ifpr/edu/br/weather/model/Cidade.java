package ifpr.edu.br.weather.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "cidade")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 2)
    private String pais;

    @Column(length = 50)
    private String apelido;

    @Column(length = 500)
    private String observacao;

    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    public Cidade() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Cidade(String nome, String pais, String apelido, String observacao) {
        this.nome = nome;
        this.pais = pais;
        this.apelido = apelido;
        this.observacao = observacao;
        this.dataCadastro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    /**
     * Texto usado para consultar a API do OpenWeatherMap (ex.: "Curitiba,BR").
     */
    public String getConsulta() {
        if (pais == null || pais.isBlank()) {
            return nome;
        }
        return nome + "," + pais;
    }

    /**
     * Nome mostrado na tela: o apelido, se existir, senão o nome da cidade.
     */
    public String getNomeExibicao() {
        if (apelido == null || apelido.isBlank()) {
            return nome;
        }
        return apelido;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
