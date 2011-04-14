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
 * Classe que representa um vetor editável.
 *
 * É particularmente útil por permitir a passagem de dados por referência e
 * o retorno de pares de dados.
 * @author Felipe Michels Fontoura
 */
public class Vetor {
    public double x, y;

    public Vetor(double x, double y) {
        this.x = x;
        this.y = y;
    }
}