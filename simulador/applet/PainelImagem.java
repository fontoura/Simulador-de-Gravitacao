package simulador.applet;

import java.awt.Graphics;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

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
 * Painel simples para exibição de imagens.
 * @author Felipe Michels Fontoura
 */
public class PainelImagem extends JPanel {
    private static final long serialVersionUID = 5867644374045884550L;

    private BufferedImage imagem;

    public PainelImagem() {}

    public void alterarImagem(BufferedImage i) {
        imagem = i;
        repaint();
    }

    @Override public void paint(Graphics g) {
        super.paint(g);
        if (imagem != null) g.drawImage(imagem, 0, 0, null);
    }
}