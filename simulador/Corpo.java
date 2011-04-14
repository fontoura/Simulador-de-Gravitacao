package simulador;

import java.awt.Color;

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
public class Corpo {
    // constantes gerais.
    public static final double constante_gravitacional = 6.67428E-11;
    
    // dados característicos do planeta no sistema internacional de unidades.
    public Escalar massa;
    public Escalar raio;
    public Vetor posicao;
    public Vetor velocidade;
    
    // dados para os gráficos.
    public Color cor;

    // construtor.
    public Corpo(double x, double y, double vx, double vy, double m, double r, Color c) {
        posicao = new Vetor(x, y);
        velocidade = new Vetor(vx, vy);
        massa = new Escalar(m);
        raio = new Escalar(r);
        cor = c;
    }
}
