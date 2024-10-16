import java.util.Map;

public class ActualizadorBitR extends Thread {
    private final Memoria memoria;
    private boolean continuar = true;

    public ActualizadorBitR(Memoria memoria) {
        this.memoria = memoria;
    }

    @Override
    public void run() {
        while (continuar) {
            synchronized (memoria) {
                for (Map.Entry<Integer, Long> entry : memoria.getTablaPaginas().entrySet()) {
                    long contador = entry.getValue();
                    long nuevoContador = memoria.actualizarContador(contador, false);
                    memoria.getTablaPaginas().put(entry.getKey(), nuevoContador);
                }
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continuar = false;
                break;
            }
        }
    }

}
