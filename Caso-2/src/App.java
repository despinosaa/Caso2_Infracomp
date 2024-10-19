import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
	int P; // tamanio pagina
	int NF; // numero filas
	int NC; // numero columnas
	int NR; // numero referencias
	int NP; // numero de paginas virtuales

	private int numeroDeMarcos;  
	private List<int[]> referencias;


	// Opción 1: generar las referencias
	public void generarReferencias(Scanner scanner) {
		System.out.println("------------------------------------");
		System.out.println("Tamanio de pagina: ");
		P = scanner.nextInt();
		scanner.nextLine();

		System.out.println("ingresa la Ruta (path) de la imagen: ");
		String input = scanner.nextLine();
		Imagen imagen = new Imagen(input);
		int longitud = imagen.leerLongitud();
		System.out.println("------------------------------------");

		System.out.println("P=" + P);
		char[] vector = new char[longitud];
		imagen.recuperar(vector, longitud);
		NF = imagen.alto;
		NC = imagen.ancho;
		System.out.println("NF=" + imagen.alto);
		System.out.println("NC=" + imagen.ancho);

		NR = (longitud * 17) + 16;
		System.out.println("NR=" + NR);

		double div = (double) longitud / P;
		int res = (int) Math.ceil(div);
		NP = ((imagen.alto * 3 * imagen.ancho) / P) + res;

		System.out.println("NP=" + NP);
		byte[][][] copyImagen = imagen.imagen.clone();

		int longitudMensaje = imagen.leerLongitud();

		char[] colores = { 'R', 'G', 'B' };

		int desplazamiento;
		int desplazamientoMensaje;
		int pagMensaje;
		int posCaracter;
		int bytesTotales = 0;
		pagMensaje = NP - res;
		int bitsCaracter = 0;

		String filePath = "referencias.txt";

		String txtReferencias = "";
		for (int i = 0; i < copyImagen.length; i++) { // fila i
			for (int j = 0; j < copyImagen[0].length; j++) { // columna j
				for (int k = 0; k < copyImagen[0][0].length; k++) { // k = R,G,B

					if (bytesTotales < (longitudMensaje * 8) + 16) {

						if(bytesTotales >= 15 && bytesTotales%8 == 0 && bitsCaracter%8 ==0) {
							posCaracter = bitsCaracter/8;
							int nextDespl = (((imagen.alto * 3 * imagen.ancho) + (bitsCaracter/8)))%P;
							int nextPg = pagMensaje;
							if(nextDespl == 0 && bytesTotales != 16) {
								nextPg++;
							}
							
							txtReferencias += "Mensaje["+posCaracter+"],"+nextPg+","+nextDespl+",W\n";

						}

						int pagina = bytesTotales / P;
						desplazamiento = bytesTotales % P;
						char comp = colores[k % 3];
						txtReferencias += "Imagen[" + i + "][" + j + "]." + comp + "," + pagina + "," + desplazamiento+ ",R\n";

						if(bytesTotales >= 15) {
							posCaracter = bitsCaracter/8;

							if(bytesTotales != 15) {
								desplazamientoMensaje = ((imagen.alto * 3 * imagen.ancho) + (bitsCaracter/8))%P;
							    pagMensaje = ((imagen.alto * 3 * imagen.ancho) + (bitsCaracter/8))/P;
								txtReferencias += "Mensaje["+posCaracter+"],"+pagMensaje+","+desplazamientoMensaje+",W\n";
								bitsCaracter++;
							}
						}

						bytesTotales++;

					}
				}
			}
		}

		try {
			FileWriter fileWriter = new FileWriter(filePath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("P=" + P + " y longitud= " + longitud + "\n");
			bufferedWriter.write("NF=" + NF + "\n");
			bufferedWriter.write("NC=" + NC + "\n");
			bufferedWriter.write("NR=" + NR + "\n");
			bufferedWriter.write("NP=" + NP + "\n");
			bufferedWriter.write(txtReferencias);
			bufferedWriter.close();

			System.out.println("Archivo de Referencias generado correctamente.");
			System.out.println("------------------------------------");


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Opción 1: empezar simulacion 
    public void calcularFallasHitsTiempos(int numeroDeMarcos, String nombreArchivo) {
        cargarReferencias(nombreArchivo);
        Simulador simulador = new Simulador();
        simulador.iniciarSimulacion(numeroDeMarcos, referencias);
    }

    private void cargarReferencias(String nombreArchivo) {
		referencias = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] partes = linea.split(",");
				if (partes.length > 1 && !linea.startsWith("P=") && !linea.startsWith("NF=") &&
						!linea.startsWith("NC=") && !linea.startsWith("NR=") && !linea.startsWith("NP=")) {
					int pagina = Integer.parseInt(partes[1].trim());
					int esEscritura = 0;
					if (partes[3].trim().equals("W")) {
						esEscritura = 1;
					}
					referencias.add(new int[]{pagina, esEscritura});
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
		boolean continuar = true;
		boolean referenciasGeneradas = false;
		App main = new App();
	
		while (continuar) {
			System.out.println("-- Menu de opciones --");
			Scanner scanner = new Scanner(System.in);
			System.out.println("Elija una opción:");
			System.out.println("1. Generar Referencias.");
			System.out.println("2. Calcular número de fallas de página, porcentaje de hits, tiempos.");
			System.out.println("3. Cargar imagen y esconder mensaje.");
			System.out.println("4. Recuperar mensaje de imagen.");
			System.out.println("5. Salir.");
	
			int opcion = scanner.nextInt();
			scanner.nextLine();
	
			if (opcion == 1) {
				// Opción 1: Generar referencias de página
				main.generarReferencias(scanner);
				referenciasGeneradas = true;
	
			} else if (opcion == 2) {
				// Opción 2: Calcular numero de fallas de página, hits, tiempos
				if (!referenciasGeneradas) {
					System.out.println("Primero debe generar las referencias en la opción 1.");
				} else {
					System.out.println("Ingrese el numero de marcos de pagina: ");
        			int numeroDeMarcos = scanner.nextInt();
        			scanner.nextLine(); 
        			System.out.println("Ingrese el nombre del archivo de referencias: ");
        			String nombreArchivo = scanner.nextLine();
        			main.calcularFallasHitsTiempos(numeroDeMarcos, nombreArchivo);
				}
			} else if (opcion == 3) {
				System.out.println("Ingrese la ruta de la imagen: ");
				String input = scanner.nextLine();
				Imagen imagen = new Imagen(input);
				System.out.println("Ingrese el mensaje a esconder: ");
				String mensaje = scanner.nextLine();
				imagen.esconder(mensaje.toCharArray(), mensaje.length());
				imagen.escribirImagen("imagen_con_mensaje.bmp");
				System.out.println("Imagen con mensaje guardada en imagen_con_mensaje.bmp");
			} else if (opcion == 4) {
				System.out.println("Ingrese la ruta de la imagen: ");
				String input = scanner.nextLine();
				Imagen imagen = new Imagen(input);
				int longitud = imagen.leerLongitud();
				System.out.println("Longitud del mensaje: " + longitud);
				char[] mensaje = new char[longitud];
				imagen.recuperar(mensaje, longitud);
				System.out.println("Mensaje recuperado: " + new String(mensaje));
			} else if (opcion == 5) {
				continuar = false;
				scanner.close();
			}else {
				System.out.println("Opción no válida. Por favor, elija 1 o 2.");
			}
		}
	}
}