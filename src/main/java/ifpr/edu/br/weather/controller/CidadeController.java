package ifpr.edu.br.weather.controller;

import ifpr.edu.br.weather.api.OpenWeatherApi;
import ifpr.edu.br.weather.model.Cidade;
import ifpr.edu.br.weather.repository.CidadeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cidades")
public class CidadeController {

    private final CidadeRepository cidadeRepository;
    private final OpenWeatherApi openWeatherApi;

    public CidadeController(CidadeRepository cidadeRepository, OpenWeatherApi openWeatherApi) {
        this.cidadeRepository = cidadeRepository;
        this.openWeatherApi = openWeatherApi;
    }

    // Listar
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cidades", cidadeRepository.findAllByOrderByNomeAsc());
        return "cidades/lista";
    }

    // Formulário de cadastro (pode vir preenchido a partir da busca na API)
    @GetMapping("/nova")
    public String nova(@RequestParam(required = false) String nome,
                       @RequestParam(required = false) String pais,
                       Model model) {
        Cidade cidade = new Cidade();
        cidade.setNome(nome);
        cidade.setPais(pais);
        model.addAttribute("cidade", cidade);
        return "cidades/form";
    }

    // Formulário de edição
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Cidade cidade = cidadeRepository.findById(id).orElse(null);
        if (cidade == null) {
            redirect.addFlashAttribute("erro", "Cidade não encontrada.");
            return "redirect:/cidades";
        }
        model.addAttribute("cidade", cidade);
        return "cidades/form";
    }

    // Cadastrar e editar
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cidade cidade, Model model, RedirectAttributes redirect) {
        if (cidade.getNome() == null || cidade.getNome().isBlank()) {
            model.addAttribute("erro", "O nome da cidade é obrigatório.");
            model.addAttribute("cidade", cidade);
            return "cidades/form";
        }
        if (cidade.getPais() != null) {
            cidade.setPais(cidade.getPais().trim().toUpperCase());
        }
        boolean novo = cidade.getId() == null;
        cidadeRepository.save(cidade);
        redirect.addFlashAttribute("mensagem", novo ? "Cidade cadastrada!" : "Cidade atualizada!");
        return "redirect:/cidades";
    }

    // Excluir
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        cidadeRepository.deleteById(id);
        redirect.addFlashAttribute("mensagem", "Cidade excluída.");
        return "redirect:/cidades";
    }

    // Clima atual + previsão de uma cidade cadastrada (consulta a API)
    @GetMapping("/{id}/clima")
    public String clima(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Cidade cidade = cidadeRepository.findById(id).orElse(null);
        if (cidade == null) {
            redirect.addFlashAttribute("erro", "Cidade não encontrada.");
            return "redirect:/cidades";
        }
        model.addAttribute("cidade", cidade);

        if (!openWeatherApi.isConfigurada()) {
            model.addAttribute("erro", "Chave da API não configurada (OPENWEATHER_API_KEY).");
            return "cidades/clima";
        }
        try {
            model.addAttribute("clima", openWeatherApi.buscarClimaAtual(cidade.getConsulta()));
            model.addAttribute("previsoes", openWeatherApi.buscarPrevisao(cidade.getConsulta()));
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("erro", "A API não encontrou a cidade \"" + cidade.getConsulta()
                    + "\". Confira o nome e o código do país.");
        } catch (HttpClientErrorException.Unauthorized e) {
            model.addAttribute("erro", "Chave da API inválida.");
        } catch (RestClientException e) {
            model.addAttribute("erro", "Não foi possível consultar a API agora. Tente novamente.");
        }
        return "cidades/clima";
    }
}
