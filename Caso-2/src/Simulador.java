import java.util.List;

public class Simulador {
    private RAM ram;
    private TablaDePaginas tablaDePaginas;
    private List<int[]> referencias;

    public void iniciarSimulacion(int numeroDeMarcos, List<int[]> referencias) {
        this.ram = new RAM(numeroDeMarcos);
        this.tablaDePaginas = new TablaDePaginas();
        this.referencias = referencias;

        ActualizadorReferencias actualizadorReferencias = new ActualizadorReferencias(referencias, ram, tablaDePaginas);
        ActualizadorBitR actualizadorBitR = new ActualizadorBitR(ram);
        actualizadorReferencias.start();
        actualizadorBitR.start();

        try {
            actualizadorReferencias.join();
            actualizadorBitR.detener();
            actualizadorBitR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int totalReferencias = referencias.size();
        int hits = ram.getHits();
        int fallas = ram.getFallas();
        double porcentajeHits = (hits * 100.0) / totalReferencias;
        double porcentajeFallas = (fallas * 100.0) / totalReferencias;
        long tiempoTotal = (hits * 25 + fallas * 10000000) / 1000000;
        long tiempoSiTodoEnRAM = (totalReferencias * 25) / 1000000;
        long tiempoSiTodoEnSWAP = (totalReferencias * 10);

        System.out.println("Numero de marcos de pagina: " + numeroDeMarcos);
        System.out.println("Total de referencias: " + totalReferencias);
        System.out.println("Total de hits: " + hits);
        System.out.printf("Porcentaje de hits: %.2f%%\n", porcentajeHits);
        System.out.println("Numero de fallas: " + fallas);
        System.out.printf("Porcentaje de fallas: %.2f%%\n", porcentajeFallas);
        System.out.println("Tiempo total con hits y fallas: " + tiempoTotal + " ms");
        System.out.println("Tiempo si todas las referencias estuvieran en RAM: " + tiempoSiTodoEnRAM + " ms");
        System.out.println("Tiempo si todas las referencias fueran fallas de pagina: " + tiempoSiTodoEnSWAP + " ms");
        System.out.println("------------------------------------");
    }
}
