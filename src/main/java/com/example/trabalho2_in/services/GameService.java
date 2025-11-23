package com.example.trabalho2_in.services;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trabalho2_in.dtos.GameCalcularDto;
import com.example.trabalho2_in.dtos.GameRecomendacao;
import com.example.trabalho2_in.models.Game;

import org.pmml4s.model.Model;

@Service
public class GameService {

    @Autowired
    private ExcelReaderService excelReaderService;

    public GameRecomendacao calcular(GameCalcularDto dto) {
        Map<String, Object> saidaModelo = null;

        try (FileInputStream modelInputStream = new FileInputStream(
                "src/main/resources/model/base1.pmml")) {

            Model model = Model.fromInputStream(modelInputStream);
            var dadosNormalizados = prepararDados(dto);
            saidaModelo = model.predict(dadosNormalizados);

            System.out.println("Chaves disponíveis no resultado: " + saidaModelo.keySet());
            System.out.println("Resultado completo: " + saidaModelo);

        } catch (Exception e) {
            e.printStackTrace();
            return new GameRecomendacao(
                "Erro",
                "Não foi possível processar a recomendação. Verifique os dados inseridos.",
                List.of("Tente novamente com valores válidos")
            );
        }

        String cluster = null;
        if (saidaModelo.containsKey("cluster_name")) {
            cluster = saidaModelo.get("cluster_name").toString();
        } else if (saidaModelo.containsKey("predictedValue")) {
            cluster = saidaModelo.get("predictedValue").toString();
        } else if (saidaModelo.containsKey("predictedDisplayValue")) {
            cluster = saidaModelo.get("predictedDisplayValue").toString();
        } else if (saidaModelo.containsKey("cluster")) {
            try {
                int num = Integer.parseInt(saidaModelo.get("cluster").toString());
                cluster = "cluster_" + (num - 1);
            } catch (NumberFormatException ex) {
                cluster = saidaModelo.get("cluster").toString();
            }
        } else {
            Object firstVal = saidaModelo.values().iterator().next();
            cluster = firstVal == null ? "unknown" : firstVal.toString();
        }

        System.out.println("Cluster identificado: " + cluster);

        // Buscar jogos do mesmo cluster no Excel
        List<Game> jogosDoCluster = excelReaderService.buscarPorCluster(cluster);
        
        if (jogosDoCluster.isEmpty()) {
            System.out.println("⚠️ Nenhum jogo encontrado para o cluster: " + cluster);
            return getFallbackRecomendacao(cluster);
        }
        
        System.out.println("📊 " + jogosDoCluster.size() + " jogos encontrados no cluster " + cluster);
        
        // Calcular similaridade para todos os jogos
        Map<Game, Double> similaridades = new HashMap<>();
        for (Game game : jogosDoCluster) {
            similaridades.put(game, calcularSimilaridade(dto, game));
        }
        
        // Pegar top 20 mais similares
        List<Game> topJogos = similaridades.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .limit(20) // Pega os 20 melhores
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // Embaralhar para adicionar aleatoriedade
        Collections.shuffle(topJogos, new Random());
        
        // Pegar apenas 10 (agora aleatórios entre os 20 melhores)
        List<String> jogosRecomendados = topJogos.stream()
            .limit(10)
            .map(Game::getName)
            .toList();
        
        System.out.println("🎮 Recomendando " + jogosRecomendados.size() + " jogos aleatórios entre os top 20 mais similares");
        
        String descricao = getDescricaoCluster(cluster);
        String texto = getTextoDescricao(cluster);
        
        return new GameRecomendacao(descricao, texto, jogosRecomendados);
    }
    
    private double calcularSimilaridade(GameCalcularDto dto, Game game) {
        // Distância euclidiana entre o perfil do usuário e o jogo
        double distancia = Math.sqrt(
            Math.pow(dto.getSports() - game.getSports(), 2) +
            Math.pow(dto.getRacing() - game.getRacing(), 2) +
            Math.pow(dto.getRolePlaying() - game.getRolePlaying(), 2) +
            Math.pow(dto.getPuzzle() - game.getPuzzle(), 2) +
            Math.pow(dto.getMisc() - game.getMisc(), 2) +
            Math.pow(dto.getShooter() - game.getShooter(), 2) +
            Math.pow(dto.getSimulation() - game.getSimulation(), 2) +
            Math.pow(dto.getAction() - game.getAction(), 2) +
            Math.pow(dto.getFighting() - game.getFighting(), 2) +
            Math.pow(dto.getAdventure() - game.getAdventure(), 2) +
            Math.pow(dto.getStrategy() - game.getStrategy(), 2)
        );
        
        // Retorna similaridade (quanto maior, mais similar)
        return 1.0 / (1.0 + distancia);
    }
    
    private String getDescricaoCluster(String cluster) {
        return recomendacoes.containsKey(cluster) 
            ? recomendacoes.get(cluster).getGrupo()
            : "Cluster " + cluster;
    }
    
    private String getTextoDescricao(String cluster) {
        return recomendacoes.containsKey(cluster)
            ? recomendacoes.get(cluster).getDescricao()
            : "Jogos classificados no grupo " + cluster;
    }
    
    private GameRecomendacao getFallbackRecomendacao(String cluster) {
        if (recomendacoes.containsKey(cluster)) {
            return recomendacoes.get(cluster);
        } else {
            return new GameRecomendacao(
                "Cluster Desconhecido (identificado: " + cluster + ")",
                "Não foi possível identificar um grupo específico para este perfil.",
                List.of("Experimente jogos de diversos gêneros", "Ajuste os valores e tente novamente")
            );
        }
    }

    private Map<String, Object> prepararDados(GameCalcularDto dto) {
        Map<String, Object> dados = new HashMap<>();

        dados.put("Global_Sales", dto.getGlobalSales());
        dados.put("Sports", dto.getSports());
        dados.put("Racing", dto.getRacing());
        dados.put("Role-Playing", dto.getRolePlaying());
        dados.put("Puzzle", dto.getPuzzle());
        dados.put("Misc", dto.getMisc());
        dados.put("Shooter", dto.getShooter());
        dados.put("Simulation", dto.getSimulation());
        dados.put("Action", dto.getAction());
        dados.put("Fighting", dto.getFighting());
        dados.put("Adventure", dto.getAdventure());
        dados.put("Strategy", dto.getStrategy());

        return dados;
    }

    private final Map<String, GameRecomendacao> recomendacoes = Map.of(

            "cluster_0", new GameRecomendacao(
                    "Cluster 0 - Fãs de Shooter",
                    "Perfil focado em jogos de tiro (Shooter). Jogos com ação rápida e combate intenso.",
                    List.of("Call of Duty", "Halo", "Counter-Strike", "Battlefield", "Titanfall")
            ),

            "cluster_1", new GameRecomendacao(
                    "Cluster 1 - Entusiastas de Esportes",
                    "Perfil voltado para jogos de esportes (Sports). Simulações realistas e competitivas.",
                    List.of("FIFA", "NBA 2K", "Madden NFL", "Pro Evolution Soccer", "NHL")
            ),

            "cluster_2", new GameRecomendacao(
                    "Cluster 2 - Jogadores Ecléticos",
                    "Perfil diversificado com interesse em múltiplos gêneros (Racing, Puzzle, Simulation, Fighting, Adventure, Strategy).",
                    List.of("The Legend of Zelda", "Mario Kart", "Tetris", "Civilization", "Street Fighter")
            ),

            "cluster_3", new GameRecomendacao(
                    "Cluster 3 - Amantes de Misc",
                    "Perfil focado em jogos diversos e experimentais (Misc). Experiências únicas e inovadoras.",
                    List.of("Wii Sports", "Nintendogs", "Brain Age", "Just Dance", "Guitar Hero")
            ),

            "cluster_4", new GameRecomendacao(
                    "Cluster 4 - Fãs de Action",
                    "Perfil voltado para jogos de ação (Action). Aventuras épicas e combate dinâmico.",
                    List.of("Grand Theft Auto", "Super Mario Bros", "Assassin's Creed", "Uncharted", "The Last of Us")
            ),

            "cluster_5", new GameRecomendacao(
                    "Cluster 5 - Jogadores de RPG",
                    "Perfil focado em Role-Playing Games (RPG). Narrativas profundas e progressão de personagem.",
                    List.of("Final Fantasy", "Pokémon", "The Elder Scrolls", "Dragon Quest", "Mass Effect")
            )
    );
}

