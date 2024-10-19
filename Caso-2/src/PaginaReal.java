public class PaginaReal {
    private final PaginaVirtual paginaV;
    private int bitR;
    private int bitM; 

    public PaginaReal(PaginaVirtual paginaV) {
        this.paginaV = paginaV;
        this.bitR = 1;
        if (paginaV.esEscritura()) {
            this.bitM = 1;
        } else {
            this.bitM = 0;
        }
    }

    public PaginaVirtual getPaginaVirtual() {
        return paginaV;
    }

    public int getBitReferencia() {
        return bitR;
    }

    public void setBitReferencia(int bit) {
        this.bitR = bit;
    }

    public int getBitModificacion() {
        return bitM;
    }

    public void setBitModificacion(int bit) {
        this.bitM = bit;
    }
}
