import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Memoria {
    private List<Integer> marcos;
    private int numeroDeMarcos;
    private Map<Integer, Long> tablaPaginas;

    public Memoria(int numeroDeMarcos) {
        this.numeroDeMarcos = numeroDeMarcos;
        this.marcos = new ArrayList<>();
        this.tablaPaginas = new LinkedHashMap<>();
    }

    public synchronized boolean contienePagina(int pagina) {
        return tablaPaginas.containsKey(pagina);
    }

    public synchronized void cargarPagina(int pagina) {
        if (marcos.size() < numeroDeMarcos) {
            marcos.add(pagina);
        } else {
            reemplazarPagina(pagina);
        }
        tablaPaginas.put(pagina, actualizarContador(0, true));
    }

    public synchronized void actualizarPagina(int pagina) {
        if (tablaPaginas.containsKey(pagina)) {
            tablaPaginas.put(pagina, actualizarContador(tablaPaginas.get(pagina), true));
        }
    }

    public synchronized long actualizarContador(long contador, boolean acceso) {
        contador >>= 1;
        if (acceso) {
            contador |= (1L << 31); 
        }
        return contador;
    }

    private synchronized void reemplazarPagina(int nuevaPagina) {
        Integer quitar = marcos.get(0); 
        marcos.remove(0);
        tablaPaginas.remove(quitar);
        marcos.add(nuevaPagina);
        tablaPaginas.put(nuevaPagina, actualizarContador(0, true));
    }

    public synchronized int getNumeroDePaginas() {
        return marcos.size();
    }

    public Map<Integer, Long> getTablaPaginas() {
        return tablaPaginas;
    }
}
