public class PaginaReal {
    private final PaginaVirtual paginaVirtual;
    private int bitReferencia;
    private int bitModificacion; 

    public PaginaReal(PaginaVirtual paginaVirtual) {
        this.paginaVirtual = paginaVirtual;
        this.bitReferencia = 1;
        if (paginaVirtual.esEscritura()) {
            this.bitModificacion = 1;
        } else {
            this.bitModificacion = 0;
        }
    }

    public PaginaVirtual getPaginaVirtual() {
        return paginaVirtual;
    }

    public int getBitReferencia() {
        return bitReferencia;
    }

    public void setBitReferencia(int bitReferencia) {
        this.bitReferencia = bitReferencia;
    }

    public int getBitModificacion() {
        return bitModificacion;
    }

    public void setBitModificacion(int bitModificacion) {
        this.bitModificacion = bitModificacion;
    }
}
