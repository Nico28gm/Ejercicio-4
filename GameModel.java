import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private Jugador jugador;
    private List<Enemigo> enemigos;
    private List<String> registroAcciones;
    private Random rng = new Random();

    private int quemaduraDuracion = 3;
    private int venenoDuracion = 3;
    private int quemaduraDanioPorTurno;
    private int venenoDanioPorTurno;
    private List<Estado> estadosJugador = new ArrayList<>();

    public GameModel() {
        registroAcciones = new ArrayList<>();
        enemigos = new ArrayList<>();
    }

    // Inicializa jugador Y calcula daño por estado en base a vidaMax del jugador
    public void inicializarJugador(String nombre, Jugador.Rol rol) {
        int vidaBaseGuerrero = 220;
        int atkBaseGuerrero = 80;
        int vidaBaseExplorador = 170;
        int atkBaseExplorador = 65;

        if (rol == Jugador.Rol.GUERRERO) {
            int vida = (int) Math.round(vidaBaseGuerrero * 1.5);
            jugador = new Jugador(nombre, rol, vida, atkBaseGuerrero);
        } else {
            int vida = (int) Math.round(vidaBaseExplorador * 1.5);
            jugador = new Jugador(nombre, rol, vida, atkBaseExplorador);
        }

        quemaduraDanioPorTurno = (int) Math.round(jugador.getVidaMax() * 0.1);
        venenoDanioPorTurno = (int) Math.round(jugador.getVidaMax() * 0.10);
    }

    public Jugador getJugador() { return jugador; }

    // Genera enemigos aleatorios 
    public void generarEnemigosAleatorios() {
        int cantidad = 2;
        for (int i = 0; i < cantidad; i++) {
            Enemigo.Tipo tipo = rng.nextBoolean() ? Enemigo.Tipo.MAGO : Enemigo.Tipo.CICLOPE;
            boolean jefe = rng.nextDouble() < 0.25;
            int vida = (tipo == Enemigo.Tipo.MAGO) ? 60 : 110;
            int atk  = (tipo == Enemigo.Tipo.MAGO) ? 25 : 35;
            vida = (int) Math.round(vida * 1.5); // +50% vida global
            if (jefe) {
                vida = (int) Math.round(vida * 1.5); // jefe +50% vida
                atk = (int) Math.round(atk * 1.3);    // jefe +30% ataque
            }
            String nombre = (tipo == Enemigo.Tipo.MAGO ? "Mago_" : "Cíclope_") + (i + 1);
            Enemigo e = new Enemigo(nombre, tipo, jefe, vida, atk);
            enemigos.add(e);
        }
    }

    public List<Enemigo> getEnemigos() { return enemigos; }

    public void agregarRegistro(String texto) {
        registroAcciones.add(texto);
        if (registroAcciones.size() > 3) registroAcciones.remove(0);
    }

    public List<String> getRegistroAcciones() { return new ArrayList<>(registroAcciones); }

    // Estado del jugador
    public void aplicarEstadoEnJugador(Estado e) { estadosJugador.add(e); }
    public boolean quitarEstadosJugador() {
        if (estadosJugador.isEmpty()) return false;
        estadosJugador.clear();
        return true;
    }

    // Aplica daño por estados (al inicio del turno del jugador)
    public String aplicarDanoEstadosJugador() {
        StringBuilder sb = new StringBuilder();
        if (estadosJugador.isEmpty()) return "";
        // copia para iterar
        List<Estado> copia = new ArrayList<>(estadosJugador);
        for (Estado e : copia) {
            if (e.estaActivo()) {
                jugador.recibirDanio(e.getDanoPorTurno());
                sb.append(jugador.getNombre() + " sufre " + e.getDanoPorTurno() + " por " + e.getTipo() + ". ");
                e.decrementarDuracion();
            }
        }
        estadosJugador.removeIf(st -> !st.estaActivo());
        return sb.toString();
    }

    public int getQuemaduraDuracion() { return quemaduraDuracion; }
    public int getVenenoDuracion() { return venenoDuracion; }
    public int getQuemaduraDanioPorTurno() { return quemaduraDanioPorTurno; }
    public int getVenenoDanioPorTurno() { return venenoDanioPorTurno; }

    // Devuelve un aliado aleatorio distinto del índice exclIndex 
    public Enemigo obtenerAliadoAleatorio(int exclIndex) {
        List<Enemigo> candidatos = new ArrayList<>();
        for (int i = 0; i < enemigos.size(); i++) {
            if (i == exclIndex) continue;
            Enemigo e = enemigos.get(i);
            if (e.estaVivo()) candidatos.add(e);
        }
        if (candidatos.isEmpty()) return null;
        return candidatos.get(rng.nextInt(candidatos.size()));
    }

    public boolean todosEnemigosMuertos() {
        for (Enemigo e : enemigos) if (e.estaVivo()) return false;
        return true;
    }
}
