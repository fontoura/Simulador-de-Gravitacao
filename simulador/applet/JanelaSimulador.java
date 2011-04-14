package simulador.applet;

import javax.swing.JFrame;
import javax.swing.UIManager;

/*
 *  Este arquivo é parte do programa Simulador de Gravitação.
 *
 *  O Simulador de Gravitação é um software livre; você pode redistribui-lo
 *  e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como
 *  publicada pela Fundação do Software Livre (FSF), quer seja na versão 2 da
 *  Licença, quer seja em qualquer versão posterior.
 *
 *  Este programa é distribuido na esperança que possa ser util,
 *  mas SEM QUALQUER GARANTIA; sem sequer garantias implícitas de
 *  ADEQUAÇÂO ao MERCADO ou a qualquer APLICAÇÃO EM PARTICULAR. Veja
 *  a Licença Pública Geral GNU para maiores detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU
 *  junto com este programa; se não, escreva para a Fundação do Software
 *  Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
 * Janela para exibição do applet fora do navegador.
 * @author Felipe Michels Fontoura
 */
public class JanelaSimulador extends JFrame {
    private static final long serialVersionUID = 6568974886161745111L;

    private AppletSimulador applet;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {};

        JanelaSimulador janela = new JanelaSimulador(500, 500);
        janela.setVisible(true);
    }

    public JanelaSimulador(int largura, int altura) {
        super("Simulador de gravitação universal");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        setSize(getInsets().left + largura + getInsets().right, getInsets().top + altura + getInsets().bottom);
        applet = new AppletSimulador();

        add(applet);
    }
}