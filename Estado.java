public class Estado {
    public enum Tipo { VENENO, QUEMADURA }
    private Tipo tipo;
    private int duracion;
    private int danoPorTurno;

    public Estado(Tipo tipo, int duracion, int danoPorTurno) {
        this.tipo = tipo;
        this.duracion = duracion;
        this.danoPorTurno = danoPorTurno;
    }

    public Tipo getTipo() { return tipo; }
    public int getDuracion() { return duracion; }
    public int getDanoPorTurno() { return danoPorTurno; }
    public void decrementarDuracion() { duracion = Math.max(0, duracion - 1); }
    public boolean estaActivo() { return duracion > 0; }
}

