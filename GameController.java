import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private GameModel model;
    private Random rng = new Random();
    private List<Item> poolItems;

    public GameController(GameModel model) {
        this.model = model;
        prepararPoolItems();
    }

    private void prepararPoolItems() {
        poolItems = new ArrayList<>();
        poolItems.add(new PocionFuria());
        poolItems.add(new PocionVida());
        poolItems.add(new Antidoto());
        poolItems.add(new PocionDefensa());
    }

    // Asigna N items aleatorios al jugador según rol (3 guerrero, 8 explorador)
    public void asignarItemsAleatoriosAlJugador() {
        Jugador jugador = model.getJugador();
        int cantidad = (jugador.getRol() == Jugador.Rol.GUERRERO) ? 3 : 8;
        for (int i = 0; i < cantidad; i++) {
            jugador.agregarItem(clonarItemAleatorio());
        }
    }

    private Item clonarItemAleatorio() {
        int i = rng.nextInt(poolItems.size());
        Item base = poolItems.get(i);
        if (base instanceof PocionFuria) return new PocionFuria();
        if (base instanceof PocionVida)  return new PocionVida();
        if (base instanceof Antidoto)    return new Antidoto();
        if (base instanceof PocionDefensa) return new PocionDefensa();
        return new PocionVida();
    }

    public void iniciarGeneracionEnemigos() { model.generarEnemigosAleatorios(); }

    /**
     * Ejecuta la acción del jugador:
     * opcion: 1=Atacar, 2=Usar item, 3=Pasar, 4=Salir
     * objetivoIndex: índice global de enemigo (en model.getEnemigos()) para atacar
     * itemIndex: índice dentro del inventario del jugador para usar
     */
    public String ejecutarTurnoJugador(int opcion, int objetivoIndex, int itemIndex) {
        Jugador jugador = model.getJugador();
        if (!jugador.estaVivo()) return jugador.getNombre() + " no puede actuar (muerto).";

        if (opcion == 4) return jugador.getNombre() + " eligió salir de la batalla.";
        if (opcion == 3) return jugador.getNombre() + " pasa su turno.";
        if (opcion == 1) {
            return jugador.tomarTurnoJugador(model, objetivoIndex, null);
        }
        if (opcion == 2) {
            if (itemIndex < 0 || itemIndex >= jugador.getInventario().size()) {
                return "Ítem inválido.";
            }
            Item it = jugador.getInventario().get(itemIndex);
            return jugador.tomarTurnoJugador(model, -1, it);
        }
        return "Opción inválida.";
    }

    // Ejecuta todos los turnos de enemigos en secuencia
    public List<String> ejecutarTurnosEnemigos() {
        List<String> acciones = new ArrayList<>();
        List<Enemigo> enemigos = model.getEnemigos();
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            if (!e.estaVivo()) continue;
            String res = e.tomarTurnoEnemigo(model, i, 0);
            e.disminuirContadoresPostTurno();
            acciones.add(res);
        }
        // quitar inmunidad temporal del jugador después de la ronda enemiga
        model.getJugador().quitarInmunidadTemporal();
        return acciones;
    }

    public String aplicarEstadosJugador() { return model.aplicarDanoEstadosJugador(); }
    public void limpiarPostTurnoJugador() { model.getJugador().disminuirContadoresPostTurno(); }
    public boolean jugadorMuerto() { return !model.getJugador().estaVivo(); }
    public boolean todosEnemigosMuertos() { return model.todosEnemigosMuertos(); }
}
