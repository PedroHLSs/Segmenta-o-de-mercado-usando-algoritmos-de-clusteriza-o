package com.example.trabalho2_in.services;

import com.example.trabalho2_in.models.Game;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {
    
    private List<Game> games;
    
    public ExcelReaderService() {
        this.games = carregarJogos();
    }
    
    private List<Game> carregarJogos() {
        List<Game> listaJogos = new ArrayList<>();
        
        try (FileInputStream file = new FileInputStream("src/main/resources/data/Dados.xlsx");
             Workbook workbook = new XSSFWorkbook(file)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Pular cabeçalho (linha 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Game game = new Game();
                game.setName(getCellValueAsString(row.getCell(0)));
                game.setPlatform(getCellValueAsString(row.getCell(1)));
                game.setYear(getCellValueAsInteger(row.getCell(2)));
                game.setGlobalSales(getCellValueAsDouble(row.getCell(3)));
                game.setSports(getCellValueAsDouble(row.getCell(4)));
                game.setRacing(getCellValueAsDouble(row.getCell(5)));
                game.setRolePlaying(getCellValueAsDouble(row.getCell(6)));
                game.setPuzzle(getCellValueAsDouble(row.getCell(7)));
                game.setMisc(getCellValueAsDouble(row.getCell(8)));
                game.setShooter(getCellValueAsDouble(row.getCell(9)));
                game.setSimulation(getCellValueAsDouble(row.getCell(10)));
                game.setAction(getCellValueAsDouble(row.getCell(11)));
                game.setFighting(getCellValueAsDouble(row.getCell(12)));
                game.setAdventure(getCellValueAsDouble(row.getCell(13)));
                game.setStrategy(getCellValueAsDouble(row.getCell(14)));
                game.setCluster(getCellValueAsString(row.getCell(15)));
                
                listaJogos.add(game);
            }
            
            System.out.println("✅ " + listaJogos.size() + " jogos carregados do Excel");
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao ler arquivo Excel: " + e.getMessage());
            System.err.println("   Certifique-se de que o arquivo existe em: src/main/resources/data/games.xlsx");
        }
        
        return listaJogos;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
    
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }
    
    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    public List<Game> buscarPorCluster(String cluster) {
        return games.stream()
            .filter(g -> cluster.equals(g.getCluster()))
            .toList();
    }
    
    public List<Game> getTodosJogos() {
        return games;
    }
}
