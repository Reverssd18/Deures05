package com.exercici0500;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

public class Cinema {

    /**
     * Crea la taula "directors", si ja existeix primer l'esborra
     */
    public static void crearTaulaDirectors() {
        AppData db = AppData.getInstance();
        db.update("DROP TABLE IF EXISTS directors");
        String sql = """
            CREATE TABLE IF NOT EXISTS directors (
                id_director INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                nacionalitat TEXT NOT NULL
            );
        """;
        db.update(sql);
    }

    public static void crearTaulaPelis() {
        AppData db = AppData.getInstance();
        db.update("DROP TABLE IF EXISTS pelis");
        String sql = """
            CREATE TABLE IF NOT EXISTS pelis (
                id_peli INTEGER PRIMARY KEY AUTOINCREMENT,
                titol TEXT NOT NULL,
                any_estrena INTEGER NOT NULL,
                durada INTEGER NOT NULL,
                id_director INTEGER,
                FOREIGN KEY(id_director) REFERENCES directors(id)
            );
        """;
        db.update(sql);
    }

    public static void crearTaulaSales() {
        AppData db = AppData.getInstance();
        db.update("DROP TABLE IF EXISTS sales");
        String sql = """
            CREATE TABLE IF NOT EXISTS sales (
                id_sala INTEGER PRIMARY KEY AUTOINCREMENT,
                nom_sala TEXT NOT NULL,
                capacitat INTEGER NOT NULL,
                id_peli INTEGER,
                FOREIGN KEY(id_peli) REFERENCES pelis(id_peli)
            );
        """;
        db.update(sql);
    }

    /**
     * Afegeix un nou director
     * @param nomDirector
     * @param nacionalitat
     * @return l'identificador del director afegit
     */
    public static int afegirDirector(String nomDirector, String nacionalitat) {
        AppData db = AppData.getInstance();
        String sql = String.format("INSERT INTO directors (nom, nacionalitat) VALUES ('%s','%s')",nomDirector,nacionalitat);
        return db.insertAndGetId(sql);
    }

    public static int afegirPeli(String titol, int any, int duracio, int idDirector) {
        AppData db = AppData.getInstance();
        String sql = String.format("INSERT INTO pelis (titol, any_estrena, durada, id_director) VALUES ('%s','%d','%d','%d')",titol, any, duracio ,idDirector);
        return db.insertAndGetId(sql);
    }

    public static int afegirSala(String nomSala, int capacitat, int idPeli) {
        AppData db = AppData.getInstance();
        String sql = String.format("INSERT INTO sales (nom_sala, capacitat, id_peli) VALUES ('%s','%d','%d')",nomSala, capacitat, idPeli);
        return db.insertAndGetId(sql);
    }

    public static String generaMarcTaula(int[] columnWidths, char[] separators) {

        String rst = "";

        rst += separators[0];
        for (int i = 0; i < columnWidths.length; i++) {
            for (int j = 0; j < columnWidths[i];j++){
                rst += "─";
            }
            if (i<columnWidths.length -1) {
                rst += separators[1];
            }   
            
        }
        rst += separators[2];


        return rst;
    }

    public static String formatRow(String[] values, int[] columnWidths) {

        String rst = "│";
        for (int val = 0; val < values.length; val++) {
            if (values[val].length() > columnWidths[val]) {
                rst += values[val].substring(0, columnWidths[val]) + "│";
            } else {
                rst += values[val] + " ".repeat(columnWidths[val] - values[val].length()) + "│";
            }
        }
        return rst;
    }
    /**
     * Mostra una taula amb informació dels directors:
     * ┌────────────┬──────────────┐
     * │ Nom        │ Nacionalitat │
     * ├────────────┼──────────────┤
     * │ Director A │ País X       │
     * │ Director B │ País Y       │
     * └────────────┴──────────────┘
     */
    public static void llistarTaulaDirectors() {

        AppData db = AppData.getInstance();
        String sql = "SELECT * FROM directors";
        ArrayList<HashMap<String, Object>> table = db.query(sql);
        
        int[] columnWidths = {14, 16};
        char[] separators =  {'┌', '┬', '┐'};
        char[] separators2 = {'├', '┼', '┤'};
        char[] separators3 = {'└', '┴', '┘'};
        
        System.out.println(generaMarcTaula(columnWidths, separators));

        String[] values = {"Nom", "Nacionalitat"};

        System.out.println(formatRow(values, columnWidths));
        
        System.out.println(generaMarcTaula(columnWidths, separators2));

        String[] rst = new String[2];

        for (HashMap<String, Object> row : table) {
            rst[0] = (String) row.get("nom");
            rst[1] = (String) row.get("nacionalitat");
            String info = formatRow(rst, columnWidths);
            System.out.println(info);
        }
        
        System.out.println(generaMarcTaula(columnWidths, separators3));

    }

    public static void llistarTaulaPelis() {
        AppData db = AppData.getInstance();
        String sql = "SELECT * FROM pelis p join directors d on p.id_director = d.id_director";
        ArrayList<HashMap<String, Object>> table = db.query(sql);
        
        int[] columnWidths = {14 ,14, 16, 16, 15, 16};
        
        System.out.println(generaMarcTaula(columnWidths, new char[] {'┌', '┬', '┐'}));

        String[] values = {"Id Peli", "Titol", "Any_Estrena", "Durada", "Id_Director", "Nom director"};
        System.out.println(formatRow(values, columnWidths));
        
        System.out.println(generaMarcTaula(columnWidths, new char[] {'├', '┼', '┤'}));

        for (HashMap<String, Object> row : table) {
            String[] filas = {
                String.valueOf(row.get("id_peli")),
                String.valueOf(row.get("titol")),
                String.valueOf(row.get("any_estrena")),
                String.valueOf(row.get("durada")),
                String.valueOf(row.get("id_director")),
                String.valueOf(row.get("nom"))
            };
            String info = formatRow(filas, columnWidths);
            System.out.println(info);
        }

        System.out.println(generaMarcTaula(columnWidths, new char[] {'└', '┴', '┘'}));
    }

    public static void llistarTaulaSales() {
        AppData db = AppData.getInstance();
        String sql = "SELECT * FROM sales s join pelis p on s.id_peli=p.id_peli";
        ArrayList<HashMap<String, Object>> table = db.query(sql);
        
        int[] columnWidths = {14,16,20,14,16};
        
        System.out.println(generaMarcTaula(columnWidths, new char[] {'┌', '┬', '┐'}));

        String[] values = {"Id Sala", "Nom", "Capacitat", "Id Peli", "Titol"};

        System.out.println(formatRow(values, columnWidths));
        
        System.out.println(generaMarcTaula(columnWidths, new char[] {'├', '┼', '┤'}));

        String[] rst = new String[5];

        for (HashMap<String, Object> row : table) {
            rst[0] = String.valueOf(row.get("id_sala")); 
            rst[1] = String.valueOf(row.get("nom_sala"));  
            rst[2] = String.valueOf(row.get("capacitat"));
            rst[3] = String.valueOf(row.get("id_peli"));
            rst[4] = String.valueOf(row.get("titol"));
            String info = formatRow(rst, columnWidths);
            System.out.println(info);
        }
        
        System.out.println(generaMarcTaula(columnWidths, new char[] {'└', '┴', '┘'}));
    }

    /**
     * Mostra una fitxa amb informació de les pelis:
     * 
     * ┌─────────────────────────┐
     * │ Film A                  │
     * ├──────────┬──────────────┤
     * │ Id       │ 1            │
     * │ Direcció │ Director A   │
     * │ Any      │ 2020         │
     * │ Duració  │ 120 minuts   │
     * └──────────┴──────────────┘
     */
    public static void llistarInfoPeli(int idPeli) {
        AppData db = AppData.getInstance();
        String sql_peli = String.format("SELECT * FROM pelis WHERE id_peli = %d", idPeli);
        ArrayList<HashMap<String, Object>> table = db.query(sql_peli);

        if (table.isEmpty()) {
            System.out.println("No s'ha trobat cap pel·lícula amb aquest ID.");
            return;
        }

        HashMap<String, Object> peli = table.get(0);

        String titol = (String) peli.get("titol");
        int anyEstrena = (int) peli.get("any_estrena");
        int durada = (int) peli.get("durada");
        int idDirector = (int) peli.get("id_director");

        String sql_director = String.format("SELECT nom FROM directors WHERE id_director = %d", idDirector);
        ArrayList<HashMap<String, Object>> directorTable = db.query(sql_director);
        String nomDirector = directorTable.isEmpty() ? "Desconegut" : (String) directorTable.get(0).get("nom");

        int[] columnWidths = {10, 14};
        char[] separators = {'┌', '─', '┐'};
        char[] separators2 = {'├', '┬', '┤'};
        char[] separators3 = {'└', '┴', '┘'};

        System.out.println(generaMarcTaula(columnWidths, separators));
        System.out.println("│ " + titol + " ".repeat(24 - titol.length()) + "│");
        System.out.println(generaMarcTaula(columnWidths, separators2));

        String[] values = {"Id", String.valueOf(idPeli)};
        System.out.println(formatRow(values, columnWidths));

        values = new String[]{"Direcció", nomDirector};
        System.out.println(formatRow(values, columnWidths));

        values = new String[]{"Any", String.valueOf(anyEstrena)};
        System.out.println(formatRow(values, columnWidths));

        values = new String[]{"Duració", String.valueOf(durada) + " minuts"};
        System.out.println(formatRow(values, columnWidths));

        System.out.println(generaMarcTaula(columnWidths, separators3));
    }

    /**
     * Guarda la informacio de les películes en un arxiu ".json"
     * @throws IOException 
     */
    public static void pelisToJSON(String path) throws IOException {
        AppData db = AppData.getInstance();
        String sql = "SELECT * FROM pelis";
        ArrayList<HashMap<String, Object>> table = db.query(sql);
        JSONArray tableArray = new JSONArray(table);
        Files.write(Paths.get(path), tableArray.toString(4).getBytes());
    }
}
