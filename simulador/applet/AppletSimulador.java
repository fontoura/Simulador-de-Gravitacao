package simulador.applet;

import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;

import simulador.*;

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
public class AppletSimulador extends JApplet implements OuvinteSimulador, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1980785002209142771L;

	public static final double RAIO_NOVOS_CORPOS = 5E9;
	public static final double MASSA_NOVOS_CORPOS = 7.5E24;
	
	private static final int SELETOR_QUADRADINHO = 3;
	
	public static final int ESTADO_COMUM = 0;
	public static final int ESTADO_MUDANDO_VELOCIDADE = 1;
	public static final int ESTADO_MULTISSELECAO = 2;
	public static final int ESTADO_POSICIONANDO_CORPO = 3;
	
	private DecimalFormat formatador;

	// variáveis internas (para economizar tempo).
	private boolean status;
	private Corpo selecionado;
	
	// para posicionamento do mouse.
	private int mouse_x1, mouse_y1;
	private int mouse_x2, mouse_y2;
	
	// para medição do tempo.
	private int total_segundos;
	private int total_minutos;
	private int total_horas;
	private int total_dias;
	private int total_anos;
	private int estado;
	
	// para a exibição dos gráficos.
	private BufferedImage tela;
    private Graphics2D g2d;
    private PainelImagem painel;
	
	// classe que executa a simulação.
	public Simulador simulador;
	
	// estado da simulação.
	public boolean simulando;

    // opções de exibição.
    public Vetor centro;
    public Escalar escala;
    public Escalar escalaVetores;
    public Escalar escalaRaios;
    
    // corpos selecionados.
    public LinkedList<Corpo> selecionados;
    
    // opções da integração numérica.
    public Timer sincronismo;
    public int intervalo;
    public int integracoes;
    
    // para seleção.
    public Corpo objeto;
    
    // para o menu.
    JMenuBar menu;
    JMenu menu_simulacao;
    JMenuItem menu_simulacao_ativar;
    JMenuItem menu_simulacao_pausar;
    JMenu menu_corpos;
    JMenu menu_alterar;
    JMenuItem menu_alterar_velocidade;
    JMenuItem menu_alterar_raio;
    JMenuItem menu_alterar_massa;
    JMenuItem menu_alterar_cor;
    JMenu menu_exibir;
    JCheckBoxMenuItem menu_exibir_velocidades;
    JCheckBoxMenuItem menu_exibir_velocidades_angulares;
    JMenuItem menu_corpos_adicionar;
    JMenuItem menu_corpos_apagar;
    JMenuItem menu_corpos_limpar;
    JMenu menu_sobre;
    JMenuItem menu_sobre_licenca;
    
    // cria um novo painel simulador.
    public AppletSimulador() {
    	super();

    	DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(new Locale("en"));
    	formatador = new DecimalFormat("0.0000E00", dfs);
    	
    	selecionados = new LinkedList<Corpo>();
    	
        // cria o painel de desenho.
    	estado = ESTADO_COMUM;
        objeto = null;
    	painel = new PainelImagem();
        painel.addMouseListener(this);
        painel.addMouseMotionListener(this);
        add(painel);
        
        // crias os parâmetros de medição do tempo.
        total_segundos = 0;
        total_minutos = 0;
        total_dias = 0;
        total_anos = 0;
        
        // cria os parâmetros padrão de exibição.
        escala = new Escalar(1E-9);
        escalaVetores = new Escalar(1E-3);
        escalaRaios = new Escalar(1E-9);
        centro = new Vetor(0, 0);
        simulando = true;
        intervalo = 30;
        integracoes = 720;
        
        // cria o menu.
        menu = new JMenuBar();
        menu_simulacao = new JMenu("Simulação");
        menu_simulacao_ativar = new JMenuItem("Ativar");
        menu_simulacao_ativar.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				definirSimulacao(true);
			}
        });
        menu_simulacao_pausar = new JMenuItem("Pausar");
        menu_simulacao_pausar.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				definirSimulacao(false);
			}
        });
        menu_simulacao_ativar.setEnabled(false);
        menu_simulacao.add(menu_simulacao_ativar);
        menu_simulacao.addSeparator();
        menu_simulacao.add(menu_simulacao_pausar);
        menu.add(menu_simulacao);
        
        menu_corpos = new JMenu("Corpos");
        menu_corpos_adicionar = new JMenuItem("Adicionar");
        menu_corpos_adicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				definirEstado(ESTADO_POSICIONANDO_CORPO);
			}
        });
        menu_corpos_apagar = new JMenuItem("Apagar");
        menu_corpos_apagar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apagarSelecionados();
			}
        });
        menu_corpos_apagar.setEnabled(false);
        menu_corpos_limpar = new JMenuItem("Limpar");
        menu_corpos_limpar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apagarTodos();
			}
        });
        menu_corpos_limpar.setEnabled(false);
        menu_corpos.add(menu_corpos_adicionar);
        menu_corpos.addSeparator();
        menu_corpos.add(menu_corpos_apagar);
        menu_corpos.add(menu_corpos_limpar);
        menu.add(menu_corpos);

        menu_alterar = new JMenu("Alterar");
        menu_alterar_velocidade = new JMenuItem("Velocidade");
        menu_alterar_velocidade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alterarVelocidade();
			}
        });
        menu_alterar_raio = new JMenuItem("Raio");
        menu_alterar_raio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alterarRaio();
			}
        });
        menu_alterar_massa = new JMenuItem("Massa");
        menu_alterar_massa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alterarMassa();
			}
        });
        menu_alterar_cor = new JMenuItem("Cor");
        menu_alterar_cor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alterarCor();
			}
        });
        menu_alterar.add(menu_alterar_velocidade);
        menu_alterar.add(menu_alterar_raio);
        menu_alterar.addSeparator();
        menu_alterar.add(menu_alterar_massa);
        menu_alterar.addSeparator();
        menu_alterar.add(menu_alterar_cor);
        menu.add(menu_alterar);

        menu_exibir = new JMenu("Exibir");
        menu_exibir_velocidades = new JCheckBoxMenuItem("Velocidades", true);
        menu_exibir_velocidades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redesenhar();
			}
        });
        menu_exibir_velocidades_angulares = new JCheckBoxMenuItem("Velocidades angulares", false);
        menu_exibir_velocidades_angulares.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redesenhar();
			}
        });
        menu_exibir.add(menu_exibir_velocidades);
        menu_exibir.add(menu_exibir_velocidades_angulares);
        menu.add(menu_exibir);

        menu_sobre = new JMenu("Sobre");
        menu_sobre_licenca = new JMenuItem("Licença");
        menu_sobre_licenca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mostrarLicenca();
			}
        });
        menu_sobre.add(menu_sobre_licenca);
        menu.add(menu_sobre);
        
        setJMenuBar(menu);

        // cria o simulador com os dados do sistema solar.
        simulador = new Simulador();
        simulador.adicionarOuvinte(this);
        /* Sol:      */ simulador.adicionarCorpo(new Corpo(0, 0, 0, 0, 1.9891E30, 50*0.696E9, Color.yellow)); // adiciona o sol.
        /* Mercúrio: */ simulador.adicionarCorpo(new Corpo(5.889E10, 0, 0, -48.99E3, 3.3022E23, 500*2.4397E6, Color.lightGray));
        /* Vênus:    */ simulador.adicionarCorpo(new Corpo(1.082E11, 0, 0, -35.02E3, 4.868E24, 500*6.0518E6, Color.white));
        /* Terra     */ simulador.adicionarCorpo(new Corpo(1.496E11, 0, 0, -29.783E3, 5.9736E24, 500*6.371E6, Color.blue));
        /* Marte:    */ simulador.adicionarCorpo(new Corpo(2.279E11, 0, 0, -24.077E3, 6.4185E23, 500*3.396E6, Color.red));

        // cria o timer de sincronismo.
        sincronismo = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gatilho();
            }
        });
        sincronismo.start();
        
        alterouSelecao();
    }
    
    private String escreverTempo() {
    	String tempo = new String();
    	if (total_anos > 0) tempo = tempo + total_anos + "a ";
    	if (total_dias > 0 || tempo.length() > 0) tempo = tempo + total_dias + "d ";
    	//if (total_horas > 0 || tempo.length() > 0) tempo = tempo + total_horas + "h ";
    	//if (total_minutos > 0 || tempo.length() > 0) tempo = tempo + total_minutos + "min ";
    	//if (total_segundos > 0 || tempo.length() > 0) tempo = tempo + total_segundos + "s ";
    	return tempo;
    }

	private void mostrarLicenca() {
		status = simulando;
		definirSimulacao(false);
		JOptionPane.showMessageDialog(this, "O programa Simulador de Gravitação, de autoria de Felipe Michels Fontoura, é\nlicenciado nos termos da licença CC-GNU GPL.\n\nO Simulador de Gravitação é um software livre; você pode redistribui-lo\ne/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como\npublicada pela Fundação do Software Livre (FSF), quer seja na versão 2 da\nLicença, quer seja em qualquer versão posterior.\n\nEste programa é distribuido na esperança que possa ser util,\nmas SEM QUALQUER GARANTIA; sem sequer garantias implícitas de\nADEQUAÇÂO ao MERCADO ou a qualquer APLICAÇÃO EM PARTICULAR. Veja\na Licença Pública Geral GNU para maiores detalhes.\n\nVocê deve ter recebido uma cópia da Licença Pública Geral GNU\njunto com este programa; se não, escreva para a Fundação do Software\nLivre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA", "Sobre > Licença", JOptionPane.INFORMATION_MESSAGE);
		if (status) definirSimulacao(true);
	}

	private void alterarMassa() {
		status = simulando;
		definirSimulacao(false);
		String retorno = (String) JOptionPane.showInputDialog(this, "Digite a nova massa (em kg)", "Corpos > Alterar > Massa", JOptionPane.QUESTION_MESSAGE, null, null, formatador.format(selecionados.getFirst().massa.v));
		if (retorno != null) {
			try {
				double massa = Double.parseDouble(retorno);
				if (massa <= 0) {
					JOptionPane.showMessageDialog(this, "A massa deve ser um número superior a zero!", "Erro!", JOptionPane.WARNING_MESSAGE);
				} else {
					selecionados.getFirst().massa.v = massa;
				}
			} catch (Throwable t) {
				JOptionPane.showMessageDialog(this, "O dado fornecido é inválido.", "Erro!", JOptionPane.WARNING_MESSAGE);
			}
		}
		if (status) definirSimulacao(true);
		redesenhar();
		
	}

	private void alterarRaio() {
		status = simulando;
		definirSimulacao(false);
		String retorno = (String) JOptionPane.showInputDialog(this, "Digite o novo raio (em m)", "Corpos > Alterar > Raio", JOptionPane.QUESTION_MESSAGE, null, null, formatador.format(selecionados.getFirst().raio.v));
		if (retorno != null) {
			try {
				double raio = Double.parseDouble(retorno);
				if (raio <= 0) {
					JOptionPane.showMessageDialog(this, "O raio deve ser um número superior a zero!", "Erro!", JOptionPane.WARNING_MESSAGE);
				} else {
					Corpo selecionado = selecionados.getFirst();
					boolean valido = true;
					double dx, dy;
					for (int i = 0; i < simulador.total_corpos; i ++) if (simulador.corpos[i] != selecionado){
						dx = selecionado.posicao.x - simulador.corpos[i].posicao.x;
						dy = selecionado.posicao.y - simulador.corpos[i].posicao.y;
						if (Math.sqrt(dx*dx + dy*dy) < raio + simulador.corpos[i].raio.v)
							valido = false;
					}
					if (valido) {
						selecionado.raio.v = raio;
					} else {
						JOptionPane.showMessageDialog(this, "O valor fornecido é grande demais!", "Erro!", JOptionPane.WARNING_MESSAGE);
					}
				}
			} catch (Throwable t) {
				JOptionPane.showMessageDialog(this, "O dado fornecido é inválido." + retorno, "Erro!", JOptionPane.WARNING_MESSAGE);
			}
		}
		if (status) definirSimulacao(true);
		redesenhar();
		
	}

	private void alterarCor() {
		status = simulando;
		definirSimulacao(false);
		Color cor = JColorChooser.showDialog(this, "Corpos > Alterar > Cor", selecionados.getFirst().cor);
		if (cor != null) {
			for (Corpo s : selecionados)
				s.cor = cor;
		}
		if (status) definirSimulacao(true);
		redesenhar();
	}

	private void alterarVelocidade() {
		selecionado = selecionados.getFirst();
		double modulo_anterior = Math.sqrt(selecionado.velocidade.x*selecionado.velocidade.x + selecionado.velocidade.y*selecionado.velocidade.y);
		boolean status = simulando;
		definirSimulacao(false);
		String retorno = (String) JOptionPane.showInputDialog(this, "Digite a nova velocidade (em m/s)", "Corpos > Alterar > Velocidade", JOptionPane.QUESTION_MESSAGE, null, null, formatador.format(modulo_anterior));
		if (retorno != null) {
			try {
				double velocidade = Double.parseDouble(retorno);
				if (modulo_anterior == 0) {
					selecionado.velocidade.x = -selecionado.posicao.x;
					selecionado.velocidade.y = -selecionado.posicao.y;
					modulo_anterior = Math.sqrt(selecionado.velocidade.x*selecionado.velocidade.x + selecionado.velocidade.y*selecionado.velocidade.y);
				}
				if (modulo_anterior != 0) {
					selecionado.velocidade.x *= velocidade / modulo_anterior;
					selecionado.velocidade.y *= velocidade / modulo_anterior;
				}
			} catch (Throwable t) {
				JOptionPane.showMessageDialog(this, "O dado fornecido é inválido.", "Erro!", JOptionPane.WARNING_MESSAGE);
			}
		}
		if (status) definirSimulacao(true);
		redesenhar();
	}

	public void apagarSelecionados() {
		while (!selecionados.isEmpty()) {
			Corpo corpo = selecionados.getFirst();
			selecionados.removeFirst();
			simulador.removerCorpo(corpo);
		}
		redesenhar();
	}

	public void apagarTodos() {
		while (simulador.total_corpos > 0) {
			simulador.removerCorpo(simulador.corpos[0]);
		}
		redesenhar();
	}

	public void init() {
    }

    public void start() {
    }

    private void alterouSelecao() {
    	menu_alterar_cor.setEnabled(!selecionados.isEmpty());
    	menu_alterar_massa.setEnabled(selecionados.size() == 1);
    	menu_alterar_raio.setEnabled(selecionados.size() == 1);
    	menu_alterar_velocidade.setEnabled(selecionados.size() == 1);
        menu_corpos_apagar.setEnabled(!selecionados.isEmpty());
    }
    
    // métodos chamados para redesenhar os gráficos na tela.
    public void redesenhar() {
		if (painel.getWidth() == 0 || painel.getHeight() == 0) return;
		if (tela == null || tela.getWidth() != painel.getWidth() || tela.getHeight() != painel.getHeight()) {
	    	tela = new BufferedImage(painel.getWidth(), painel.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	        g2d = tela.createGraphics();
	        g2d.setColor(Color.black);
	        g2d.translate(painel.getWidth() / 2, painel.getHeight() / 2);
	        g2d.fillRect(-painel.getWidth(), -painel.getHeight(), 2*painel.getWidth(), 2*painel.getHeight());
		}
        g2d.setColor(Color.black);
        g2d.fillRect(-painel.getWidth(), -painel.getHeight(), 2*painel.getWidth(), 2*getHeight());
    	g2d.setColor(Color.white);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        desenhar(g2d);
        painel.alterarImagem(tela);
    }
    private void desenhar(Graphics2D g2d) {
        simulador.centroGravitacional(centro);
        for (int i = 0; i < simulador.total_corpos; i ++) {
        	simulador.corpos[i].posicao.x -= centro.x;
        	simulador.corpos[i].posicao.y -= centro.y;
        }
        centro.x = 0;
        centro.y = 0;
        int x1, x2, y1, y2;
        double cmx = 0;
        double cmy = 0;
        double m = 0;
        if (! selecionados.isEmpty()) {
        	for (Corpo selecionado : selecionados) {
            	m += selecionado.massa.v;
            	cmx += selecionado.massa.v * selecionado.posicao.x;
            	cmy += selecionado.massa.v * selecionado.posicao.y;
        	}
            if (m == 0) {
            	cmx = 0;
            	cmy = 0;
            } else {
            	cmx /= m;
            	cmy /= m;
            }
            x1 = (int) Math.round(escala.v*(-centro.x + cmx));
            y1 = (int) Math.round(escala.v*(-centro.y + cmy));
        	g2d.setColor(Color.gray);
        	g2d.drawLine(-painel.getWidth(), y1, painel.getWidth(), y1);
        	g2d.drawLine(x1, -painel.getHeight(), x1, painel.getHeight());
        	for (Corpo selecionado : selecionados) {
	            x2 = (int) Math.round(escala.v*(-centro.x + selecionado.posicao.x));
	            y2 = (int) Math.round(escala.v*(-centro.y + selecionado.posicao.y));
	            g2d.drawLine(x1, y1, x2, y2);
        	}
        }
        for (int i = 0; i < simulador.total_corpos; i ++) {
            x1 = (int) Math.floor(escala.v*(simulador.corpos[i].posicao.x - centro.x) - escalaRaios.v * simulador.corpos[i].raio.v);
            x2 = (int) Math.ceil(escala.v*(simulador.corpos[i].posicao.x - centro.x) + escalaRaios.v * simulador.corpos[i].raio.v);
            y1 = (int) Math.floor(escala.v*(simulador.corpos[i].posicao.y - centro.y) - escalaRaios.v * simulador.corpos[i].raio.v);
            y2 = (int) Math.ceil(escala.v*(simulador.corpos[i].posicao.y - centro.y) + escalaRaios.v * simulador.corpos[i].raio.v);
            g2d.setColor(simulador.corpos[i].cor);
            g2d.fillOval(x1, y1, x2-x1, y2-y1);
            if (selecionados.contains(simulador.corpos[i])) {
	            x1 = (int) Math.round(escala.v*(-centro.x + simulador.corpos[i].posicao.x));
	            y1 = (int) Math.round(escala.v*(-centro.y + simulador.corpos[i].posicao.y));
	            x2 = (int) Math.round(escalaVetores.v * simulador.corpos[i].velocidade.x + escala.v*(simulador.corpos[i].posicao.x - centro.x));
	            y2 = (int) Math.round(escalaVetores.v * simulador.corpos[i].velocidade.y + escala.v*(simulador.corpos[i].posicao.y - centro.y));
	            g2d.setColor(Color.white);
	            g2d.drawLine(x1, y1, x2, y2);
	            if (menu_exibir_velocidades.getState()) g2d.drawString(formatador.format(Math.sqrt(simulador.corpos[i].velocidade.x*simulador.corpos[i].velocidade.x + simulador.corpos[i].velocidade.y*simulador.corpos[i].velocidade.y)) + " m/s", (int)((simulador.corpos[i].raio.v + simulador.corpos[i].posicao.x) * escala.v), (int)((simulador.corpos[i].raio.v + simulador.corpos[i].posicao.y) * escala.v));
	            x1 = x2 - SELETOR_QUADRADINHO;
	            y1 = y2 - SELETOR_QUADRADINHO;
	            g2d.setColor(Color.white);
	            g2d.fillRect(x1, y1, 2 * SELETOR_QUADRADINHO, 2 * SELETOR_QUADRADINHO);
	            g2d.setColor(Color.black);
	            g2d.drawRect(x1, y1, 2 * SELETOR_QUADRADINHO, 2 * SELETOR_QUADRADINHO);
            }
        }
        if (selecionados.size() > 1) {
        	g2d.setColor(Color.gray);
            x1 = (int) Math.round(escala.v*(-centro.x + cmx));
            y1 = (int) Math.round(escala.v*(-centro.y + cmy));
        	if (menu_exibir_velocidades_angulares.getState())
        		for (Corpo selecionado : selecionados) {
	        		double px = selecionado.posicao.x - cmx;
	        		double py = selecionado.posicao.y - cmy;
	            	m = (py*selecionado.velocidade.x - px*selecionado.velocidade.y)/(px*px + py*py);
		            x2 = (int) Math.round(escala.v*(-centro.x + selecionado.posicao.x));
		            y2 = (int) Math.round(escala.v*(-centro.y + selecionado.posicao.y));
		            g2d.drawString(formatador.format(m) + " rad/s", (x1 + x2)/2, (y1 + y2)/2);
	        	}
        }
        if (estado == ESTADO_MULTISSELECAO) {
        	x1 = (mouse_x1 < mouse_x2) ? mouse_x1 : mouse_x2;
        	x2 = (mouse_x1 > mouse_x2) ? mouse_x1 : mouse_x2;
        	y1 = (mouse_y1 < mouse_y2) ? mouse_y1 : mouse_y2;
        	y2 = (mouse_y1 > mouse_y2) ? mouse_y1 : mouse_y2;
        	g2d.setColor(Color.white);
        	g2d.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
        if (estado == ESTADO_POSICIONANDO_CORPO) {
        	x1 = mouse_x1 - (int) Math.floor(escalaRaios.v * RAIO_NOVOS_CORPOS);
            x2 = mouse_x1 + (int) Math.ceil(escalaRaios.v * RAIO_NOVOS_CORPOS);
        	y1 = mouse_y1 - (int) Math.floor(escalaRaios.v * RAIO_NOVOS_CORPOS);
            y2 = mouse_y1 + (int) Math.ceil(escalaRaios.v * RAIO_NOVOS_CORPOS);
            g2d.setColor(Color.white);
            g2d.fillOval(x1, y1, x2-x1, y2-y1);
        }
        g2d.setColor(Color.white);
        g2d.drawString(escreverTempo(), -painel.getWidth()/2, painel.getHeight()/2);
    }
    
    // define o estado da simulação.
    public void definirSimulacao(boolean ativa) {
        if (ativa) {
        	simulando = true;
        	menu_simulacao_ativar.setEnabled(false);
        	menu_simulacao_pausar.setEnabled(true);
        } else {
        	simulando = false;
        	menu_simulacao_ativar.setEnabled(true);
        	menu_simulacao_pausar.setEnabled(false);
        }
    }

    public void definirEstado(int estado) {
		if (estado == ESTADO_POSICIONANDO_CORPO) {
			this.estado = ESTADO_POSICIONANDO_CORPO;
			redesenhar();
		}
	}
    
    public void gatilho() {
        if (simulando && estado != ESTADO_MUDANDO_VELOCIDADE && estado != ESTADO_POSICIONANDO_CORPO) {
        	total_segundos += intervalo*integracoes;
        	if (total_segundos >= 60) {
        		total_minutos += total_segundos / 60;
        		total_segundos %= 60;
        	}
        	if (total_minutos >= 60) {
        		total_horas += total_minutos / 60;
        		total_minutos %= 60;
        	}
        	if (total_horas >= 24) {
        		total_dias += total_horas / 24;
        		total_horas %= 24;
        	}
        	if (total_dias >= 365) {
        		total_anos += total_dias / 365;
        		total_dias %= 365;
        	}
            for (int i = 0; i < integracoes; i ++)
                simulador.gatilho(intervalo);
            redesenhar();
        }
    }

	public void adicionou(Corpo c) {
		if (simulador.total_corpos != 0) {
			menu_corpos_limpar.setEnabled(true);
		}
	}

	public void removeu(Corpo c) {
		if (selecionados.contains(c)) {
			selecionados.remove(c);
			alterouSelecao();
		}
		if (estado == ESTADO_MUDANDO_VELOCIDADE) {
			if (objeto == c) {
				objeto = null;
				estado = ESTADO_COMUM;
			}
		}
		
		if (simulador.total_corpos == 0) {
			menu_corpos_limpar.setEnabled(false);
		}
	}

	public void juntou(Corpo c1, Corpo c2) {
		if (selecionados.contains(c2)) {
			selecionados.remove(c2);
			if (!selecionados.contains(c1)) {
				selecionados.add(c1);
				alterouSelecao();
			}
		}
		if (estado == ESTADO_MUDANDO_VELOCIDADE) {
			if (objeto == c2 || objeto == c1) {
				objeto = null;
				estado = ESTADO_COMUM;
			}
		}
	}

	public void mouseClicked(MouseEvent evento_mouse) {
	}

	public void mouseEntered(MouseEvent evento_mouse) {
	}

	public void mouseExited(MouseEvent evento_mouse) {
	}

	public void mousePressed(MouseEvent evento_mouse) {
		if (estado == ESTADO_POSICIONANDO_CORPO) {
			if (evento_mouse.getButton() == MouseEvent.BUTTON3) {
				estado = ESTADO_COMUM;
				redesenhar();
			} else {
				double x, y, dx, dy;
				boolean valido = true;
				x = mouse_x1 / escala.v;
				y = mouse_y1 / escala.v;
				for (int i = 0; i < simulador.total_corpos; i ++) {
					dx = simulador.corpos[i].posicao.x - x;
					dy = simulador.corpos[i].posicao.y - y;
					if (Math.sqrt(dx*dx + dy*dy) < simulador.corpos[i].raio.v + RAIO_NOVOS_CORPOS) {
						valido = false;
					}
				}
				if (valido) {
					simulador.adicionarCorpo(new Corpo(x, y, 0, 0, MASSA_NOVOS_CORPOS, RAIO_NOVOS_CORPOS, Color.white));
					estado = ESTADO_COMUM;
					definirSimulacao(false);
					redesenhar();
				}
			}
		} else if (estado == ESTADO_COMUM) {
			int x, y, r;
			mouse_x1 = evento_mouse.getX() - painel.getWidth() / 2;
			mouse_y1 = evento_mouse.getY() - painel.getHeight() / 2;
			
			// verifica se está sobre um seletor de velocidade.
			Corpo selecionado = null;
			for (Corpo corpo : selecionados) {
	            x = (int) Math.round(escalaVetores.v * corpo.velocidade.x + escala.v*(corpo.posicao.x - centro.x)) - mouse_x1;
	            y = (int) Math.round(escalaVetores.v * corpo.velocidade.y + escala.v*(corpo.posicao.y - centro.y)) - mouse_y1;
	            if (x >= -SELETOR_QUADRADINHO && x <= SELETOR_QUADRADINHO && y >= -SELETOR_QUADRADINHO && y <= SELETOR_QUADRADINHO)
	            	selecionado = corpo;
			}
			
			if (selecionado != null) {
				// se estiver, muda o estado.
				estado = ESTADO_MUDANDO_VELOCIDADE;
				objeto = selecionado;
				redesenhar();
			} else {
				// se não estiver, seleciona ou desseleciona 
				for (int i = 0; i < simulador.total_corpos; i ++) {
		            x = (int) Math.round(escala.v*(simulador.corpos[i].posicao.x - centro.x)) - mouse_x1;
		            y = (int) Math.round(escala.v*(simulador.corpos[i].posicao.y - centro.y)) - mouse_y1;
		            r = (int) Math.ceil(escalaRaios.v*simulador.corpos[i].raio.v);
		            if (Math.sqrt(x*x + y*y) < r) {
		            	selecionado = simulador.corpos[i];
		            }
		        }
				if (!evento_mouse.isShiftDown()) selecionados.clear();
				if (selecionado != null) {
	        		if (selecionados.contains(selecionado)) selecionados.remove(selecionado);
	        		else selecionados.add(selecionado);
				} else {
					mouse_x2 = mouse_x1;
					mouse_y2 = mouse_y1;
					estado = ESTADO_MULTISSELECAO;
				}
				alterouSelecao();
				redesenhar();
			}
		}
	}

	public void mouseReleased(MouseEvent evento_mouse) {
		if (estado == ESTADO_MUDANDO_VELOCIDADE) {
			objeto.velocidade.x = (evento_mouse.getX() - painel.getWidth() / 2 - escala.v*(objeto.posicao.x - centro.x)) / escalaVetores.v;
			objeto.velocidade.y = (evento_mouse.getY() - painel.getHeight() / 2 - escala.v*(objeto.posicao.y - centro.y)) / escalaVetores.v;
			estado = ESTADO_COMUM;
			redesenhar();
		} else if (estado == ESTADO_MULTISSELECAO) {
			mouse_x2 = evento_mouse.getX() - painel.getWidth() / 2;
			mouse_y2 = evento_mouse.getY() - painel.getHeight() / 2;
			if (mouse_x1 > mouse_x2) {
				int temp = mouse_x2;
				mouse_x2 = mouse_x1;
				mouse_x1 = temp;
			}
			if (mouse_y1 > mouse_y2) {
				int temp = mouse_y2;
				mouse_y2 = mouse_y1;
				mouse_y1 = temp;
			}
			double xr1 = mouse_x1;
			double yr1 = mouse_y1;
			double xr2 = evento_mouse.getX();
			double yr2 = evento_mouse.getY();
	        for (int i = 0; i < simulador.total_corpos; i ++) {
	        	xr1 = Math.ceil(escala.v*(simulador.corpos[i].posicao.x - centro.x) - escalaRaios.v * simulador.corpos[i].raio.v);
	        	xr2 = Math.floor(escala.v*(simulador.corpos[i].posicao.x - centro.x) + escalaRaios.v * simulador.corpos[i].raio.v);
	        	yr1 = Math.ceil(escala.v*(simulador.corpos[i].posicao.y - centro.y) - escalaRaios.v * simulador.corpos[i].raio.v);
	        	yr2 = Math.floor(escala.v*(simulador.corpos[i].posicao.y - centro.y) + escalaRaios.v * simulador.corpos[i].raio.v);
	        	if (xr1 >= mouse_x1 && yr1 >= mouse_y1 && xr2 <= mouse_x2 && yr2 <= mouse_y2) {
	        		if (selecionados.contains(simulador.corpos[i])) selecionados.remove(simulador.corpos[i]);
	        		else selecionados.add(simulador.corpos[i]);
	        	}
	        }
	        alterouSelecao();
			estado = ESTADO_COMUM;
			redesenhar();
		}
	}

	public void mouseDragged(MouseEvent evento_mouse) {
		if (estado == ESTADO_MUDANDO_VELOCIDADE) {
			objeto.velocidade.x = (evento_mouse.getX() - painel.getWidth() / 2 - escala.v*(objeto.posicao.x - centro.x)) / escalaVetores.v;
			objeto.velocidade.y = (evento_mouse.getY() - painel.getHeight() / 2 - escala.v*(objeto.posicao.y - centro.y)) / escalaVetores.v;
			redesenhar();
		} else if (estado == ESTADO_MULTISSELECAO) {
			mouse_x2 = evento_mouse.getX() - painel.getWidth() / 2;
			mouse_y2 = evento_mouse.getY() - painel.getHeight() / 2;
			redesenhar();
		}
		
	}

	public void mouseMoved(MouseEvent evento_mouse) {
		if (estado == ESTADO_MULTISSELECAO) {
			mouse_x2 = evento_mouse.getX() - painel.getWidth() / 2;
			mouse_y2 = evento_mouse.getY() - painel.getHeight() / 2;
			redesenhar();
		} else {
			mouse_x1 = evento_mouse.getX() - painel.getWidth() / 2;
			mouse_y1 = evento_mouse.getY() - painel.getHeight() / 2;
			if (estado == ESTADO_POSICIONANDO_CORPO) {
				redesenhar();
			}
		}
	}
}