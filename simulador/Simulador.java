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
public class Simulador {
	private static final int INCREMENTO_CAPACIDADE = 8;
	
	// vari�veis internas declara tudo de uma vez para ganhar velocidade.
	private int i, j, c1, c2;
	private double a, b, c, ct, delta_x, delta_y, delta_vx, delta_vy, mi, mj, distancia, delta, massa;
	private boolean colisao;
	
	// lista de corpos do simulador.
	public Corpo[] corpos;
	public int total_corpos;
	
	// lista de vetores de acelera��o.
	private Vetor[] aceleracao;
	
	// lista de ouvintes do simulador
	private OuvinteSimulador[] ouvintes;
	
	// inicializa os valores do simulador.
	public Simulador() {
		corpos = new Corpo[INCREMENTO_CAPACIDADE];
		aceleracao = new Vetor[INCREMENTO_CAPACIDADE];
		ouvintes = new OuvinteSimulador[0];
		total_corpos = 0;
	}
	
	public void centroGravitacional(Vetor vetor) {
		vetor.x = 0;
		vetor.y = 0;
		double massa = 0;
		for (i = 0; i < total_corpos; i ++) {
			massa += corpos[i].massa.v;
			vetor.x += corpos[i].massa.v * corpos[i].posicao.x;
			vetor.y += corpos[i].massa.v * corpos[i].posicao.y;
		}
		if (massa != 0) {
			vetor.x /= massa;
			vetor.y /= massa;
		}
	}
	
	// adiciona um ouvinte de simulador.
	public void adicionarOuvinte(OuvinteSimulador ouvinte) {
		// aloca mais mem�ria para o novo ouvinte.
		OuvinteSimulador[] ouvintes_2 = new OuvinteSimulador[ouvintes.length + 1];
		System.arraycopy(ouvintes, 0, ouvintes_2, 0, ouvintes.length);
		ouvintes_2[ouvintes.length] = ouvinte;
		ouvintes = ouvintes_2;
	}

	// remove um ouvinte de simulador.
	public void removerOuvinte(OuvinteSimulador ouvinte) {
		j = 0;
		for (i = 0; i < ouvintes.length; i ++) {
			ouvintes[j] = ouvintes[i];
			if (ouvintes[j] != ouvinte) j ++;
		}
		OuvinteSimulador[] ouvintes_2 = new OuvinteSimulador[j];
		System.arraycopy(ouvintes, 0, ouvintes_2, 0, j);
		ouvintes = ouvintes_2;
	}
	
	// remove um corpo do simulador.
	public void removerCorpo(Corpo corpo) {
		// remove as ocorr�ncias do corpo.
		j = 0;
		for (i = 0; i < total_corpos; i ++) {
			aceleracao[j] = aceleracao[i];
			corpos[j] = corpos[i];
			if (corpos[j] != corpo) j ++;
		}
		for (i = j + 1; i < total_corpos; i ++) {
			corpos[i] = null;
			aceleracao[i] = null;
		}
		
		// dispara os eventos.
		if (total_corpos > j) {
			total_corpos = j;
			for (i = 0; i < ouvintes.length; i ++)
				ouvintes[i].removeu(corpo);
		}
	}
	
	// colide dois corpos.
	public void colidirCorpos(Corpo corpo1, Corpo corpo2) {
		// junta as informa��es sobre os corpos.
		massa = corpo1.massa.v + corpo2.massa.v;
		corpo1.cor = new Color((int)((corpo1.cor.getRed() * corpo1.massa.v + corpo2.cor.getRed() * corpo2.massa.v)/massa),
							   (int)((corpo1.cor.getGreen() * corpo1.massa.v + corpo2.cor.getGreen() * corpo2.massa.v)/massa),
							   (int)((corpo1.cor.getBlue() * corpo1.massa.v + corpo2.cor.getBlue() * corpo2.massa.v)/massa));
		corpo1.raio.v = Math.sqrt(corpo1.raio.v * corpo1.raio.v + corpo2.raio.v * corpo2.raio.v);
		corpo1.posicao.x = (corpo1.posicao.x * corpo1.massa.v + corpo2.posicao.x * corpo2.massa.v)/massa;
		corpo1.posicao.y = (corpo1.posicao.y * corpo1.massa.v + corpo2.posicao.y* corpo2.massa.v)/massa;
		corpo1.velocidade.x = (corpo1.velocidade.x * corpo1.massa.v + corpo2.velocidade.x * corpo2.massa.v) / massa;
		corpo1.velocidade.y = (corpo1.velocidade.y * corpo1.massa.v + corpo2.velocidade.y * corpo2.massa.v) / massa;
		corpo1.massa.v = massa;
		
		// remove o segundo corpo.
		j = 0;
		for (i = 0; i < total_corpos; i ++) {
			aceleracao[j] = aceleracao[i];
			corpos[j] = corpos[i];
			if (corpos[j] != corpo2) j ++;
		}
		for (i = j + 1; i < total_corpos; i ++) {
			corpos[i] = null;
			aceleracao[i] = null;
		}
		total_corpos = j;
		
		// dispara os eventos.
		for (i = 0; i < ouvintes.length; i ++)
			ouvintes[i].juntou(corpo1, corpo2);
	}
	
	// adiciona um corpo ao simulador.
	public void adicionarCorpo(Corpo corpo) {
		// incrementa o total de corpos.
		total_corpos ++;
		
		// se necess�rio, aloca mais mem�ria.
		if (total_corpos == corpos.length) {
			Corpo[] corpos_2 = new Corpo[total_corpos + INCREMENTO_CAPACIDADE];
			Vetor[] aceleracao_2 = new Vetor[total_corpos + INCREMENTO_CAPACIDADE];
			System.arraycopy(corpos, 0, corpos_2, 0, total_corpos - 1);
			System.arraycopy(aceleracao, 0, aceleracao_2, 0, total_corpos - 1);
			corpos = corpos_2;
			aceleracao = aceleracao_2;
		}
		
		// insere o novo corpo no vetor.
		corpos[total_corpos - 1] = corpo;
		aceleracao[total_corpos - 1] = new Vetor(0, 0);
		
		// dispara os eventos.
		for (i = 0; i < ouvintes.length; i ++)
			ouvintes[i].adicionou(corpo);
	}
	
	// m�todo que executa a integra��o num�rica.
	public void gatilho(double t) {
		
		// limpa o vetor de acelera��es.
		for (i = 0; i < total_corpos; i ++) {
			aceleracao[i].x = 0;
			aceleracao[i].y = 0;
		}
		
		// calcula a acelera��o instant�nea de cada um dos corpos.
		for (i = 0; i < total_corpos; i ++) for (j = i + 1; j < total_corpos; j ++) {
	        delta_x = corpos[i].posicao.x - corpos[j].posicao.x;
	        delta_y = corpos[i].posicao.y - corpos[j].posicao.y;
	        distancia = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
			distancia = distancia * distancia * distancia;
			mi = (Corpo.constante_gravitacional * corpos[j].massa.v) / distancia;
			mj = (Corpo.constante_gravitacional * corpos[i].massa.v) / distancia;
			aceleracao[i].x -= delta_x * mi;
			aceleracao[i].y -= delta_y * mi;
			aceleracao[j].x += delta_x * mj;
			aceleracao[j].y += delta_y * mj;
		}
		
		// calcula a velocidade m�dia dos corpos.
		for (i = 0; i < total_corpos; i ++) {
	        corpos[i].velocidade.x += (aceleracao[i].x * t) / 2;
	        corpos[i].velocidade.y += (aceleracao[i].y * t) / 2;
		}
		
		// testa por colis�es entre os corpos.
		colisao = true;
		while (colisao) {
			colisao = false;
			ct = t;
			for (i = 0; i < total_corpos; i ++) for (j = i + 1; j < total_corpos; j ++) {
				distancia = corpos[i].raio.v + corpos[j].raio.v;
		        delta_x = corpos[i].posicao.x - corpos[j].posicao.x;
		        delta_y = corpos[i].posicao.y - corpos[j].posicao.y;
		        if (Math.sqrt(delta_x * delta_x + delta_y * delta_y) < distancia) {
                	colisao = true;
                	ct = 0;
                	c1 = i;
                	c2 = j;
		        } else {
					delta_vx = corpos[i].velocidade.x - corpos[j].velocidade.x;
					delta_vy = corpos[i].velocidade.y - corpos[j].velocidade.y;
		            a = delta_vx*delta_vx + delta_vy*delta_vy;
		            b = 2*(delta_x*delta_vx + delta_y*delta_vy);
		            c = delta_x*delta_x + delta_y*delta_y - distancia*distancia;
		            delta =  b*b - 4*a*c; // ditancia � o delta
		            if (delta > 0) {
		            	delta = Math.sqrt(delta);
		            	delta /= 2*a;
		                b /= -2*a;
		                delta = (b > delta) ? (b - delta) : (b + delta);
		                if (0 < delta && delta < ct) {
		                	colisao = true;
		                	ct = delta;
		                	c1 = i;
		                	c2 = j;
		                }
		            }
		        }
			}
			if (colisao) colidirCorpos(corpos[c1], corpos[c2]);
		}

		// limpa o vetor de acelera��es.
		for (i = 0; i < total_corpos; i ++) {
			aceleracao[i].x = 0;
			aceleracao[i].y = 0;
		}
		
		// calcula a acelera��o instant�nea de cada um dos corpos.
		for (i = 0; i < total_corpos; i ++) for (j = i + 1; j < total_corpos; j ++) {
	        delta_x = corpos[i].posicao.x - corpos[j].posicao.x;
	        delta_y = corpos[i].posicao.y - corpos[j].posicao.y;
	        distancia = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
			distancia = distancia * distancia * distancia;
			mi = (Corpo.constante_gravitacional * corpos[j].massa.v) / distancia;
			mj = (Corpo.constante_gravitacional * corpos[i].massa.v) / distancia;
			aceleracao[i].x -= delta_x * mi;
			aceleracao[i].y -= delta_y * mi;
			aceleracao[j].x += delta_x * mj;
			aceleracao[j].y += delta_y * mj;
		}
		
		// calcula a velocidade dos corpos e movimenta-os.
		for (i = 0; i < total_corpos; i ++) {
	        corpos[i].posicao.x += corpos[i].velocidade.x * t;
	        corpos[i].posicao.y += corpos[i].velocidade.y * t;
	        corpos[i].velocidade.x += (aceleracao[i].x * t) / 2;
	        corpos[i].velocidade.y += (aceleracao[i].y * t) / 2;
		}
	}
}
