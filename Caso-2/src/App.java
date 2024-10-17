import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
	int P; // tamanio pagina
	int NF; // numero filas
	int NC; // numero columnas
	int NR; // numero referencias
	int NP; // numero de paginas virtuales

	private int numeroDeMarcos;  
    private List<Integer> referencias;  
    private int hits;  
    private int fallas; 
	private final Map<Integer, Long> memoria = new LinkedHashMap<>();
	private final List<Integer> swap = new ArrayList<>();
	private volatile boolean x = true;


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

    // Opción 2: calcular número de fallas de página, porcentaje de hits y tiempos
    public void calcularFallasHitsTiempos(int numeroDeMarcos, String nombreArchivo) {
        System.out.println("------------------------------------");
        this.numeroDeMarcos = numeroDeMarcos;
        cargarReferencias(nombreArchivo);
        hits = 0;
        fallas = 0;

        Thread actualizador = new Thread(new ActualizadorReferencias());
        Thread actualizadorBitR = new Thread(new ActualizadorBitR());
        actualizador.start();
        actualizadorBitR.start();
        try {
            actualizador.join();
			x = false; 
            actualizadorBitR.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long tiempoTotal = (hits * 25 + fallas * 10000000)/1000000;
        long tiempoSiTodoEnRAM = (referencias.size() * 25)/1000000;
        long tiempoSiTodoEnSWAP = (referencias.size() * 10);
        double porcentajeHits = (hits * 100.00) / referencias.size();
        double porcentajeFallas = (fallas * 100.00) / referencias.size();

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
        System.out.println("------------------------------------");
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
                    referencias.add(pagina);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ActualizadorReferencias implements Runnable {
        @Override
        public void run() {
            for (int pagina : referencias) {
                synchronized (memoria) {
                    if (memoria.containsKey(pagina)) {
                        hits++;
                        long contador = memoria.get(pagina);
                        memoria.put(pagina, actualizarContador(contador, true));
                    } else {
                        fallas++;
                        synchronized (swap) {
							if (!swap.contains(pagina)) {
								swap.add(pagina);
							}
						}
                        if (memoria.size() >= numeroDeMarcos) {
                            reemplazarPagina(pagina);
                        } else {
                            memoria.put(pagina, actualizarContador(0, true));
                        }
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    private class ActualizadorBitR implements Runnable {
        @Override
        public void run() {
            while (x) {
                synchronized (memoria) {
                    for (Map.Entry<Integer, Long> entry : memoria.entrySet()) {
                        long contador = entry.getValue();
                        memoria.put(entry.getKey(), actualizarContador(contador, false));
                    }
                }
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private long actualizarContador(long contador, boolean acceso) {
        contador >>= 1;
        if (acceso) {
            contador |= (1L << 31);
        }
        return contador;
    }

    private void reemplazarPagina(int paginaNueva) {
		Integer paginaAReemplazar = null;
		long contadorMin = Long.MAX_VALUE;
		synchronized (memoria) {
			for (Map.Entry<Integer, Long> entry : memoria.entrySet()) {
				if (entry.getValue() < contadorMin) {
					contadorMin = entry.getValue();
					paginaAReemplazar = entry.getKey();
				}
			}
			if (paginaAReemplazar != null) {
				swap.add(paginaAReemplazar);
				memoria.remove(paginaAReemplazar);
			}
			memoria.put(paginaNueva, actualizarContador(0, true));
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