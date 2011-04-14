package simulador;

import java.awt.Color;

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
public class Corpo {
    // constantes gerais.
    public static final double constante_gravitacional = 6.67428E-11;
    
    // dados caracter�sticos do planeta no sistema internacional de unidades.
    public Escalar massa;
    public Escalar raio;
    public Vetor posicao;
    public Vetor velocidade;
    
    // dados para os gr�ficos.
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
