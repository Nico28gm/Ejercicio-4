public class PocionVida extends Item {
    public PocionVida() { super("Poción de vida", "Cura 40% de la vida máxima."); }
    @Override
    public String usar(Jugador usuario, GameModel model, int objetivoIndex) {
        int cantidad = (int) Math.round(usuario.getVidaMax() * 0.40);
        usuario.curar(cantidad);
        return usuario.getNombre() + " usa Poción de vida y recupera " + cantidad + " HP.";
    }
}

