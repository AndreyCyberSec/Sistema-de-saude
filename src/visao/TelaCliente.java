package visao;

import visao.TelaSistemaSaude;
import modelo.Pedido;
import modelo.Remedio;
import modelo.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaCliente extends JFrame {

    private static final Color AZUL_SUS = new Color(0, 94, 184);
    private static final Color FUNDO    = new Color(232, 244, 248);
    private static final Color BRANCO   = Color.WHITE;
    private static final Color VERDE    = new Color(0, 150, 0);

    private final TelaSistemaSaude sistema = TelaSistemaSaude.getInstance();
    private final Usuario    usuarioAtual;
    private final int        ubsId;

    // Carrinho de medicamentos do usuário
    private final List<Remedio> carrinho = new ArrayList<>();

    public TelaCliente(Usuario usuario, int ubsId) {
        this.usuarioAtual = usuario;
        this.ubsId        = ubsId;

        setTitle("Sistema SUS – Farmácia Popular");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        carregarIcone();

        construirTela();
        setVisible(true);
    }

    /** Reconstrói toda a tela (chamado após adicionar item ao carrinho). */
    private void construirTela() {
        getContentPane().removeAll();
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(FUNDO);
        main.add(criarHeader(), BorderLayout.NORTH);
        main.add(criarConteudo(), BorderLayout.CENTER);
        add(main);
        revalidate();
        repaint();
    }

    private JPanel criarHeader() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRANCO);
        header.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        // Esquerda: logo + botão "Meus Pedidos"
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        esquerda.setBackground(BRANCO);
        esquerda.add(carregarLogo());

        JButton btnPedidos = new JButton("🔍 Meus Pedidos");
        btnPedidos.setFont(new Font("Arial", Font.BOLD, 13));
        btnPedidos.setForeground(BRANCO);
        btnPedidos.setBackground(AZUL_SUS);
        btnPedidos.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        btnPedidos.setFocusPainted(false);
        btnPedidos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPedidos.addActionListener(e -> verMeusPedidos());
        esquerda.add(btnPedidos);

        // Direita: nome, cotas e carrinho
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        direita.setBackground(BRANCO);

        JLabel lblNome = new JLabel("Olá, " + usuarioAtual.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 16));
        lblNome.setForeground(AZUL_SUS);

        JLabel lblCotas = new JLabel(
            "Cotas: " + usuarioAtual.getCotasDisponiveis() + "/" + usuarioAtual.getCotaMensal());
        lblCotas.setFont(new Font("Arial", Font.BOLD, 16));
        lblCotas.setForeground(VERDE);

        // Contador do carrinho
        JPanel carrinhoPanel = criarIconeCarrinho();

        direita.add(lblNome);
        direita.add(lblCotas);
        direita.add(carrinhoPanel);

        header.add(esquerda, BorderLayout.WEST);
        header.add(direita,  BorderLayout.EAST);
        wrapper.add(header,  BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel criarIconeCarrinho() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        painel.setBackground(BRANCO);
        painel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Número de itens no carrinho
        JLabel contador = new JLabel(String.valueOf(carrinho.size()));
        contador.setFont(new Font("Arial", Font.BOLD, 13));
        contador.setForeground(AZUL_SUS);
        contador.setHorizontalAlignment(SwingConstants.CENTER);
        contador.setPreferredSize(new Dimension(26, 26));
        contador.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        try {
            File f = new File("imagens/cart.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                painel.add(contador);
                painel.add(new JLabel(new ImageIcon(img)));
            } else {
                painel.add(contador);
                painel.add(new JLabel("🛒"));
            }
        } catch (Exception e) {
            painel.add(contador);
            painel.add(new JLabel("🛒"));
        }

        painel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { verCarrinho(); }
        });
        return painel;
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel(new BorderLayout(30, 30));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BRANCO);
        container.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Barra de título + campo de pesquisa
        container.add(criarBarraTopo(), BorderLayout.NORTH);

        // Corpo: lista de remédios (esquerda) + detalhes (direita)
        List<Remedio> remedios = sistema.getRemediosPorUnidade(ubsId);
        JPanel[] painelDetalhes = { new JPanel() };
        Remedio[] selecionado   = { remedios.isEmpty() ? null : remedios.get(0) };

        JPanel listaRemedios = new JPanel();
        listaRemedios.setLayout(new BoxLayout(listaRemedios, BoxLayout.Y_AXIS));
        listaRemedios.setBackground(BRANCO);

        for (Remedio r : remedios) {
            listaRemedios.add(criarCardRemedio(r, selecionado, painelDetalhes));
            listaRemedios.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(listaRemedios);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Painel de detalhes (direita)
        JPanel painelDir = new JPanel(new BorderLayout());
        painelDir.setBackground(BRANCO);
        painelDir.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        painelDir.setPreferredSize(new Dimension(420, 0));

        painelDetalhes[0] = criarPainelDetalhes(selecionado[0]);
        painelDir.add(painelDetalhes[0], BorderLayout.CENTER);

        JPanel corpo = new JPanel(new BorderLayout(20, 0));
        corpo.setBackground(BRANCO);
        corpo.add(scroll,    BorderLayout.CENTER);
        corpo.add(painelDir, BorderLayout.EAST);

        container.add(corpo, BorderLayout.CENTER);
        conteudo.add(container, BorderLayout.CENTER);
        return conteudo;
    }

    private JPanel criarBarraTopo() {
        JPanel painel = new JPanel(new BorderLayout(15, 0));
        painel.setBackground(BRANCO);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.addActionListener(e -> { new TelaSelecaoUBS(usuarioAtual); dispose(); });

        JLabel titulo = new JLabel("Remédios Disponíveis", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(AZUL_SUS);

        painel.add(btnVoltar, BorderLayout.WEST);
        painel.add(titulo,    BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarCardRemedio(Remedio r, Remedio[] selecionado, JPanel[] painelDetalhes) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BRANCO);

        JLabel lblNome = new JLabel(r.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        lblNome.setForeground(AZUL_SUS);

        String detalhe = r.getTipo().isEmpty() ? "" : r.getTipo();
        if (!r.getGramatura().isEmpty()) detalhe += (detalhe.isEmpty() ? "" : " – ") + r.getGramatura();
        if (detalhe.isEmpty()) detalhe = "Medicamento gratuito";

        JLabel lblDetalhe = new JLabel(detalhe);
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);

        info.add(lblNome);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(lblDetalhe);

        // Botão "+" rápido
        JButton btnAdd = new JButton("+");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 22));
        btnAdd.setForeground(AZUL_SUS);
        btnAdd.setBackground(BRANCO);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            carrinho.add(r);
            construirTela(); // atualiza contador do carrinho
        });

        card.add(info,   BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.EAST);

        // Clique no card exibe os detalhes à direita
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selecionado[0] = r;
                Container pai  = painelDetalhes[0].getParent();
                pai.remove(painelDetalhes[0]);
                painelDetalhes[0] = criarPainelDetalhes(r);
                pai.add(painelDetalhes[0], BorderLayout.CENTER);
                pai.revalidate();
                pai.repaint();
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

    private JPanel criarPainelDetalhes(Remedio r) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(BRANCO);

        if (r == null) {
            JLabel aviso = new JLabel("Nenhum remédio disponível");
            aviso.setForeground(Color.GRAY);
            aviso.setAlignmentX(Component.CENTER_ALIGNMENT);
            painel.add(Box.createVerticalGlue());
            painel.add(aviso);
            painel.add(Box.createVerticalGlue());
            return painel;
        }

        // Nome
        JLabel lblNome = new JLabel(r.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 22));
        lblNome.setForeground(AZUL_SUS);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descrição
        JTextArea txtDesc = new JTextArea(
            r.getDescricao().isEmpty() ? "Sem descrição disponível." : r.getDescricao());
        txtDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDesc.setForeground(Color.DARK_GRAY);
        txtDesc.setBackground(BRANCO);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tipo e gramatura
        String det = r.getTipo() + (r.getGramatura().isEmpty() ? "" : " – " + r.getGramatura());
        JLabel lblDet = new JLabel(det);
        lblDet.setFont(new Font("Arial", Font.BOLD, 12));
        lblDet.setForeground(AZUL_SUS);
        lblDet.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Receita
        if (r.isPrecisaReceita()) {
            JLabel lblReceita = new JLabel("⚠ Exige receita médica");
            lblReceita.setFont(new Font("Arial", Font.BOLD, 13));
            lblReceita.setForeground(Color.RED);
            lblReceita.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(lblNome);
            painel.add(Box.createRigidArea(new Dimension(0, 10)));
            painel.add(txtDesc);
            painel.add(Box.createRigidArea(new Dimension(0, 6)));
            painel.add(lblDet);
            painel.add(Box.createRigidArea(new Dimension(0, 6)));
            painel.add(lblReceita);
        } else {
            painel.add(lblNome);
            painel.add(Box.createRigidArea(new Dimension(0, 10)));
            painel.add(txtDesc);
            painel.add(Box.createRigidArea(new Dimension(0, 6)));
            painel.add(lblDet);
        }

        // Estoque
        JLabel lblEst = new JLabel("Disponível: " + r.getEstoque() + " unidade(s)");
        lblEst.setFont(new Font("Arial", Font.PLAIN, 13));
        lblEst.setForeground(Color.DARK_GRAY);
        lblEst.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGratis = new JLabel("Medicamento gratuito via SUS");
        lblGratis.setFont(new Font("Arial", Font.BOLD, 13));
        lblGratis.setForeground(VERDE);
        lblGratis.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Spinner de quantidade + botão adicionar
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Math.max(1, r.getEstoque()), 1));
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        spinner.setMaximumSize(new Dimension(80, 35));

        JButton btnAdd = new JButton("+ Carrinho");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setForeground(BRANCO);
        btnAdd.setBackground(AZUL_SUS);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            int qtd = (Integer) spinner.getValue();
            for (int i = 0; i < qtd; i++) carrinho.add(r);
            construirTela();
        });

        JPanel acaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acaoPanel.setBackground(BRANCO);
        acaoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        acaoPanel.add(spinner);
        acaoPanel.add(btnAdd);

        painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(lblEst);
        painel.add(Box.createRigidArea(new Dimension(0, 4)));
        painel.add(lblGratis);
        painel.add(Box.createRigidArea(new Dimension(0, 18)));
        painel.add(acaoPanel);
        painel.add(Box.createVerticalGlue());

        return painel;
    }

    private void verCarrinho() {
        JFrame frame = new JFrame("Carrinho – " + usuarioAtual.getNome());
        frame.setSize(750, 580);
        frame.setLocationRelativeTo(this);
        frame.getContentPane().setBackground(FUNDO);

        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Título
        JLabel titulo = new JLabel("Seu Carrinho", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(AZUL_SUS);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        main.add(titulo, BorderLayout.NORTH);

        // Lista de itens
        JPanel itens = new JPanel();
        itens.setLayout(new BoxLayout(itens, BoxLayout.Y_AXIS));
        itens.setBackground(FUNDO);

        for (int i = 0; i < carrinho.size(); i++) {
            final int idx = i;
            Remedio r = carrinho.get(i);

            JPanel item = new JPanel(new BorderLayout(10, 0));
            item.setBackground(BRANCO);
            item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_SUS, 1, true),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel lblNome = new JLabel(r.getNome()
                + (r.isPrecisaReceita() ? "  ⚠ Requer receita" : ""));
            lblNome.setFont(new Font("Arial", Font.BOLD, 15));
            lblNome.setForeground(AZUL_SUS);

            JButton btnRem = new JButton("✕");
            btnRem.setFont(new Font("Arial", Font.BOLD, 13));
            btnRem.setForeground(BRANCO);
            btnRem.setBackground(new Color(220, 53, 69));
            btnRem.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            btnRem.setFocusPainted(false);
            btnRem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnRem.addActionListener(e -> {
                carrinho.remove(idx);
                frame.dispose();
                verCarrinho();
            });

            item.add(lblNome, BorderLayout.CENTER);
            item.add(btnRem,  BorderLayout.EAST);
            itens.add(item);
            itens.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scroll = new JScrollPane(itens);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        main.add(scroll, BorderLayout.CENTER);

        // Resumo e botão finalizar
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBackground(BRANCO);
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2, true),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        JLabel lblTotal = new JLabel("Total: " + carrinho.size() + " medicamento(s)");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(AZUL_SUS);

        JLabel lblCota = new JLabel("Cotas disponíveis: " + usuarioAtual.getCotasDisponiveis()
            + " / " + usuarioAtual.getCotaMensal());
        lblCota.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCota.setForeground(Color.GRAY);

        JButton btnFinalizar = new JButton("Finalizar Pedido");
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 16));
        btnFinalizar.setForeground(BRANCO);
        btnFinalizar.setBackground(AZUL_SUS);
        btnFinalizar.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        btnFinalizar.setFocusPainted(false);
        btnFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFinalizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFinalizar.addActionListener(e -> {
            if (finalizarPedido()) frame.dispose();
        });

        rodape.add(lblTotal);
        rodape.add(Box.createRigidArea(new Dimension(0, 6)));
        rodape.add(lblCota);
        rodape.add(Box.createRigidArea(new Dimension(0, 14)));
        rodape.add(btnFinalizar);

        main.add(rodape, BorderLayout.SOUTH);

        frame.add(main);
        frame.setVisible(true);
    }


    private boolean finalizarPedido() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Carrinho vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int totalItens = carrinho.size();

        // Verifica cotas
        if (!usuarioAtual.temCotaDisponivel(totalItens)) {
            JOptionPane.showMessageDialog(this,
                "Cota insuficiente!\nCotas disponíveis: " + usuarioAtual.getCotasDisponiveis()
                + "\nQuantidade solicitada: " + totalItens,
                "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Verifica estoque de cada remédio (conta duplicatas)
        Map<Integer, Integer> quantidades = new HashMap<>();
        for (Remedio r : carrinho)
            quantidades.put(r.getId(), quantidades.getOrDefault(r.getId(), 0) + 1);

        for (Map.Entry<Integer, Integer> entry : quantidades.entrySet()) {
            Remedio remedioAtual = null;
            for (Remedio r : sistema.getRemedios()) {
                if (r.getId() == entry.getKey()) { remedioAtual = r; break; }
            }
            if (remedioAtual == null || remedioAtual.getEstoque() < entry.getValue()) {
                String nome = remedioAtual != null ? remedioAtual.getNome() : "ID " + entry.getKey();
                JOptionPane.showMessageDialog(this,
                    "Estoque insuficiente para: " + nome, "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Cria o pedido
        Pedido pedido = sistema.criarPedido(usuarioAtual, new ArrayList<>(carrinho));

        // Desconta estoque
        for (Map.Entry<Integer, Integer> entry : quantidades.entrySet())
            sistema.diminuirEstoque(entry.getKey(), entry.getValue());

        // Desconta cotas
        sistema.consumirCota(usuarioAtual.getCpf(), totalItens);
        usuarioAtual.consumirCota(totalItens);

        // Define status do pedido
        if (!pedido.precisaReceita()) {
            sistema.aprovarPedido(pedido.getId());
            JOptionPane.showMessageDialog(this,
                "Pedido #" + pedido.getId() + " realizado!\n"
                + "Status: Aprovado – dirija-se à UBS para retirada.\n"
                + "Cotas restantes: " + usuarioAtual.getCotasDisponiveis(),
                "Pedido Confirmado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Pedido #" + pedido.getId() + " realizado!\n"
                + "Status: Pendente – aguardando aprovação do administrador\n"
                + "(um ou mais remédios exigem receita médica).\n"
                + "Cotas restantes: " + usuarioAtual.getCotasDisponiveis(),
                "Pedido Confirmado", JOptionPane.INFORMATION_MESSAGE);
        }

        carrinho.clear();
        construirTela();
        return true;
    }

    private void verMeusPedidos() {
        JDialog dialog = new JDialog(this, "Meus Pedidos", true);
        dialog.setSize(850, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(FUNDO);

        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("Meus Pedidos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(AZUL_SUS);
        main.add(titulo, BorderLayout.NORTH);

        JPanel listaPedidos = new JPanel();
        listaPedidos.setLayout(new BoxLayout(listaPedidos, BoxLayout.Y_AXIS));
        listaPedidos.setBackground(FUNDO);

        boolean temPedidos = false;
        for (Pedido p : sistema.getPedidos()) {
            if (!p.getUsuario().getCpf().equals(usuarioAtual.getCpf())) continue;
            temPedidos = true;

            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(BRANCO);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_SUS, 2, true),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(BRANCO);

            JLabel lblId = new JLabel("Pedido #" + p.getId());
            lblId.setFont(new Font("Arial", Font.BOLD, 18));
            lblId.setForeground(AZUL_SUS);

            JLabel lblItens = new JLabel(p.getRemedios().size() + " medicamento(s)");
            lblItens.setFont(new Font("Arial", Font.PLAIN, 13));
            lblItens.setForeground(Color.DARK_GRAY);

            Color corStatus = corParaStatus(p.getStatus());
            JLabel lblStatus = new JLabel("Status: " + p.getStatus());
            lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
            lblStatus.setForeground(corStatus);

            info.add(lblId);
            info.add(Box.createRigidArea(new Dimension(0, 4)));
            info.add(lblItens);
            info.add(Box.createRigidArea(new Dimension(0, 4)));
            info.add(lblStatus);

            card.add(info, BorderLayout.CENTER);
            listaPedidos.add(card);
            listaPedidos.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!temPedidos) {
            JLabel aviso = new JLabel("Você ainda não realizou nenhum pedido.");
            aviso.setFont(new Font("Arial", Font.PLAIN, 15));
            aviso.setForeground(Color.GRAY);
            aviso.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaPedidos.add(Box.createVerticalGlue());
            listaPedidos.add(aviso);
        }

        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scroll, BorderLayout.CENTER);

        dialog.add(main);
        dialog.setVisible(true);
    }

    private Color corParaStatus(String status) {
        return switch (status) {
            case "Concluído" -> AZUL_SUS;
            case "Aprovado"  -> VERDE;
            case "Cancelado" -> Color.RED;
            default          -> new Color(255, 140, 0); // Pendente = laranja
        };
    }

    private JLabel carregarLogo() {
        try {
            File f = new File("imagens/logo.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(75, 38, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        JLabel l = new JLabel("SUS");
        l.setFont(new Font("Arial", Font.BOLD, 18));
        l.setForeground(AZUL_SUS);
        return l;
    }

    private JButton criarBotaoVoltar() {
        JButton btn = new JButton("←");
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setForeground(AZUL_SUS);
        btn.setBackground(BRANCO);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
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