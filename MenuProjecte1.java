package provesMYSQL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class MenuProjecte1 {
    static final String PATHPENDENTS = "files/ENTRADES PENDENTS/";
    static final String PATHPROCESSATS = "files/ENTRADES PROCESSADES/";
    static final String PATHCOMANDES = "files/COMANDES/";
    static Connection connexioBD = null;
    static Scanner teclat = new Scanner(System.in);

    static FileWriter fw = null;
    static BufferedWriter bw = null;
    static PrintWriter pw = null;

    static String nom_mevaempresa = "Nabitek";
    static String inf_empresaprov = "Tàrrega";
    static String[] nom_proveidor = new String[3];
    static int[] num_productes = new int[3];
    

    public static void main(String[] args) throws SQLException, IOException {

        boolean sortir = false;

        connexioBD();

        do {
            System.out.println("\n-----GESTOR D'INVENTARI-----");
            System.out.println("1. Gestió productes");
            System.out.println("2. Actualitzar stock");
            System.out.println("3. Preparar comandes");
            System.out.println("4. Analitzar les comandes");
            System.out.println("5. Sortir");
            System.out.print("\nTRIA UNA OPCIÓ: ");

            int opcio = teclat.nextInt();

            switch (opcio) {
            case 1:
                gestioProductes();
                break;
            case 2:
                actualitzarStock();
                break;
            case 3:
                prepararComandes();
                break;
            case 4:
                analisiComandes();
                break;
            case 5:
                sortir = true;
                break;
            default:
                System.out.println("VALOR NO VÀLID");
                break;
            }
        } while (!sortir);
        desconnexioBD();
    }

    static void gestioProductes() throws SQLException {

        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        connexioBD();

        do {
            System.out.println("\n-----GESTOR PRODUCTES-----");
            System.out.println("1. LLISTA tots els productes");
            System.out.println("2. Consulta un producte");
            System.out.println("3. ALTA producte");
            System.out.println("4. MODIFICA producte");
            System.out.println("5. ESBORRA producte");
            System.out.println("6. Sortir");
            System.out.print("\nTRIA UNA OPCIÓ: ");

            int opcio = teclat.nextInt();

            switch (opcio) {
            case 1:
                llistarTotsProductes();
                break;
            case 2:
                consultaProducte();
                break;
            case 3:
                altaProductes();
                break;
            case 4:
                modificarProducte();
                break;
            case 5:
                esborraProductes();
                break;
            case 6:
                sortir = true;
                break;
            default:
                System.out.println("VALOR NO VÀLID");
                break;
            }
        } while (!sortir);
    }

    static void consultaProducte() throws SQLException {
        System.out.println("Introdueix l'ID del producte que vols consultar: ");
        int id = teclat.nextInt();

        String consulta = "SELECT * FROM Productes WHERE id = " + id;

        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.executeQuery();

        ResultSet rs = sentencia.executeQuery();

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + " | Nom: " + rs.getString("nom") + " | Estoc: "
                    + rs.getInt("estoc") + " | Imatge: " + rs.getString("imatge") + " | Codi categoria: "
                    + rs.getInt("codi_categoria"));
        }
    }

    static void altaProductes() throws SQLException {
        System.out.println("Introdueix el nom del producte: ");
        teclat.nextLine();
        String nom = teclat.nextLine();
        System.out.println("Introdueix l'estoc del producte: ");
        int estoc = teclat.nextInt();
        System.out.println("Introdueix la url de la imatge");
        teclat.nextLine();
        String url = teclat.nextLine();
        System.out.println("Introdueix el codi de la categoria: ");
        int codiCat = teclat.nextInt();

        String consulta = "INSERT INTO Productes (nom, estoc, imatge, codi_categoria) VALUES (?,?,?,?)";
        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.setString(1, nom);
        sentencia.setInt(2, estoc);
        sentencia.setString(3, url);
        sentencia.setInt(4, codiCat);

        sentencia.executeUpdate();

        String consulta_prod = "SELECT id FROM Productes where nom='" + nom + "'";
        PreparedStatement sentencia2 = connexioBD.prepareStatement(consulta_prod);

        ResultSet rs = sentencia2.executeQuery();

        int ID = 0;

        while (rs.next()) {
            ID = rs.getInt("id");
        }

        System.out.println("Codi del Material en que està format el Producte: ");
        System.out.println("1. Fusta ");
        System.out.println("2. Fibra ");
        System.out.println("3. Acer ");
        System.out.println("4. Alumini ");
        System.out.println("5. Vidre ");
        System.out.println("6. Tela ");
        System.out.println("7. Guata ");
        System.out.println("8. Plàstic ");

        int codi = teclat.nextInt();

        String inserció2 = "INSERT INTO Formats (id, codi) value (?,?)";
        PreparedStatement sentencia3 = connexioBD.prepareStatement(inserció2);

        sentencia3.setInt(1, ID);
        sentencia3.setInt(2, codi);

        if (sentencia3.executeUpdate() != 0) {
            System.out.println("Producte donat d'alta: " + nom + " | " + estoc + " | " + url + " | " + codiCat);
        } else {
            System.out.println("No s'ha donat d'alta cap producte");
        }
    }

    static void llistarTotsProductes() throws SQLException {
        System.out.println("LLISTAT DE TOTS ELS PRODUCTES");

        // Creem un String, el valor serà la consulta que volem fer.
        String consulta = "SELECT * FROM Productes ORDER BY id";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);

        // Per fer SELECT fem executeQuery, per fer INSERT, UPDATE, DELETE
        // executeUpdate()
        ps.executeQuery();

        ResultSet rs = ps.executeQuery();

        // rs.next retorna un valor booleà. TRUE si hi ha més registres de la consulta
        // que s'ha fet, FALSE si no n'hi ha.
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + " | Nom: " + rs.getString("nom") + " | Estoc: "
                    + rs.getInt("estoc") + " | Imatge: " + rs.getString("imatge") + " | Codi categoria: "
                    + rs.getInt("codi_categoria"));
        }
    }

    static void modificarProducte() throws SQLException {
        System.out.println("Escriu la ID producte que vol modificar: ");
        int id = teclat.nextInt();
        teclat.nextLine();

        String productes = " SELECT * FROM Productes WHERE id =" + id;
        PreparedStatement dades = connexioBD.prepareStatement(productes);
        dades.executeQuery();

        ResultSet producte = dades.executeQuery();

        if (!producte.next()) {

        } else {
            // Declaro les variables on es guardaran els valors dels atributs del producte.
            String nom;
            int estoc;
            String imatge;
            int codi_categoria;

            // Aquesta serà la consulta que fa per actualitzar els atributs.
            String actualitzar = "UPDATE Productes SET nom = ?, estoc= ?, imatge= ?, codi_categoria= ? where id = "
                    + id;
            PreparedStatement sentencia = connexioBD.prepareStatement(actualitzar);

            System.out.println("Escriu 'S' si vols editar el nom");
            String resposta = teclat.nextLine();

            // Faig un IF, si l'usuari indica que vol editar el nom es posarà el valor que
            // escrigui a la variable nom, si no escriu S la variable nom mantindrà el valor
            // que ja té.
            // Es repeteix això per a tots els atributs del producte.
            if (resposta.equals("S")) {
                System.out.println("Escriu el NOU nom del producte: ");
                nom = teclat.nextLine();
                sentencia.setString(1, nom);
            } else {
                nom = producte.getString("nom");
                sentencia.setString(2, nom);
            }
            System.out.println("Escriu 'S' si vols editar l'estoc");
            String resposta1 = teclat.nextLine();
            if (resposta1.equals("S")) {
                System.out.println("Escriu l'estoc del producte: ");
                estoc = teclat.nextInt();
                sentencia.setInt(2, estoc);
                teclat.nextLine();
            } else {
                estoc = producte.getInt("estoc");
                sentencia.setInt(2, estoc);
            }
            System.out.println("Escriu 'S' si vols editar l'enllaç de la imatge");
            String resposta2 = teclat.nextLine();
            if (resposta2.equals("S")) {
                System.out.println("Escriu l'enllaç de la imatge del producte: ");
                imatge = teclat.nextLine();
                sentencia.setString(3, imatge);
            } else {
                imatge = producte.getString("imatge");
                sentencia.setString(3, imatge);
            }
            System.out.println("Escriu 'S' si vols editar la categoria");
            String resposta3 = teclat.nextLine();
            if (resposta3.equals("S")) {
                System.out.println("Escriu el codi de la categoria del producte: ");
                codi_categoria = teclat.nextInt();
                sentencia.setInt(4, codi_categoria);
                teclat.nextLine();
            } else {
                codi_categoria = producte.getInt("codi_categoria");
                sentencia.setInt(4, codi_categoria);
            }

            // S'executa i imprimeix un missatge indicant si s'ha fet correctament.
            sentencia.executeUpdate();

            if (sentencia.executeUpdate() != 0) {
                System.out.println("El producte " + id + " s'ha actualitzat correctament");
            } else {
                System.out.println("No s'ha actualitzat cap producte");
            }
        }
    }

    static void esborraProductes() throws SQLException {
        System.out.println("Introdueix la ID del producte que vols eliminar: ");
        int id = teclat.nextInt();

        String consulta = "DELETE FROM Productes where id = " + id;
        PreparedStatement delete = connexioBD.prepareStatement(consulta);

        
        if (delete.executeUpdate() != 0) {
            System.out.println("S'ha eliminat el producte " + id);
        } else {
            System.out.println("No s'ha eliminat cap producte");
        }

    }

    static void actualitzarStock() throws IOException, SQLException {
        //He declarat el directori com a CONSTANT al principi del programa.
        File fitxer = new File (PATHPENDENTS);

        //Aquest mètode crea els directoris que hem indicat.
        fitxer.mkdirs();

        if (fitxer.isDirectory()) {
            //El mètode .listFiles retorna un array d'objectes de tipus FILE.
            File[] fitxers = fitxer.listFiles();
            
            //Aquest bucle recorre els fitxers fins que els ha recorregut tots
            for (int i=0; i< fitxers.length; i++) {
                System.out.println(fitxers[i].getName());
                actualitzarFitxerBD(fitxers[i]);
                moureFitxerAProcessat(fitxers[i]);
            }
        }

        File fitxer3 = new File (PATHPROCESSATS);
        fitxer3.mkdirs();

    }

    static void actualitzarFitxerBD(File fitxer) throws IOException, SQLException {
        FileReader reader = new FileReader(fitxer);
        
        //BufferedReader permet fer un .readLine per a llegir un fitxer línea a línea en lloc de caràcter a caràcter.
        BufferedReader buffer = new BufferedReader(reader);

        String linea;

        //Aquest bucle recorre totes les línies d'un fitxer
        while((linea=buffer.readLine()) != null) {
            System.out.println(linea);
            int posSep = linea.indexOf(":");

            int id = Integer.parseInt(linea.substring(0,posSep));
            int unitats = Integer.parseInt(linea.substring(posSep+1));

            String update = "UPDATE Productes SET estoc=estoc+? WHERE id=?";
            PreparedStatement actualitzar = connexioBD.prepareStatement(update);
            actualitzar.setInt(1, unitats);
            actualitzar.setInt(2, id);

            actualitzar.executeUpdate();
            
        }
        //Hem de tancar el FileReader i BufferedReader per a poder moure els fitxers després.
        reader.close();
        buffer.close();
    }

    static void moureFitxerAProcessat(File fitxers) throws IOException {
        FileSystem sistemaFitxers = FileSystems.getDefault();
        Path origen = sistemaFitxers.getPath(PATHPENDENTS + fitxers.getName());
        Path desti = sistemaFitxers.getPath(PATHPROCESSATS + fitxers.getName());

        //Indiquem quin fitxer volem moure i on el volem moure.
        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a processats el fitxer: " + fitxers.getName());
    }

    static void prepararComandes() throws SQLException, IOException {

        System.out.println("Generació de comandes");

        String consulta = "SELECT Pr.id, Pr.nom, Pr.estoc, P.NIF, Prov.nom, Prov.telèfon, Prov.direcció FROM Productes Pr join Proveeix P join Proveïdors Prov on Pr.id = P.id and P.NIF = Prov.NIF where estoc <20 order by NIF";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);

        ResultSet rs = ps.executeQuery();

        int i = 0;
        int in = 0;
        int productes = 0;

        // Mostra tots els productes de la BS amb els seus atributs corresponents
        if (rs.next()) {

            String actproveidor = rs.getString("Prov.nom");
            // Crear el fitxer, que de nom es el nom del proveïdor, la data de avui i sera
            // un fitxer .txt

            CrearFitxer(actproveidor, rs);

            nom_proveidor[i] = actproveidor;
            System.out.println("Array:" + nom_proveidor[i]);
            do {

                if (!actproveidor.equals(rs.getString("Prov.nom"))) {

                    System.out.println("Ha canviat de proveïdor");
                    // Em guardo el nou proveïdor
                    actproveidor = rs.getString("Prov.nom");
                    pw.close();

                    i++;

                    nom_proveidor[i] = actproveidor;
                    System.out.println("Proveidor: " + nom_proveidor[i]);

                    productes = 0;
                    in++;
                    CrearFitxer(actproveidor, rs);
                }

                System.out.print("ID PRODUCTE: " + rs.getInt("id") + " ");
                System.out.print("Estoc restant: " + (300 - rs.getInt("estoc")) + " ");
                System.out.println("NIF proveïdor:  " + rs.getString("NIF") + " ");

                productes++;
                num_productes[in] = productes;

                pw.println("   " + rs.getInt("id") + "\t\t\t" + (300 - rs.getInt("estoc")));
            } while (rs.next());
            pw.close();

        }
    }

    static void CrearFitxer(String actproveidor, ResultSet rs) throws SQLException, IOException {

        // Crear el fitxer, que de nom es el nom del proveïdor, la data de avui i sera
        // un fitxer .txt
        fw = new FileWriter(PATHCOMANDES + actproveidor + LocalDate.now() + ".txt", false);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);

        pw.println("Nom empresa sol·licitant:  " + nom_mevaempresa);
        pw.println(" Informació de la empresa sol·licitant:  " + inf_empresaprov);

        pw.println("\n Nom empresa Proveïdora:  " + actproveidor);
        pw.println("Telèfon de la empresa Proveïdora:  " + rs.getInt("telèfon"));
        pw.println("Direcció de la empresa Proveïdora:  " + rs.getString("direcció"));

        pw.println("\nID producte" + "       " + "Estoc sol·licitant");
    }

    static void analisiComandes() {

        ProductesDemanats();
        MaxProductesDemanats();
        MinProductesDemanats();
        MitjanaProductesDemanats();

    }

    static void ProductesDemanats() {

        for (int i = 0; i < nom_proveidor.length; i++) {
            System.out.println("El proveïdor " + nom_proveidor[i] + " ha sol·licitat " + num_productes[i] + " productes.");
        }

    }

    static void MaxProductesDemanats() {

        int maxim = 0;
        int imaxim = 0;
        for (int i = 0; i < nom_proveidor.length; i++) {

            if (num_productes[i] > maxim) {

                maxim = num_productes[i];
                imaxim = i;
            }

        }
        System.out.println("\n" + "El proveïdor que més productes ha demanat és: " + nom_proveidor[imaxim] + " amb " + maxim + " producte/s");
    }

    static void MinProductesDemanats() {
        int minim = num_productes[0];
        int iminim = 0;
        for (int i = 0; i < nom_proveidor.length; i++) {

            if (num_productes[i] < minim) {

                minim = num_productes[i];
                iminim = i;
            }
        }

        System.out.println("\n" + "El proveïdor que menys productes ha demanat és: " + nom_proveidor[iminim] + " amb " + minim + " producte/s");
    }

    static void MitjanaProductesDemanats() {
        double mitjana = 0;
        double suma = 0;

        for (int i = 0; i < nom_proveidor.length; i++) {

            suma += num_productes[i];
        }
        mitjana = suma / num_productes.length;

        System.out.printf("\n" + "La mitjana de productes sol·licitats és: " + "%.2f", mitjana);

    }

    static void connexioBD() {

        String servidor = "jdbc:mysql://192.168.1.120:3306/";
        String bbdd = "empresa";
        String user = "roger";
        String password = "roger";

        try { // El try intenta fer una connexió amb la base de dades.
            connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
            System.out.println("Connexió amb èxit");
        } catch (SQLException e) { // Si la connexió no funciona executarà el codi de dins del catch.
            e.printStackTrace();
        }

    }

    static void desconnexioBD() throws SQLException {
        connexioBD.close();
    }

}
