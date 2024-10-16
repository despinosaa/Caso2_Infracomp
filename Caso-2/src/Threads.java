public class Threads {
    private ActualizadorReferencias threadActualizador;
    private ActualizadorBitR threadActualizadorR;

    public void iniciarThreads(ActualizadorReferencias actualizador, ActualizadorBitR actualizadorR) {
        this.threadActualizador = actualizador;
        this.threadActualizadorR = actualizadorR;
        threadActualizador.start();
        threadActualizadorR.start();

        try {
            threadActualizador.join();
            threadActualizadorR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
