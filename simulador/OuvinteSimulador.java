package simulador;

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
 * @author Felipe Michels Fontoura
 */
public interface OuvinteSimulador {
	/**
	 * Evento disparado quando um novo corpo é adicionado ao simulador.
	 * @param corpo Corpo adicionado ao simulador.
	 */
	public void adicionou(Corpo corpo);
	
	/**
	 * Evento disparado quando um corpo é removido do simulador.
	 * @param corpo Corpo removido do simulador.
	 */
	public void removeu(Corpo corpo);
	
	/**
	 * Evento disparado quando dois corpos são unidos no simulador, ocupando
	 * o lugar do primeiro.
	 * @param corpo_1 Primeiro corpo.
	 * @param corpo_2 Segundo corpo.
	 */
	public void juntou(Corpo corpo_1, Corpo corpo_2);
}
