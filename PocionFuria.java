public class PocionFuria extends Item {
    public PocionFuria() { super("Poción de furia", "Aumenta ataque +80% por 2 turnos."); }
    @Override
    public String usar(Jugador usuario, GameModel model, int objetivoIndex) {
        usuario.aplicarFuria(2, 1.80); // 2 turnos, +40%
        return usuario.getNombre() + " usa Poción de furia. Ataque aumentado por 2 turnos.";
    }
}

