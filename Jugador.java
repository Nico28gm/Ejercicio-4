import java.util.ArrayList;
import java.util.List;

public class Jugador extends Combatiente {
    public enum Rol { GUERRERO, EXPLORADOR }
    private Rol rol;
    private List<Item> inventario;

    public Jugador(String nombre, Rol rol, int vidaMax, int ataqueBase) {
        super(nombre, vidaMax, ataqueBase);
        this.rol = rol;
        this.inventario = new ArrayList<>();
    }

    public Rol getRol() { return rol; }
    public List<Item> getInventario() { return inventario; }

    public void agregarItem(Item it) { inventario.add(it); }
    public void removerItem(int idx) { if (idx >= 0 && idx < inventario.size()) inventario.remove(idx); }

    @Override
    public String tomarTurnoJugador(GameModel model, int objetivoIndex, Item itemUsado) {
        // Si proporciona un item
        if (itemUsado != null) {
            String res = itemUsado.usar(this, model, objetivoIndex);
            // eliminar la primera ocurrencia del item usado
            int idx = inventario.indexOf(itemUsado);
            if (idx >= 0) inventario.remove(idx);
            return res;
        } else {
            // Atacar
            if (objetivoIndex < 0 || objetivoIndex >= model.getEnemigos().size()) {
                return nombre + " intentó atacar un objetivo inválido.";
            }
            Enemigo objetivo = model.getEnemigos().get(objetivoIndex);
            int dano = calcularAtaqueActual();
            objetivo.recibirDanio(dano);
            return nombre + " ataca a " + objetivo.getNombre() + " por " + dano + " puntos.";
        }
    }

    // No aplica para jugador
    @Override
    public String tomarTurnoEnemigo(GameModel m, int eIndex, int objIndex) { return ""; }

    @Override
    public String mensajeInicio() {
        return nombre + " (Jugador - " + rol + ") entra al combate.";
    }
}
