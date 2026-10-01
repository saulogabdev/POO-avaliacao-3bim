# Clima – Consulta de tempo com OpenWeatherMap

Trabalho do 3º bimestre de **Programação Orientada a Objetos** (3º Informática – IFPR).

## Integrantes

- _Nome do integrante 1_
- _Nome do integrante 2_

## Tema

Consulta de clima: o usuário pesquisa o tempo de qualquer cidade e mantém uma lista de cidades favoritas para acompanhar.

## Descrição do sistema

Aplicação web em **Java + Spring Boot** no padrão **MVC**:

| Camada | Classes |
|---|---|
| Model / Entity | `Cidade` (entidade JPA: nome, país, apelido, observação, data de cadastro) |
| Model (dados da API) | `Clima` (tempo atual) e `Previsao` (previsão dos próximos dias) |
| Repository | `CidadeRepository` (estende `JpaRepository`) |
| Controller | `CidadeController` (CRUD + clima da cidade) e `ClimaController` (busca) |
| Integração | `OpenWeatherApi` (consome a API com `RestClient`) |
| View | Páginas HTML com **Thymeleaf** em `src/main/resources/templates` |

Funcionalidades:

- **Cadastrar, listar, editar e excluir** cidades (salvas no banco MySQL via Spring Data JPA).
- **Buscar o clima** de qualquer cidade pelo nome.
- **Ver o clima** de uma cidade cadastrada com um clique.
- **Salvar** uma cidade encontrada na busca (o formulário já vem preenchido com nome e país vindos da API).

## API utilizada

**OpenWeatherMap** – https://openweathermap.org/api

- Tempo atual: `GET https://api.openweathermap.org/data/2.5/weather?q={cidade}&units=metric&lang=pt_br&appid={chave}`
  (documentação: https://openweathermap.org/current)
- Previsão de 5 dias: `GET https://api.openweathermap.org/data/2.5/forecast?q={cidade}&units=metric&lang=pt_br&appid={chave}`
  (documentação: https://openweathermap.org/forecast5)

### O que o sistema faz com a API

1. O usuário digita o nome de uma cidade (ou clica em "Ver clima" numa cidade cadastrada).
2. A classe `OpenWeatherApi` consulta a API e recebe a resposta em JSON.
3. O JSON é convertido em objetos `Clima` e `Previsao`.
4. A tela mostra temperatura, sensação térmica, mínima/máxima, umidade, vento (convertido de m/s para km/h),
   descrição e ícone do tempo, uma **dica** gerada a partir dos dados (ex.: "Leve um guarda-chuva!")
   e a **previsão dos próximos dias** (um cartão por dia, horário do meio-dia).
5. Na busca, o usuário pode salvar a cidade encontrada no banco.

## Como executar

Pré-requisitos: Java 21 e MySQL rodando.

1. Crie uma chave gratuita em https://home.openweathermap.org/api_keys (pode levar alguns minutos para ativar).
2. Configure as variáveis de ambiente (ou altere `src/main/resources/application.properties`):
   - `OPENWEATHER_API_KEY` – chave da API
   - `DB_USER` / `DB_PASSWORD` – usuário e senha do MySQL (padrão `root` / `root`)
   - `DB_URL` – opcional; por padrão usa `jdbc:mysql://localhost:3306/weather` e cria o banco se não existir
3. Rode:

```bash
./mvnw spring-boot:run
```

No Windows: `mvnw.cmd spring-boot:run`

4. Acesse http://localhost:8080

## Testes

```bash
./mvnw test
```

Os testes usam banco H2 em memória e simulam a API do OpenWeatherMap com um servidor local, então não precisam de MySQL nem de chave.
