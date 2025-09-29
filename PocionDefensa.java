public class PocionDefensa extends Item {
    public PocionDefensa() { super("Poción de defensa", "Concede inmunidad durante la ronda enemiga."); }
    @Override
    public String usar(Jugador usuario, GameModel model, int objetivoIndex) {
        usuario.establecerInmunidadTemporal(true);
        return usuario.getNombre() + " usa Poción de defensa y será inmune durante la ronda enemiga.";
    }
}

