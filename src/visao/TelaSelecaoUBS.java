package visao;

import visao.TelaSistemaSaude;
import modelo.Unidade;
import modelo.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class TelaSelecaoUBS extends JFrame {

    private static final Color AZUL_SUS = new Color(0, 94, 184);
    private static final Color FUNDO    = new Color(232, 244, 248);
    private static final Color BRANCO   = Color.WHITE;

    private final TelaSistemaSaude sistema = TelaSistemaSaude.getInstance();
    private final Usuario    usuarioAtual;

    // Painel de detalhes à direita — atualizado ao clicar numa UBS
    private JPanel painelDetalhes;

    public TelaSelecaoUBS(Usuario usuario) {
        this.usuarioAtual = usuario;

        setTitle("Sistema SUS – Selecionar Unidade");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        carregarIcone();

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(FUNDO);
        main.add(criarHeader(), BorderLayout.NORTH);
        main.add(criarConteudo(), BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    private JPanel criarHeader() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 50, 10, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRANCO);
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        // Lado esquerdo: logo
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        esquerda.setBackground(BRANCO);
        esquerda.add(carregarLogo());

        // Lado direito: saudação
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        direita.setBackground(BRANCO);
        JLabel saudacao = new JLabel("Olá, " + usuarioAtual.getNome());
        saudacao.setFont(new Font("Arial", Font.BOLD, 18));
        saudacao.setForeground(AZUL_SUS);
        direita.add(saudacao);

        header.add(esquerda, BorderLayout.WEST);
        header.add(direita,  BorderLayout.EAST);
        wrapper.add(header,  BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarConteudo() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));

        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setBackground(BRANCO);
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título + botão voltar
        container.add(criarTituloBar(), BorderLayout.NORTH);

        // Dois painéis: lista de UBS (esquerda) + detalhes (direita)
        JPanel corpo = new JPanel(new BorderLayout(20, 0));
        corpo.setBackground(BRANCO);

        List<Unidade> ubsList = sistema.getUnidades();
        Unidade[] selecionada = { ubsList.isEmpty() ? null : ubsList.get(0) };

        // Painel de detalhes — começa mostrando a primeira UBS
        painelDetalhes = new JPanel(new BorderLayout());
        painelDetalhes.setBackground(BRANCO);
        painelDetalhes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        painelDetalhes.setPreferredSize(new Dimension(380, 0));

        if (selecionada[0] != null) {
            preencherDetalhes(selecionada[0]);
        }

        // Lista de cards das UBS
        JPanel listaUBS = new JPanel();
        listaUBS.setLayout(new BoxLayout(listaUBS, BoxLayout.Y_AXIS));
        listaUBS.setBackground(BRANCO);

        int numero = 1;
        for (Unidade ubs : ubsList) {
            listaUBS.add(criarCardUBS(ubs, numero++, selecionada));
            listaUBS.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JScrollPane scroll = new JScrollPane(listaUBS);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        corpo.add(scroll,         BorderLayout.CENTER);
        corpo.add(painelDetalhes, BorderLayout.EAST);

        container.add(corpo, BorderLayout.CENTER);
        wrapper.add(container, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarTituloBar() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BRANCO);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.addActionListener(e -> { new TelaInicial(); dispose(); });

        JLabel titulo = new JLabel("Selecione a Unidade", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(AZUL_SUS);

        painel.add(btnVoltar, BorderLayout.WEST);
        painel.add(titulo,    BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarCardUBS(Unidade ubs, int numero, Unidade[] selecionada) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ícone da UBS
        JLabel icone = carregarIconeUBS(numero);

        // Informações da UBS
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BRANCO);

        JLabel lblNome = new JLabel(ubs.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 20));
        lblNome.setForeground(AZUL_SUS);

        JLabel lblEnd = new JLabel(ubs.getEnderecoCompleto());
        lblEnd.setFont(new Font("Arial", Font.PLAIN, 13));
        lblEnd.setForeground(Color.GRAY);

        info.add(lblNome);
        info.add(Box.createRigidArea(new Dimension(0, 5)));
        info.add(lblEnd);

        card.add(icone, BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);

        // Clique no card atualiza o painel de detalhes
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selecionada[0] = ubs;
                preencherDetalhes(ubs);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
                info.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(BRANCO);
                info.setBackground(BRANCO);
            }
        });

        return card;
    }

    /**
     * Preenche o painel de detalhes com as informações da UBS clicada.
     * Limpa o painel anterior e reconstrói o conteúdo.
     */
    private void preencherDetalhes(Unidade ubs) {
        painelDetalhes.removeAll();

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(BRANCO);

        // Nome da UBS
        JLabel lblNome = new JLabel(ubs.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 22));
        lblNome.setForeground(AZUL_SUS);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Imagem da UBS (usa local.png como padrão)
        JLabel lblImagem = carregarImagemUBS();

        // Endereço
        JLabel lblEnd = new JLabel(
            "<html><b>Endereço:</b><br>" + ubs.getEnderecoCompleto() + "</html>");
        lblEnd.setFont(new Font("Arial", Font.PLAIN, 13));
        lblEnd.setForeground(Color.DARK_GRAY);
        lblEnd.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botão de seleção
        JButton btnSelecionar = new JButton("Selecionar Unidade");
        btnSelecionar.setFont(new Font("Arial", Font.BOLD, 15));
        btnSelecionar.setForeground(BRANCO);
        btnSelecionar.setBackground(AZUL_SUS);
        btnSelecionar.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btnSelecionar.setFocusPainted(false);
        btnSelecionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelecionar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSelecionar.addActionListener(e -> {
            new TelaCliente(usuarioAtual, ubs.getId());
            dispose();
        });

        conteudo.add(lblNome);
        conteudo.add(Box.createRigidArea(new Dimension(0, 15)));
        if (lblImagem != null) conteudo.add(lblImagem);
        conteudo.add(Box.createRigidArea(new Dimension(0, 15)));
        conteudo.add(lblEnd);
        conteudo.add(Box.createRigidArea(new Dimension(0, 25)));
        conteudo.add(btnSelecionar);
        conteudo.add(Box.createVerticalGlue());

        painelDetalhes.add(conteudo, BorderLayout.NORTH);
        painelDetalhes.revalidate();
        painelDetalhes.repaint();
    }

    private JLabel carregarLogo() {
        try {
            File f = new File("imagens/logo.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(80, 40, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        JLabel lblSus = new JLabel("SUS");
        lblSus.setFont(new Font("Arial", Font.BOLD, 20));
        lblSus.setForeground(AZUL_SUS);
        return lblSus;
    }

    private JLabel carregarIconeUBS(int numero) {
        try {
            File f = new File("imagens/home.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        JLabel lblNum = new JLabel(String.valueOf(numero));
        lblNum.setFont(new Font("Arial", Font.BOLD, 26));
        lblNum.setForeground(AZUL_SUS);
        return lblNum;
    }

    private JLabel carregarImagemUBS() {
        try {
            File f = new File("imagens/local.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(200, 130, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(img));
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                return lbl;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private JButton criarBotaoVoltar() {
        JButton btn = new JButton("←");
        btn.setFont(new Font("Arial", Font.BOLD, 26));
        btn.setForeground(AZUL_SUS);
        btn.setBackground(BRANCO);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        return btn;
    }

    private void carregarIcone() {
        try { setIconImage(ImageIO.read(new File("imagens/icon.png"))); }
        catch (Exception ignored) {}
    }
}