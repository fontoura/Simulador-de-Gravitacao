package simulador.applet;

import java.awt.Graphics;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/*
 *  Este arquivo � parte do programa Simulador de Gravita��o.
 *
 *  O Simulador de Gravita��o � um software livre; voc� pode redistribui-lo
 *  e/ou modifica-lo dentro dos termos da Licen�a P�blica Geral GNU como 
 *  publicada pela Funda��o do Software Livre (FSF), quer seja na vers�o 2 da 
 *  Licen�a, quer seja em qualquer vers�o posterior.
 *
 *  Este programa � distribuido na esperan�a que possa ser util, 
 *  mas SEM QUALQUER GARANTIA; sem sequer garantias impl�citas de
 *  ADEQUA��O ao MERCADO ou a qualquer APLICA��O EM PARTICULAR. Veja
 *  a Licen�a P�blica Geral GNU para maiores detalhes.
 *
 *  Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral GNU
 *  junto com este programa; se n�o, escreva para a Funda��o do Software
 *  Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
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
