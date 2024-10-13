import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Escoge una opción:");
        System.out.println("1. Generar Referencias");
        System.out.println("2. Opción 2");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("Ingrese el tamaño de página (en bytes):");
            int pageSize = scanner.nextInt();
            System.out.println("Ingrese el nombre del archivo de la imagen:");
            String imageFileName = scanner.next();
            Imagen imagen = new Imagen(imageFileName);
            generarReferencias(imagen, pageSize);
        } else if (choice == 2) {
            System.out.println("Opción 2");
        } else {
            System.out.println("Opción inválida");
        }
        scanner.close();
    }

    public static void generarReferencias(Imagen imagen, int pageSize) {

        System.out.println("P=" + pageSize);
        System.out.println("NF=" + imagen.alto); 
        System.out.println("NC=" + imagen.ancho);
        System.out.println("NR=" + (imagen.leerLongitud()*17 + 16)); 
        System.out.println("NP=" + ((imagen.alto * imagen.ancho * 3 / pageSize)));

    }
}
