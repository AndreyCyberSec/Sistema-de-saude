package SistemaFinal;

import visao.TelaInicial;
import javax.swing.SwingUtilities;

/**
 * Main — ponto de entrada do sistema.
 *
 * Inicia a interface gráfica na thread correta do Swing (EDT).
 * Abre a TelaInicial (Login/Cadastro).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial());
    }
}