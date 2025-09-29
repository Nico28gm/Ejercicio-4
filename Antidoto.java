public class Antidoto extends Item {
    public Antidoto() { super("Antídoto", "Elimina veneno o quemadura."); }
    @Override
    public String usar(Jugador usuario, GameModel model, int objetivoIndex) {
        boolean removed = model.quitarEstadosJugador();
        if (removed) return usuario.getNombre() + " usa Antídoto y elimina condiciones (veneno/quemadura).";
        else return usuario.getNombre() + " usa Antídoto pero no tenía condiciones.";
    }
}


