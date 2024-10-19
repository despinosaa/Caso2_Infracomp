import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class RAM {
    private final Map<Integer, PaginaReal> marcos = new LinkedHashMap<>();
    private final int numeroDeMarcos;
    private int hits;
    private int fallas;

    public RAM(int numeroDeMarcos) {
        this.numeroDeMarcos = numeroDeMarcos;
        this.hits = 0;
        this.fallas = 0;
    }

    public synchronized void agregarPagina(PaginaReal paginaReal) {
        if (marcos.size() >= numeroDeMarcos) {
            reemplazarPagina(paginaReal);
        } else {
            marcos.put(paginaReal.getPaginaVirtual().getNumero(), paginaReal);
        }
    }

    public synchronized void reemplazarPagina(PaginaReal nuevaPagina) {
        Integer paginaAReemplazar = null;
        PaginaReal bits = null;
        for (Map.Entry<Integer, PaginaReal> entry : marcos.entrySet()) {
            PaginaReal pagina = entry.getValue();
            int numeroPagina = entry.getKey();
            if (pagina.getBitReferencia() == 0 && pagina.getBitModificacion() == 0) {
                paginaAReemplazar = numeroPagina;
                break; 
            }
            else if (pagina.getBitReferencia() == 0 && pagina.getBitModificacion() == 1) {
                if (paginaAReemplazar == null || (bits.getBitReferencia() == 1 || bits.getBitModificacion() == 1)) {
                    paginaAReemplazar = numeroPagina;
                    bits = pagina;
                }
            }
            else if (pagina.getBitReferencia() == 1 && pagina.getBitModificacion() == 0) {
                if (paginaAReemplazar == null || (bits.getBitReferencia() == 1 && bits.getBitModificacion() == 1)) {
                    paginaAReemplazar = numeroPagina;
                    bits = pagina;
                }
            }
            else if (paginaAReemplazar == null) {
                paginaAReemplazar = numeroPagina;
                bits = pagina;
            }
        }
        if (paginaAReemplazar != null) {
            marcos.remove(paginaAReemplazar);
        }
        nuevaPagina.setBitReferencia(1);
        if (nuevaPagina.getPaginaVirtual().esEscritura()) {
            nuevaPagina.setBitModificacion(1);
        }        
        marcos.put(nuevaPagina.getPaginaVirtual().getNumero(), nuevaPagina);
    }
    

    public int getHits() {
        return hits;
    }

    public int getFallas() {
        return fallas;
    }

    public void incrementarHits() {
        this.hits++;
    }

    public void incrementarFallas() {
        this.fallas++;
    }

    public Collection<PaginaReal> obtenerMarcos() {
        return marcos.values();
    }
}
