package controller;

import model.User;
import org.apache.commons.lang3.ArrayUtils;
import util.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppController {
    /*
     * In this class you have to program decisions about your entire solution.
     */
    private ArrayList<User> users;
    private List<List<String>> recordsDataset1;  //cada elemento del array contiene un registro de tipo array
    private List<List<String>> recordsDataset2;  // " "
    private List<String> header;

    public AppController() {
        users = new ArrayList<>();
        recordsDataset1 = new ArrayList<>();
        recordsDataset2 = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public List<List<String>> getRecordsDataset1() {
        return recordsDataset1;
    }

    public void setRecordsDataset1(List<List<String>> recordsDataset1) {
        this.recordsDataset1 = recordsDataset1;
    }

    public List<List<String>> getRecordsDataset2() {
        return recordsDataset2;
    }

    public void setRecordsDataset2(List<List<String>> recordsDataset2) {
        this.recordsDataset2 = recordsDataset2;
    }

    public List<String> getHeader() {
        return header;
    }

    public void readRecordsDataset(String folderName){
        Utils utils = new Utils();
        this.recordsDataset1 = utils.parseCSVFileIntoArray(folderName,0);   //0 -> dataset1.csv
        this.recordsDataset2 = utils.parseCSVFileIntoArray(folderName,1);  //1 -> dataset2.csv
    }

    public void combineRecordsDatasetIntoUsers(){
        if (recordsDataset1.size() == recordsDataset2.size()){
            setHeaderFromDataset();
            generateUsers();
            combineRecordsIdCorreo();
            combineRecordsUltimaConexion();
            combineRecordsSeguidos();
            clearRecordsDataset();
        } else {
            System.err.println("Los archivos .csv recibidos contienen distinto orden de registros.");
        }
    }

    public void generateDatosCsv(String fileNameDatos){
        Utils utils = new Utils();
        utils.writeFileDatos(users,fileNameDatos,header);
    }

    public void generateResultadosTxt(String fileNameResultados){
        Utils utils = new Utils();
        utils.writeFileResultados(users,fileNameResultados);
    }

    public void setHeaderFromDataset(){
        if (recordsDataset1.get(0).equals(recordsDataset2.get(0))) {
            this.header = recordsDataset1.get(0);
        } else {
            System.err.println("Los headers de los archivos .csv recibidos son distintos.");
        }
    }

    public void generateUsers(){
        for (int i = 1; i < recordsDataset1.size(); i++)
            users.add(new User());
    }

    public void combineRecordsIdCorreo(){
        for (int i = 1; i < recordsDataset1.size(); i++) {
            // si el id del 1er registro coincide con el id del 2do registro, se combinan id y correo
            // guardándose en array "users"
            if (recordsDataset1.get(i).get(0).equals(recordsDataset2.get(i).get(0))){
                users.get(i-1).setId(Integer.parseInt(recordsDataset1.get(i).get(0))); //se combina id
                users.get(i-1).setCorreo(recordsDataset1.get(i).get(1));  //se combina correo
            }
        }
    }

    public void combineRecordsUltimaConexion(){
        Date fecha1;    //variable temporal que guarda la fecha ultima_conexion del registro 1
        Date fecha2;    //" " del registro 2

        try {
            for (int i = 1; i < recordsDataset1.size(); i++) {
                fecha1 = parseStringToFecha(recordsDataset1.get(i).get(2));
                fecha2 = parseStringToFecha(recordsDataset2.get(i).get(2));
                //se guarda la fecha de ultima_conexion más reciente entre los dos registros, además
                //se calcula si tal fecha es inactiva
                if (fecha1.after(fecha2))
                    setFechaIntoUsers(fecha1,i);
                else
                    setFechaIntoUsers(fecha2,i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combineRecordsSeguidos(){
        int[] seguidos1;    //variable temporal que guarda las ID de los seguidos del registro 1
        int[] seguidos2;    // " " del registro 2
        int[] sortedSeguidos;    //combinación ordenada de seguidos1 y seguidos2

        for (int i = 1; i < recordsDataset1.size(); i++) {
            seguidos1 = parseStrSeguidosToIntArray(recordsDataset1.get(i).get(3));
            seguidos2 = parseStrSeguidosToIntArray(recordsDataset2.get(i).get(3));
            sortedSeguidos = combineAndSort(seguidos1,seguidos2);

            for (int sortedSeguido : sortedSeguidos) {
                addSeguidosSeguidoresIntoUsers(sortedSeguido,i);
            }
        }
    }

    private Date parseStringToFecha(String strFecha) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(strFecha);
    }

    private void setFechaIntoUsers(Date fecha, int iterador){
        this.users.get(iterador-1).setUltima_conexion(fecha);
        this.users.get(iterador-1).setInactivo(fechaEsInactiva(fecha));
    }

    private boolean fechaEsInactiva(Date fecha){
        //fecha inactiva: 30-08-2019
        Date fechaInactiva = new GregorianCalendar(2019, Calendar.AUGUST,30).getTime();
        return !fecha.after(fechaInactiva);
    }

    private int[] parseStrSeguidosToIntArray(String strSeguidos){
        return Arrays.stream(strSeguidos.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private int[] combineAndSort(int[] arrayA, int[] arrayB){
        int[] arrayC = ArrayUtils.addAll(arrayA,arrayB);   //combinación de A y B, luego se hará sort
        int in, out, temp;    //punteros

        for(out=1; out<arrayC.length; out++) {
            temp = arrayC[out];
            in = out;
            while(in>0 && arrayC[in-1] >= temp)  // bucle hasta que llegue a un elemento menor que temp
            {
                arrayC[in] = arrayC[in-1];  // mueve el elemento a la derecha
                --in;  // retrocede puntero
            }
            arrayC[in] = temp;   // se inserta temp en su pos. ordenada
        }

        return arrayC;
    }

    private void addSeguidosSeguidoresIntoUsers(int idSeguido, int iterator){
        this.users.get(iterator - 1).getSeguidos().add(users.get(idSeguido - 1));    //add seguido
        this.users.get(idSeguido - 1).getSeguidores().add(users.get(iterator - 1));   //add seguidor
    }

    private void clearRecordsDataset() {
        this.recordsDataset1.clear();
        this.recordsDataset2.clear();
    }
}