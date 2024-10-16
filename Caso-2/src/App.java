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

	public void ejecutarSimulacion(int numeroDeMarcos, String archivoReferencias) {
		Memoria memoria = new Memoria(numeroDeMarcos);
		Swap swap = new Swap();
		Threads threads = new Threads();
	
		List<Integer> referencias = cargarReferencias(archivoReferencias);
		ActualizadorReferencias actualizador = new ActualizadorReferencias(memoria, swap, referencias);
		ActualizadorBitR actualizadorR = new ActualizadorBitR(memoria);
		threads.iniciarThreads(actualizador, actualizadorR);

		int hits = actualizador.getHits();
		int fallas = actualizador.getFallas();
		double porcentajeHits = (hits * 100.0) / referencias.size();
		double porcentajeFallas = (fallas * 100.0) / referencias.size();
		long tiempoTotal = (hits * 25 + fallas * 10000000) / 1000000;
		long tiempoSiTodoEnRAM = (referencias.size() * 25) / 1000000;
		long tiempoSiTodoEnSWAP = referencias.size() * 10;
	
		System.out.println("------------------------------------");
		System.out.println("Numero de marcos de pagina: " + numeroDeMarcos);
		System.out.println("Total de referencias: " + referencias.size());
		System.out.println("Total de hits: " + hits);
		System.out.printf("Porcentaje de hits: %.2f%%\n", porcentajeHits);
		System.out.println("Numero de fallas: " + fallas);
		System.out.printf("Porcentaje de fallas: %.2f%%\n", porcentajeFallas); 
		System.out.println("Tiempo total con hits y fallas: " + tiempoTotal + " ms");
		System.out.println("Tiempo si todas las referencias estuvieran en RAM: " + tiempoSiTodoEnRAM + " ms");
		System.out.println("Tiempo si todas las referencias fueran fallas de pagina: " + tiempoSiTodoEnSWAP + " ms");
	}
	

	private List<Integer> cargarReferencias(String archivoReferencias) {
    	List<Integer> referencias = new ArrayList<>();
    	try (BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] partes = linea.split(",");
				if (partes.length > 1 && !linea.startsWith("P=") && !linea.startsWith("NF=") &&
					!linea.startsWith("NC=") && !linea.startsWith("NR=") && !linea.startsWith("NP=")) {
					int pagina = Integer.parseInt(partes[1].trim());
					referencias.add(pagina);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return referencias;
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
			System.out.println("3. Salir");
	
			int opcion = scanner.nextInt();
			scanner.nextLine();
	
			if (opcion == 1) {
				// Opción 1: Generar referencias de página
				main.generarReferencias(scanner);
				referenciasGeneradas = true;
			} else if (opcion == 2) {
				// Opción 2: Calcular número de fallas de página, hits, tiempos
				if (!referenciasGeneradas) {
					System.out.println("Primero debe generar las referencias en la opción 1.");
				} else {
					System.out.print("Ingrese el numero de marcos de pagina: ");
                	int numeroDeMarcos = scanner.nextInt();
					scanner.nextLine(); 
                	System.out.print("Ingrese el nombre del archivo de referencias: ");
                	String archivoReferencias = scanner.nextLine();
                	main.ejecutarSimulacion(numeroDeMarcos, archivoReferencias);
				}
			} else if (opcion == 3) {
				continuar = false;
				scanner.close();
			} else {
				System.out.println("Opción no válida. Por favor, elija 1 o 2.");
			}
		}
	}
}