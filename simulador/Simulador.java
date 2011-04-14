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
 * Classe que faz todas as operações necessárias à simulação.
 *
 * É possível detectar eventos ocorridos durante a simulação com o uso de
 * ouvintes de simulação.
 * @author Felipe Michels Fontoura
 */
public class Simulador {
    private static final int INCREMENTO_CAPACIDADE = 8;

    // variáveis internas, alocadas uma única vez.
    private int i, j, c1, c2;
    private double a, b, c, ct, delta_x, delta_y, delta_vx, delta_vy, mi, mj, distancia, delta, massa;
    private boolean colisao;

    // lista de corpos do simulador.
    public Corpo[] corpos;
    public int total_corpos;

    // lista de vetores de aceleração.
    private Vetor[] aceleracao;

    // lista de ouvintes do simulador
    private OuvinteSimulador[] ouvintes;

    /**
     * Inicializa um simulador vazio.
     */
    public Simulador() {
        corpos = new Corpo[INCREMENTO_CAPACIDADE];
        aceleracao = new Vetor[INCREMENTO_CAPACIDADE];
        ouvintes = new OuvinteSimulador[0];
        total_corpos = 0;
    }

    /**
     * Calcula o centro gravitacional, levando em conta todos os corpos.
     */
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
        // aloca mais memória para o novo ouvinte.
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
        // remove as ocorrências do corpo.
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
        // junta as informações sobre os corpos.
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

        // se necessário, aloca mais memória.
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

    // método que executa a integração numérica.
    public void gatilho(double t) {

        // limpa o vetor de acelerações.
        for (i = 0; i < total_corpos; i ++) {
            aceleracao[i].x = 0;
            aceleracao[i].y = 0;
        }

        // calcula a aceleração instantânea de cada um dos corpos.
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

        // calcula a velocidade média dos corpos.
        for (i = 0; i < total_corpos; i ++) {
            corpos[i].velocidade.x += (aceleracao[i].x * t) / 2;
            corpos[i].velocidade.y += (aceleracao[i].y * t) / 2;
        }

        // testa por colisões entre os corpos.
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
                    delta =  b*b - 4*a*c; // ditancia é o delta
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

        // limpa o vetor de acelerações.
        for (i = 0; i < total_corpos; i ++) {
            aceleracao[i].x = 0;
            aceleracao[i].y = 0;
        }

        // calcula a aceleração instantânea de cada um dos corpos.
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
