package simulador;

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
public interface OuvinteSimulador {
	/**
	 * Evento disparado quando um novo corpo � adicionado ao simulador.
	 * @param corpo Corpo adicionado ao simulador.
	 */
	public void adicionou(Corpo corpo);
	
	/**
	 * Evento disparado quando um corpo � removido do simulador.
	 * @param corpo Corpo removido do simulador.
	 */
	public void removeu(Corpo corpo);
	
	/**
	 * Evento disparado quando dois corpos s�o unidos no simulador, ocupando
	 * o lugar do primeiro.
	 * @param corpo_1 Primeiro corpo.
	 * @param corpo_2 Segundo corpo.
	 */
	public void juntou(Corpo corpo_1, Corpo corpo_2);
}
