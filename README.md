# Segmentação de mercado usando algoritmos de clusterização

Aplicação web em **Java + Spring Boot** para recomendação de jogos com base em perfil de preferências.  
O sistema recebe características de interesse por gênero, identifica um **cluster** com modelo **PMML (K-means)** e retorna jogos recomendados do mesmo grupo.

## Funcionalidades

- Formulário web para entrada do perfil do usuário.
- Predição de cluster via arquivo `base1.pmml`.
- Leitura de base de jogos em `Dados.xlsx`.
- Cálculo de similaridade entre perfil informado e jogos do cluster.
- Exibição de recomendações na interface web (Thymeleaf).

## Tecnologias usadas

- Java 21
- Spring Boot 4
- Spring MVC
- Thymeleaf
- PMML4S (`org.pmml4s:pmml4s_3`)
- Apache POI (leitura de Excel)
- Maven (com wrapper `mvnw`)

## Estrutura principal

```text
src/main/java/com/example/trabalho2_in
├── controllers/GameController.java
├── services/GameService.java
├── services/ExcelReaderService.java
├── dtos/GameCalcularDto.java
├── dtos/GameRecomendacao.java
└── models/Game.java

src/main/resources
├── data/Dados.xlsx
├── model/base1.pmml
├── templates/games-form.html
├── templates/games-resultado.html
└── static/style.css
```

## Como executar

### Pré-requisitos

- JDK 21 instalado
- Maven (opcional, pois o projeto inclui Maven Wrapper)

### Passos

1. Clone o repositório.
2. Acesse a pasta do projeto.
3. Execute:

```bash
.\mvnw spring-boot:run
```

4. Abra no navegador:

```text
http://localhost:8080/games/form
```

## Fluxo de uso

1. Preencha os campos do formulário (`Global Sales` e gêneros).
2. Envie para cálculo.
3. O sistema:
   - prepara os dados de entrada para o PMML,
   - identifica o cluster previsto,
   - busca jogos do cluster na base Excel,
   - calcula similaridade,
   - sorteia recomendações entre os mais similares.
4. A página de resultado mostra grupo, descrição e lista de jogos.

## Observações

- Os arquivos `src/main/resources/model/base1.pmml` e `src/main/resources/data/Dados.xlsx` precisam existir para a recomendação funcionar corretamente.
- Em caso de falha na predição, o sistema retorna uma recomendação de fallback com mensagem amigável.
