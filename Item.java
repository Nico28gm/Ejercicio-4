public abstract class Item {
    protected String nombre;
    protected String descripcion;

    public Item(String n, String d) { nombre = n; descripcion = d; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    // Usar el item
    public abstract String usar(Jugador usuario, GameModel model, int objetivoIndex);
}
