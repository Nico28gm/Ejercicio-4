import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel();

        // Inicializar jugador (pedir nombre y rol)
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido al simulador de batalla.");
        System.out.print("Ingrese nombre del jugador: ");
        String nombre = sc.nextLine().trim();

        System.out.println("Elija rol: 1) Guerrero  2) Explorador");
        int rolOp = leerEntero(sc, 1, 2);
        Jugador.Rol rol = (rolOp == 1) ? Jugador.Rol.GUERRERO : Jugador.Rol.EXPLORADOR;

        // Crear controlador
        model.inicializarJugador(nombre, rol);
        GameController controller = new GameController(model);

        // Generar enemigos y asignar items
        controller.iniciarGeneracionEnemigos();
        controller.asignarItemsAleatoriosAlJugador();

        // Mensajes de inicio
        System.out.println(model.getJugador().mensajeInicio());
        for (Enemigo e : model.getEnemigos()) {
            System.out.println(e.mensajeInicio());
        }

        boolean batallaTerminada = false;
        boolean salirPorJugador = false;

        while (!batallaTerminada) {
            // Mostrar estado
            mostrarEstado(model);

            // Aplicar efectos al inicio del turno del jugador
            String efectos = controller.aplicarEstadosJugador();
            if (!efectos.isEmpty()) {
                System.out.println(efectos);
                model.agregarRegistro(efectos);
            }

            // Si el jugador muere por estados
            if (controller.jugadorMuerto()) {
                String msg = model.getJugador().mensajeDerrota();
                System.out.println(msg);
                model.agregarRegistro(msg);
                batallaTerminada = true;
                continue;
            }

            // Menú del jugador
            System.out.println("\nTu turno. Elige acción:");
            System.out.println("1) Atacar");
            System.out.println("2) Usar ítem");
            System.out.println("3) Pasar turno");
            System.out.println("4) Salir de batalla");
            int opcion = leerEntero(sc, 1, 4);

            if (opcion == 1) {
                int objetivoGlobal = elegirObjetivo(sc, model);
                if (objetivoGlobal == -1) {
                    System.out.println("No hay enemigos vivos.");
                } else {
                    String res = controller.ejecutarTurnoJugador(1, objetivoGlobal, -1);
                    System.out.println(res);
                    model.agregarRegistro(res);
                }
            } else if (opcion == 2) {
                Jugador j = model.getJugador();
                if (j.getInventario().isEmpty()) {
                    System.out.println("No tienes ítems.");
                } else {
                    System.out.println("Inventario:");
                    for (int i = 0; i < j.getInventario().size(); i++) {
                        Item it = j.getInventario().get(i);
                        System.out.println("[" + i + "] " + it.getNombre() + " - " + it.getDescripcion());
                    }
                    int idx = leerEntero(sc, 0, j.getInventario().size() - 1);
                    String res = controller.ejecutarTurnoJugador(2, -1, idx);
                    System.out.println(res);
                    model.agregarRegistro(res);
                }
            } else if (opcion == 3) {
                String res = controller.ejecutarTurnoJugador(3, -1, -1);
                System.out.println(res);
                model.agregarRegistro(res);
            } else if (opcion == 4) {
                String res = controller.ejecutarTurnoJugador(4, -1, -1);
                System.out.println(res);
                model.agregarRegistro(res);
                salirPorJugador = true;
                batallaTerminada = true;
            }

            // ¿Ganó el jugador tras su acción?
            if (controller.todosEnemigosMuertos()) {
                String msg = model.getJugador().mensajeVictoria();
                System.out.println(msg);
                model.agregarRegistro(msg);
                batallaTerminada = true;
                continue;
            }

            if (!batallaTerminada) {
                // Post-turno del jugador: disminuir counters (furia)
                controller.limpiarPostTurnoJugador();

                // Turnos de enemigos
                List<String> accionesEnemigos = controller.ejecutarTurnosEnemigos();
                for (String a : accionesEnemigos) {
                    System.out.println(a);
                    model.agregarRegistro(a);
                }

                // Revisar estado posterior a ataques enemigos
                if (controller.jugadorMuerto()) {
                    String msg = model.getJugador().mensajeDerrota();
                    System.out.println(msg);
                    model.agregarRegistro(msg);
                    batallaTerminada = true;
                } else if (controller.todosEnemigosMuertos()) {
                    String msg = model.getJugador().mensajeVictoria();
                    System.out.println(msg);
                    model.agregarRegistro(msg);
                    batallaTerminada = true;
                }
            }
        } // end while

        if (salirPorJugador) System.out.println("La batalla terminó por decisión del jugador.");

        // Mostrar registro final (últimas 3)
        System.out.println("\n--- Registro final (últimas 3) ---");
        for (String s : model.getRegistroAcciones()) System.out.println("- " + s);

        sc.close();
    }

    private static void mostrarEstado(GameModel model) {
        System.out.println("\n--- ESTADO ---");
        System.out.println(model.getJugador().toString());
        List<Enemigo> enemigos = model.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            System.out.println("[" + i + "] " + enemigos.get(i).toString());
        }
        System.out.println("Ultimas acciones:");
        for (String s : model.getRegistroAcciones()) System.out.println("- " + s);
    }

    // Devuelve índice global del enemigo seleccionado, o -1 si no hay enemigos vivos
    private static int elegirObjetivo(Scanner sc, GameModel model) {
        List<Enemigo> enemigos = model.getEnemigos();
        List<Integer> vivosGlobal = new ArrayList<>();
        for (int i = 0; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) vivosGlobal.add(i);
        }
        if (vivosGlobal.isEmpty()) return -1;
        if (vivosGlobal.size() == 1) {
            int idx = vivosGlobal.get(0);
            Enemigo e = enemigos.get(idx);
            System.out.println("Objetivo automático: [" + idx + "] " + e.getNombre() + " - HP: " + e.getVida() + "/" + e.getVidaMax());
            return idx;
        }
        System.out.println("Elige objetivo entre los enemigos vivos:");
        for (int k = 0; k < vivosGlobal.size(); k++) {
            int globalIdx = vivosGlobal.get(k);
            Enemigo e = enemigos.get(globalIdx);
            System.out.println("[" + k + "] " + e.getNombre() + " - HP: " + e.getVida() + "/" + e.getVidaMax());
        }
        int choiceLocal = leerEntero(sc, 0, vivosGlobal.size() - 1);
        return vivosGlobal.get(choiceLocal);
    }

    private static int leerEntero(Scanner sc, int min, int max) {
        int val = -1;
        boolean ok = false;
        while (!ok) {
            try {
                String line = sc.nextLine().trim();
                val = Integer.parseInt(line);
                if (val < min || val > max) {
                    System.out.println("Ingrese un número entre " + min + " y " + max + ":");
                } else ok = true;
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Intente de nuevo:");
            }
        }
        return val;
    }
}
