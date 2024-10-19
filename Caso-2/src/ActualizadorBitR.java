public class ActualizadorBitR extends Thread {
    private final RAM ram;

    public ActualizadorBitR(RAM ram) {
        this.ram = ram;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) { 
            synchronized (ram) {
                try {
                    for (PaginaReal pagina : ram.obtenerMarcos()) {
                        pagina.setBitReferencia(0);
                    }
                    Thread.sleep(2); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
