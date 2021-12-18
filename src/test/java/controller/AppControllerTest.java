package controller;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class AppControllerTest {
    /*
     * Para los siguientes test, se va a suponer que los datos obtenidos son escritos correctamente en CSV,
     * por lo que estos test se enfocan en los datos pasados a la clase User desde los datasets.
     */
    private AppController controller;
    private DateFormat dateFormat;      //formatea Date a String
    private String esperado;
    private String obtenido;
    private String arrEsperado[];
    private String arrObtenido[];

    @BeforeEach
    //Se van a utilizar registros a modo de prueba, una cabecera y dos usuarios, de los dataset 1 y 2
    void setUp() {
        controller = new AppController();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        controller.getRecordsDataset1().add(Arrays.asList("id","correo","ultima_conexion","siguiendo"));
        controller.getRecordsDataset1().add(Arrays.asList("1","ivan.kononova@hotmail.com","2021-03-15",
                                                          "29542,458,18022,12696,13482"));
        controller.getRecordsDataset1().add(Arrays.asList("2","luismiguel90@yahoo.com","2018-02-19",
                                                          "1228,9598,19096,13068"));

        controller.getRecordsDataset2().add(Arrays.asList("id","correo","ultima_conexion","siguiendo"));
        controller.getRecordsDataset2().add(Arrays.asList("1","ivan.kononova@hotmail.com","2016-06-26",
                                                          "11093,7187,23730"));
        controller.getRecordsDataset2().add(Arrays.asList("2","luismiguel90@yahoo.com","2015-09-20",
                                                          "2267,25455,6095,18543"));
    }

    @AfterEach
    void tearDown() {
        controller = null;
        dateFormat = null;
        esperado = null;
        obtenido = null;
        arrEsperado = null;
        arrObtenido = null;
    }

    @Test
    void testDesafio1_user1() {
        controller.generateUsers();
        controller.combineRecordsIdCorreo();
        esperado = "1";   //ID del usuario 1 en el dataset1
        obtenido = String.valueOf(controller.getUsers().get(0).getId());    //ID del usuario 1 en User
        assertEquals(esperado,obtenido);    //Los ID se mantienen

        esperado = "ivan.kononova@hotmail.com";   //Correo del usuario 1 en el dataset1
        obtenido = controller.getUsers().get(0).getCorreo();    //Correo del usuario 1 en User
        assertEquals(esperado,obtenido);    //Los correos se mantienen
    }

    @Test
    void testDesafio1_user2() {
        controller.generateUsers();
        controller.combineRecordsIdCorreo();
        esperado = "2";   //ID del usuario 2 en el dataset1
        obtenido = String.valueOf(controller.getUsers().get(1).getId());    //ID del usuario 2 en User
        assertEquals(esperado,obtenido);    //Los ID se mantienen

        esperado = "luismiguel90@yahoo.com";   //Correo del usuario 2 en el dataset1
        obtenido = controller.getUsers().get(1).getCorreo();    //Correo del usuario 2 en User
        assertEquals(esperado,obtenido);    //Los correos se mantienen
    }

    @Test
    void testDesafio2_user1() {
        controller.generateUsers();
        controller.combineRecordsUltimaConexion();
        esperado = "2021-03-15";   //Fecha más reciente entre dataset1 y dataset2 del usuario 1
        obtenido = dateFormat.format(controller.getUsers().get(0).getUltima_conexion()); //Fecha en User del usuario 1
        assertEquals(esperado,obtenido);    //Se conserva la fecha de última conexión más reciente
    }

    @Test
    void testDesafio2_user2() {
        controller.generateUsers();
        controller.combineRecordsUltimaConexion();
        esperado = "2018-02-19";   //Fecha más reciente entre dataset1 y dataset2 del usuario 2
        obtenido = dateFormat.format(controller.getUsers().get(1).getUltima_conexion()); //Fecha en User del usuario 2
        assertEquals(esperado,obtenido);    //Se conserva la fecha de última conexión más reciente
    }

    @Test
    void testDesafio3_user1() {
        //se generan 30000 registros de prueba necesarios para testear los seguidos
        for (int i = 3; i <= 30000; i++) {
            controller.getRecordsDataset1().add(Arrays.asList(String.valueOf(i),"a","a","3"));
            controller.getRecordsDataset2().add(Arrays.asList(String.valueOf(i),"a","a","3"));
        }
        controller.generateUsers();
        controller.combineRecordsIdCorreo();
        controller.combineRecordsSeguidos();

        esperado = "458,7187,11093,12696,13482,18022,23730,29542"; //Combinación IDs seguidos ordenados del usuario 1
        obtenido = "";

        for (User seguido : controller.getUsers().get(0).getSeguidos())   //se registran los id seguidos del usuario 1
            obtenido += (seguido.getId() + ",");
        obtenido = obtenido.substring(0, obtenido.length() - 1);    //se elimina la última coma
        assertEquals(esperado,obtenido);    //IDs seguidos están combinados y ordenados de menor a mayor
    }

    @Test
    void testDesafio3_user2() {
        //se generan 30000 registros de prueba necesarios para testear los seguidos
        for (int i = 3; i <= 30000; i++) {
            controller.getRecordsDataset1().add(Arrays.asList(String.valueOf(i),"a","a","3"));
            controller.getRecordsDataset2().add(Arrays.asList(String.valueOf(i),"a","a","3"));
        }
        controller.generateUsers();
        controller.combineRecordsIdCorreo();
        controller.combineRecordsSeguidos();

        esperado = "1228,2267,6095,9598,13068,18543,19096,25455"; //Combinación IDs seguidos ordenados del usuario 2
        obtenido = "";

        for (User seguido : controller.getUsers().get(1).getSeguidos())   //se registran los id seguidos del usuario 2
            obtenido += (seguido.getId() + ",");
        obtenido = obtenido.substring(0, obtenido.length() - 1);    //se elimina la última coma
        assertEquals(esperado,obtenido);    //IDs seguidos están combinados y ordenados de menor a mayor
    }

    @Test
    void testDesafio4_header() {
        //se testea si el header del objeto AppController es el mismo que el header del dataset original
        controller.setHeaderFromDataset();
        arrEsperado = new String[]{"id","correo","ultima_conexion","siguiendo"};
        arrObtenido = controller.getHeader().toArray(new String[0]); //se convierte el arraylist a array estático
        assertArrayEquals(arrEsperado,arrObtenido);    //el header del controller contiene el mismo formato que el original
    }

    @Test
    void testDesafio4_user1() {
        //se generan 30000 registros de prueba necesarios para el test
        for (int i = 3; i <= 30000; i++) {
            controller.getRecordsDataset1().add(Arrays.asList(String.valueOf(i),"a","01-01-2019","3"));
            controller.getRecordsDataset2().add(Arrays.asList(String.valueOf(i),"a","01-01-2015","3"));
        }
        controller.combineRecordsDatasetIntoUsers();

        //se espera mismo formato del dataset original, al testear usuario 1
        arrEsperado = new String[]{"1","ivan.kononova@hotmail.com","2021-03-15",
                                   "458,7187,11093,12696,13482,18022,23730,29542"};
        arrObtenido = new String[4];
        arrObtenido[0] = String.valueOf(controller.getUsers().get(0).getId());  //id
        arrObtenido[1] = controller.getUsers().get(0).getCorreo();   //correo
        arrObtenido[2] = dateFormat.format(controller.getUsers().get(0).getUltima_conexion());  //ultima_conexion
        arrObtenido[3] = "";

        for (User seguido : controller.getUsers().get(0).getSeguidos())   //se registran los id seguidos del usuario 1
            arrObtenido[3] += (seguido.getId() + ",");
        arrObtenido[3] = arrObtenido[3].substring(0, arrObtenido[3].length() - 1);    //se elimina la última coma

        assertArrayEquals(arrEsperado,arrObtenido);    //IDs seguidos están combinados y ordenados de menor a mayor
    }

    @Test
    void testDesafio4_user2() {
        //se generan 30000 registros de prueba necesarios para el test
        for (int i = 3; i <= 30000; i++) {
            controller.getRecordsDataset1().add(Arrays.asList(String.valueOf(i),"a","01-01-2019","3"));
            controller.getRecordsDataset2().add(Arrays.asList(String.valueOf(i),"a","01-01-2015","3"));
        }
        controller.combineRecordsDatasetIntoUsers();

        //se espera mismo formato del dataset original, al testear usuario 2
        arrEsperado = new String[]{"1","ivan.kononova@hotmail.com","2021-03-15",
                "458,7187,11093,12696,13482,18022,23730,29542"};
        arrObtenido = new String[4];
        arrObtenido[0] = String.valueOf(controller.getUsers().get(0).getId());  //id
        arrObtenido[1] = controller.getUsers().get(0).getCorreo();   //correo
        arrObtenido[2] = dateFormat.format(controller.getUsers().get(0).getUltima_conexion());  //ultima_conexion
        arrObtenido[3] = "";

        for (User seguido : controller.getUsers().get(0).getSeguidos())   //se registran los id seguidos del usuario 2
            arrObtenido[3] += (seguido.getId() + ",");
        arrObtenido[3] = arrObtenido[3].substring(0, arrObtenido[3].length() - 1);    //se elimina la última coma

        assertArrayEquals(arrEsperado,arrObtenido);    //IDs seguidos están combinados y ordenados de menor a mayor
    }
}