public abstract class Combatiente {
    protected String nombre;
    protected int vidaMax;
    protected int vida;
    protected int ataqueBase;

    protected boolean inmunidadTemporal = false;
    protected int furiaTurnosRestantes = 0;
    protected double furiaMultiplicador = 1.0;

    public Combatiente(String nombre, int vidaMax, int ataqueBase) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vida = vidaMax;
        this.ataqueBase = ataqueBase;
    }

    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public int getVidaMax() { return vidaMax; }
    public int getAtaqueBase() { return ataqueBase; }
    public boolean estaVivo() { return vida > 0; }

    public void recibirDanio(int d) {
        if (!inmunidadTemporal) {
            vida -= d;
            if (vida < 0) vida = 0;
        }
    }

    public void curar(int cantidad) {
        vida += cantidad;
        if (vida > vidaMax) vida = vidaMax;
    }

    public int calcularAtaqueActual() {
        return (int) Math.round(ataqueBase * furiaMultiplicador);
    }

    public void aplicarFuria(int turnos, double mult) {
        furiaTurnosRestantes = turnos;
        furiaMultiplicador = mult;
    }

    public void disminuirContadoresPostTurno() {
        if (furiaTurnosRestantes > 0) {
            furiaTurnosRestantes--;
            if (furiaTurnosRestantes == 0) furiaMultiplicador = 1.0;
        }
    }

    public void establecerInmunidadTemporal(boolean val) { inmunidadTemporal = val; }
    public void quitarInmunidadTemporal() { inmunidadTemporal = false; }

    public String mensajeInicio() { return nombre + " entra al combate."; }
    public String mensajeVictoria() { return nombre + " ha ganado la batalla!"; }
    public String mensajeDerrota() { return nombre + " ha sido derrotado."; }

    // Los métodos de acción 
    public abstract String tomarTurnoJugador(GameModel model, int objetivoIndex, Item itemUsado);
    public abstract String tomarTurnoEnemigo(GameModel model, int enemigoIndex, int objetivoJugadorIndex);

    @Override
    public String toString() {
        return nombre + " [HP: " + vida + "/" + vidaMax + " | ATK: " + ataqueBase + "]";
    }
}
