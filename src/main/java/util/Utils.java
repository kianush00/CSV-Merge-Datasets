package util;

import com.opencsv.*;
import model.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {
    /*
     * In this part you have to put some CSV processing.
     * Check this out: https://zetcode.com/java/opencsv/
     * Official documentation: http://opencsv.sourceforge.net/apidocs/index.html
     */

    public Utils() {
    }

    public List<List<String>> parseCSVFileIntoArray(String folderName, int fileIndex){
        File folder = new File(folderName);     //directorio datasets
        File datasetFile = null;        //archivo dataset .csv
        List<List<String>> records = new ArrayList<>();     //registros de dataset
        String[] values;    //columnas de cada registro (se va actualizando en cada iteración)

        try {
            if(Objects.requireNonNull(folder.listFiles())[fileIndex].getName().contains(".csv"))
                //se registra el archivo .csv ubicado en el index pasado por parámetro
                datasetFile = Objects.requireNonNull(folder.listFiles())[fileIndex];

            FileReader fileReader = new FileReader(Objects.requireNonNull(datasetFile));
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader reader = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();

            while ((values = reader.readNext()) != null) {  //se van añadiendo los registros dentro del while
                records.add(Arrays.asList(values));
            }

            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return records;
    }

    public void writeFileDatos(ArrayList<User> users, String fileNameDatos, List<String> header){
        File file = new File(fileNameDatos);     //archivo datos.csv
        String[] values = new String[4];    //valores de cada registro
        String[] arrHeader = header.toArray(new String[0]);  //se convierte el arraylist a array estático
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   //formatea Date a String

        try {
            FileWriter fileWriter = new FileWriter(file);
            ICSVWriter writer = new CSVWriterBuilder(fileWriter).withSeparator(';')
                                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();
            writer.writeNext(arrHeader);

            for (User user : users) {
                values[0] = Integer.toString(user.getId());
                values[1] = user.getCorreo();
                values[2] = dateFormat.format(user.getUltima_conexion());  //ultima_conexion a String
                values[3] = "";    //se actualiza values[3] en cada iteración

                for (User seguido : user.getSeguidos())    //se añaden los id de los seguidos de user
                    values[3] += (seguido.getId() + ",");
                values[3] = eliminarUltimaComa(values[3]);

                writer.writeNext(values);
            }

            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeFileResultados(ArrayList<User> users, String fileNameResultados){
        File file = new File(fileNameResultados);     //archivo datos.csv

        try {
            FileWriter fileWriter = new FileWriter(file);
            ICSVWriter writer = new CSVWriterBuilder(fileWriter).withSeparator(';')
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build();

            writeMatricula(writer);
            writeUsuariosInactivos(writer,users);
            writeUsuariosSiguiendo50PRCInactivos(writer,users);
            writeUsuariosConMasSeguidores(writer,users);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeMatricula(ICSVWriter writer){
        writer.writeNext(new String[]{"### MATRÍCULA ###"});
        writer.writeNext(new String[]{"20583946119"});
    }

    private void writeUsuariosInactivos(ICSVWriter writer, ArrayList<User> users){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   //formatea Date a String
        String[] values = new String[3];    //valores de cada registro

        writer.writeNext(new String[]{"### INICIO USUARIOS INACTIVOS ###"});

        for (User user: users) {
            // Si el usuario es inactivo, se escribe el registro con formato "id;correo;ultima_conexion"
            if(user.isInactivo()){
                values[0] = Integer.toString(user.getId());
                values[1] = user.getCorreo();
                values[2] = dateFormat.format(user.getUltima_conexion());
                writer.writeNext(values);
            }
        }

        writer.writeNext(new String[]{"### FIN USUARIOS INACTIVOS ###"});
    }

    private void writeUsuariosSiguiendo50PRCInactivos(ICSVWriter writer, ArrayList<User> users){
        int acum;   //acumulador
        String[] values = new String[3];    //valores de cada registro

        writer.writeNext(new String[]{"### INICIO USUARIOS SIGUIENDO 50% O MÁS INACTIVOS ###"});

        for (User user: users) {
            acum = 0;
            values[2] = "";
            for (User seguido: user.getSeguidos()) {
                if (seguido.isInactivo()){
                    values[2] += (seguido.getId() + ",");
                    acum++;
                }
            }
            values[2] = eliminarUltimaComa(values[2]);

            if (user.getSeguidos().size() != 0) {
                if (((acum * 100) / user.getSeguidos().size()) >= 50){ //si el usuario sigue >= 50% inactivos
                    values[0] = Integer.toString(user.getId());
                    values[1] = user.getCorreo();
                    writer.writeNext(values);
                }
            }
        }

        writer.writeNext(new String[]{"### FIN USUARIOS SIGUIENDO 50% O MÁS INACTIVOS ###"});
    }

    private void writeUsuariosConMasSeguidores(ICSVWriter writer, ArrayList<User> users){
        int acum;   //acumulador
        int acumMax = 0;
        String[] values = new String[3];    //valores de cada registro

        writer.writeNext(new String[]{"### INICIO USUARIOS CON MÁS SEGUIDORES ###"});

        for (User user: users) {
            acum = user.getSeguidores().size();
            if (acum > acumMax)
                acumMax = acum;
        }
        for (User user: users) {
            if (user.getSeguidores().size() == acumMax) {
                values[0] = Integer.toString(user.getId());
                values[1] = user.getCorreo();
                values[2] = Integer.toString(acumMax);
                writer.writeNext(values);
            }
        }

        writer.writeNext(new String[]{"### FIN USUARIOS CON MÁS SEGUIDORES ###"});
    }

    private String eliminarUltimaComa(String value){
        if (value.length() > 0)    //se elimina la última coma
            return value.substring(0, value.length() - 1);
        else return "";
    }
}