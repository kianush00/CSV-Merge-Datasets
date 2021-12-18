package view;

import controller.AppController;

public class CommandLineProcess {
    /*
     * In this class, you could code some processing about command line parameters
     */

    public CommandLineProcess() {
    }

    public void processParameters(String[] array){
        AppController controller = new AppController();

        controller.readRecordsDataset(array[0]);
        System.out.println("Registros leidos correctamente...");

        controller.combineRecordsDatasetIntoUsers();
        controller.generateDatosCsv(array[1]);
        System.out.println("Archivo datos.csv generado exitosamente...");

        controller.generateResultadosTxt(array[2]);
        System.out.println("Archivo resultados.txt generado exitosamente...");
    }

}