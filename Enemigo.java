import java.util.Random;

public class Enemigo extends Combatiente {
    public enum Tipo { MAGO, CICLOPE }
    private Tipo tipo;
    private boolean jefe;
    private Random rng = new Random();

    public Enemigo(String nombre, Tipo tipo, boolean jefe, int vidaMax, int atk) {
        super(nombre, vidaMax, atk);
        this.tipo = tipo;
        this.jefe = jefe;
    }

    public Tipo getTipo() { return tipo; }
    public boolean esJefe() { return jefe; }

    public void aplicarBuffTemporalPorMago(double mult, int turnos) {
        aplicarFuria(turnos, mult);
    }

    @Override
    public String tomarTurnoJugador(GameModel m, int o, Item it) { return ""; }

    @Override
    public String tomarTurnoEnemigo(GameModel m, int enemigoIndex, int objetivoJugadorIndex) {
        Jugador jugador = m.getJugador();
        StringBuilder sb = new StringBuilder();
        int dano = calcularAtaqueActual();

        // Cíclope hace más daño a guerrero
        if (tipo == Tipo.CICLOPE && jugador.getRol() == Jugador.Rol.GUERRERO) {
            dano = (int) Math.round(dano * (jefe ? 1.5 : 1.25));
        }

        if (jugador.inmunidadTemporal) {
            sb.append(nombre + " ataca pero " + jugador.getNombre() + " está protegido.");
        } else {
            jugador.recibirDanio(dano);
            sb.append(nombre + " ataca a " + jugador.getNombre() + " por " + dano + " puntos.");
        }

        double prob = rng.nextDouble();
        if (tipo == Tipo.MAGO && prob < 0.30) {
            // quemadura
            Estado e = new Estado(Estado.Tipo.QUEMADURA, m.getQuemaduraDuracion(), m.getQuemaduraDanioPorTurno());
            m.aplicarEstadoEnJugador(e);
            sb.append(" " + jugador.getNombre() + " ha sido quemado!");
        }
        if (tipo == Tipo.CICLOPE && prob < 0.30) {
            // envenenar
            Estado e = new Estado(Estado.Tipo.VENENO, m.getVenenoDuracion(), m.getVenenoDanioPorTurno());
            m.aplicarEstadoEnJugador(e);
            sb.append(" " + jugador.getNombre() + " ha sido envenenado!");
        }

        // Mago jefe potencia a un aliado
        if (tipo == Tipo.MAGO && jefe) {
            Enemigo aliado = m.obtenerAliadoAleatorio(enemigoIndex);
            if (aliado != null && aliado.estaVivo()) {
                aliado.aplicarBuffTemporalPorMago(1.30, 1); // +30% ataque 1 turno
                sb.append(" " + nombre + " (mago jefe) potencia a " + aliado.getNombre() + "!");
            }
        }

        return sb.toString();
    }

    @Override
    public String mensajeInicio() {
        return (jefe ? "Jefe " : "") + nombre + " (" + tipo + ") aparece.";
    }
}
