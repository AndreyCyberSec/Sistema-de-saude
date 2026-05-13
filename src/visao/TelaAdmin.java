package visao;

import visao.TelaSistemaSaude;
import modelo.Pedido;
import modelo.Remedio;
import modelo.Unidade;
import modelo.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaAdmin extends JFrame {

    private static final Color AZUL_SUS = new Color(0, 94, 184);
    private static final Color FUNDO    = new Color(232, 244, 248);
    private static final Color BRANCO   = Color.WHITE;

    private final TelaSistemaSaude sistema = TelaSistemaSaude.getInstance();

    public TelaAdmin() {
        setTitle("Sistema SUS – Área Administrativa");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        carregarIcone();

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(FUNDO);
        main.add(criarHeader(),   BorderLayout.NORTH);
        main.add(criarConteudo(), BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // ─── Header ───────────────────────────────────────────────────────────────

    private JPanel criarHeader() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 50, 10, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRANCO);
        header.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        esquerda.setBackground(BRANCO);
        esquerda.add(carregarLogo());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        direita.setBackground(BRANCO);
        JLabel lblAdmin = new JLabel("Olá, Administrador");
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 18));
        lblAdmin.setForeground(AZUL_SUS);
        direita.add(lblAdmin);

        header.add(esquerda, BorderLayout.WEST);
        header.add(direita,  BorderLayout.EAST);
        wrapper.add(header,  BorderLayout.CENTER);
        return wrapper;
    }

    // ─── Conteúdo: cards de opções ────────────────────────────────────────────

    private JPanel criarConteudo() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));

        JPanel container = new JPanel(new BorderLayout(0, 25));
        container.setBackground(BRANCO);
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título + botão voltar
        JPanel topoPanel = new JPanel(new BorderLayout());
        topoPanel.setBackground(BRANCO);

        JButton btnVoltar = criarBotaoVoltar();
        btnVoltar.addActionListener(e -> { new TelaInicial(); dispose(); });

        JLabel titulo = new JLabel("Painel Administrativo", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(AZUL_SUS);

        topoPanel.add(btnVoltar, BorderLayout.WEST);
        topoPanel.add(titulo,    BorderLayout.CENTER);
        container.add(topoPanel, BorderLayout.NORTH);

        // Grid de cards — 2 colunas, 3 linhas = 6 opções
        JPanel grid = new JPanel(new GridLayout(0, 2, 25, 18));
        grid.setBackground(BRANCO);

        grid.add(criarCard("1", "Gerenciar Remédios",
            "Cadastrar, editar ou excluir medicamentos das UBS",
            this::gerenciarRemedios));

        grid.add(criarCard("2", "Listar Estoque",
            "Visualizar o estoque atual de todos os remédios",
            this::listarEstoque));

        grid.add(criarCard("3", "Gerenciar Cotas",
            "Definir a cota mensal de medicamentos de cada paciente",
            this::gerenciarCotas));

        grid.add(criarCard("4", "Revisar Pedidos",
            "Aprovar pedidos pendentes que exigem receita médica",
            this::revisarPedidos));

        grid.add(criarCard("5", "Concluir Pedidos",
            "Confirmar a retirada de medicamentos pelos pacientes",
            this::concluirPedidos));

        grid.add(criarCard("6", "Consultar Pedidos",
            "Visualizar o histórico completo de todos os pedidos",
            this::consultarPedidos));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scroll, BorderLayout.CENTER);
        wrapper.add(container, BorderLayout.CENTER);
        return wrapper;
    }

    /** Cria um card clicável para o painel de opções. */
    private JPanel criarCard(String numero, String titulo, String descricao, Runnable acao) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNum = new JLabel(numero);
        lblNum.setFont(new Font("Arial", Font.BOLD, 26));
        lblNum.setForeground(AZUL_SUS);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BRANCO);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_SUS);

        JLabel lblDesc = new JLabel("<html>" + descricao + "</html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDesc.setForeground(Color.GRAY);

        info.add(lblTitulo);
        info.add(Box.createRigidArea(new Dimension(0, 6)));
        info.add(lblDesc);

        card.add(lblNum, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { acao.run(); }
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


    private void gerenciarRemedios() {
        JDialog dialog = criarDialog("Gerenciar Remédios", 900, 580);

        JPanel main = criarMainDialog("Remédios Cadastrados");

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Remedio r : sistema.getRemedios()) {
            model.addElement(String.format("ID: %d | %s | UBS: %d | Estoque: %d | Receita: %s",
                r.getId(), r.getNome(), r.getUbsId(), r.getEstoque(),
                r.isPrecisaReceita() ? "Sim" : "Não"));
        }
        JList<String> lista = criarLista(model);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        botoes.setBackground(FUNDO);

        JButton btnAdd = criarBotaoAcao("Cadastrar");
        btnAdd.addActionListener(e -> { dialog.dispose(); formRemedio(null); gerenciarRemedios(); });

        JButton btnEdit = criarBotaoAcao("Editar");
        btnEdit.addActionListener(e -> {
            String sel = lista.getSelectedValue();
            if (sel == null) { aviso(dialog, "Selecione um remédio!"); return; }
            int id = extrairId(sel);
            for (Remedio r : sistema.getRemedios()) {
                if (r.getId() == id) { dialog.dispose(); formRemedio(r); gerenciarRemedios(); return; }
            }
        });

        JButton btnDel = criarBotaoAcao("Excluir");
        btnDel.addActionListener(e -> {
            String sel = lista.getSelectedValue();
            if (sel == null) { aviso(dialog, "Selecione um remédio!"); return; }
            int id = extrairId(sel);
            int conf = JOptionPane.showConfirmDialog(dialog,
                "Confirmar exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                sistema.removerRemedio(id);
                dialog.dispose();
                gerenciarRemedios();
            }
        });

        botoes.add(btnAdd);
        botoes.add(btnEdit);
        botoes.add(btnDel);

        main.add(new JScrollPane(lista), BorderLayout.CENTER);
        main.add(botoes, BorderLayout.SOUTH);
        dialog.add(main);
        dialog.setVisible(true);
    }

    /** Formulário para cadastrar ou editar um remédio. */
    private void formRemedio(Remedio rem) {
        boolean editando = rem != null;
        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField txtNome      = new JTextField(editando ? rem.getNome()      : "");
        JTextField txtEstoque   = new JTextField(editando ? String.valueOf(rem.getEstoque()) : "");
        JTextField txtDescricao = new JTextField(editando ? rem.getDescricao() : "");
        JTextField txtTipo      = new JTextField(editando ? rem.getTipo()      : "");
        JTextField txtGramatura = new JTextField(editando ? rem.getGramatura() : "");
        JCheckBox  chkReceita   = new JCheckBox("", editando && rem.isPrecisaReceita());

        // as 2 UBS fixas
        String[] ubsOpcoes = { "1 – UBS Jordanópolis", "2 – UBS Vila Mariana" };
        JComboBox<String> cmbUbs = new JComboBox<>(ubsOpcoes);
        if (editando && rem.getUbsId() == 2) cmbUbs.setSelectedIndex(1);

        painel.add(new JLabel("Nome:"));          painel.add(txtNome);
        painel.add(new JLabel("Estoque:"));       painel.add(txtEstoque);
        painel.add(new JLabel("UBS:"));           painel.add(cmbUbs);
        painel.add(new JLabel("Precisa Receita:")); painel.add(chkReceita);
        painel.add(new JLabel("Descrição:"));     painel.add(txtDescricao);
        painel.add(new JLabel("Tipo:"));          painel.add(txtTipo);
        painel.add(new JLabel("Gramatura:"));     painel.add(txtGramatura);

        int res = JOptionPane.showConfirmDialog(this, painel,
            editando ? "Editar Remédio" : "Cadastrar Remédio",
            JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            try {
                int estoque = Integer.parseInt(txtEstoque.getText().trim());
                int ubsId   = cmbUbs.getSelectedIndex() + 1; // índice 0→UBS1, 1→UBS2

                if (editando) {
                    sistema.atualizarRemedio(rem.getId(),
                        txtNome.getText().trim(), estoque, chkReceita.isSelected(),
                        ubsId, txtDescricao.getText().trim(),
                        txtTipo.getText().trim(), txtGramatura.getText().trim());
                    JOptionPane.showMessageDialog(this, "Remédio atualizado!");
                } else {
                    sistema.cadastrarRemedio(txtNome.getText().trim(), estoque,
                        chkReceita.isSelected(), ubsId,
                        txtDescricao.getText().trim(),
                        txtTipo.getText().trim(), txtGramatura.getText().trim());
                    JOptionPane.showMessageDialog(this, "Remédio cadastrado!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Estoque deve ser um número inteiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarEstoque() {
        JDialog dialog = criarDialog("Estoque", 750, 480);
        JPanel main    = criarMainDialog("Estoque de Medicamentos");

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Remedio r : sistema.getRemedios()) {
            String ubs = r.getUbsId() == 1 ? "UBS Jordanópolis" : "UBS Vila Mariana";
            model.addElement(String.format("%-30s | %-20s | Estoque: %3d | Receita: %s",
                r.getNome(), ubs, r.getEstoque(), r.isPrecisaReceita() ? "Sim" : "Não"));
        }

        main.add(new JScrollPane(criarLista(model)), BorderLayout.CENTER);
        dialog.add(main);
        dialog.setVisible(true);
    }

    /**
     * Permite ao admin ajustar a cota mensal de cada paciente.
     * A cota padrão é 10 remédios por mês.
     */
    private void gerenciarCotas() {
        JDialog dialog = criarDialog("Gerenciar Cotas", 850, 520);
        JPanel main    = criarMainDialog("Cotas dos Pacientes");

        List<Usuario> usuarios = sistema.getUsuarios();

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Usuario u : usuarios) {
            model.addElement(String.format("CPF: %s | %-25s | Cotas: %d/%d disponíveis",
                u.getCpf(), u.getNome(), u.getCotasDisponiveis(), u.getCotaMensal()));
        }
        JList<String> lista = criarLista(model);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        botoes.setBackground(FUNDO);

        JButton btnDefinir = criarBotaoAcao("Definir Cota");
        btnDefinir.addActionListener(e -> {
            String sel = lista.getSelectedValue();
            if (sel == null) { aviso(dialog, "Selecione um usuário!"); return; }

            String cpf = sel.split("CPF: ")[1].split(" ")[0];
            String valorStr = JOptionPane.showInputDialog(dialog,
                "Nova cota mensal (quantidade de remédios):", "Definir Cota",
                JOptionPane.QUESTION_MESSAGE);

            if (valorStr != null && !valorStr.trim().isEmpty()) {
                try {
                    int nova = Integer.parseInt(valorStr.trim());
                    if (nova < 1) throw new NumberFormatException();
                    sistema.definirCota(cpf, nova);
                    JOptionPane.showMessageDialog(dialog,
                        "Cota definida para " + nova + " remédio(s)/mês!");
                    dialog.dispose();
                    gerenciarCotas();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Informe um número inteiro positivo!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botoes.add(btnDefinir);
        main.add(new JScrollPane(lista), BorderLayout.CENTER);
        main.add(botoes, BorderLayout.SOUTH);
        dialog.add(main);
        dialog.setVisible(true);
    }

    private void revisarPedidos() {
        JDialog dialog = criarDialog("Revisar Pedidos", 1000, 580);
        JPanel main    = criarMainDialog("Pedidos Pendentes de Aprovação");

        JPanel listaPedidos = new JPanel();
        listaPedidos.setLayout(new BoxLayout(listaPedidos, BoxLayout.Y_AXIS));
        listaPedidos.setBackground(FUNDO);

        boolean temPendentes = false;
        for (Pedido p : sistema.getPedidos()) {
            if (!"Pendente".equals(p.getStatus()) || !p.precisaReceita()) continue;
            temPendentes = true;

            JPanel card = criarCardPedido(p);

            // Botão Aprovar
            JButton btnAprovar = new JButton("Aprovar");
            btnAprovar.setFont(new Font("Arial", Font.BOLD, 14));
            btnAprovar.setForeground(BRANCO);
            btnAprovar.setBackground(new Color(0, 150, 0));
            btnAprovar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            btnAprovar.setFocusPainted(false);
            btnAprovar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAprovar.addActionListener(e -> {
                sistema.aprovarPedido(p.getId());
                JOptionPane.showMessageDialog(dialog, "Pedido #" + p.getId() + " aprovado!");
                dialog.dispose();
                revisarPedidos();
            });

            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            botoes.setBackground(BRANCO);
            botoes.add(btnAprovar);
            card.add(botoes, BorderLayout.EAST);

            listaPedidos.add(card);
            listaPedidos.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!temPendentes) {
            JLabel aviso = new JLabel("Nenhum pedido pendente de aprovação.");
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

    private void concluirPedidos() {
        JDialog dialog = criarDialog("Concluir Pedidos", 1000, 580);
        JPanel main    = criarMainDialog("Pedidos Aprovados – Confirmar Retirada");

        JPanel listaPedidos = new JPanel();
        listaPedidos.setLayout(new BoxLayout(listaPedidos, BoxLayout.Y_AXIS));
        listaPedidos.setBackground(FUNDO);

        boolean temAprovados = false;
        for (Pedido p : sistema.getPedidos()) {
            if (!"Aprovado".equals(p.getStatus())) continue;
            temAprovados = true;

            JPanel card = criarCardPedido(p);

            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            botoes.setBackground(BRANCO);

            // Botão Cancelar
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
            btnCancelar.setForeground(BRANCO);
            btnCancelar.setBackground(new Color(220, 53, 69));
            btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            btnCancelar.setFocusPainted(false);
            btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCancelar.addActionListener(e -> {
                int conf = JOptionPane.showConfirmDialog(dialog,
                    "Cancelar pedido #" + p.getId() + "?\nEstoque e cotas serão devolvidos.",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    // Devolve estoque
                    Map<Integer, Integer> qtds = new HashMap<>();
                    for (Remedio r : p.getRemedios())
                        qtds.put(r.getId(), qtds.getOrDefault(r.getId(), 0) + 1);
                    for (Map.Entry<Integer, Integer> entry : qtds.entrySet())
                        sistema.aumentarEstoque(entry.getKey(), entry.getValue());
                    // Devolve cotas
                    sistema.devolverCota(p.getUsuario().getCpf(), p.getRemedios().size());
                    sistema.cancelarPedido(p.getId());
                    JOptionPane.showMessageDialog(dialog, "Pedido cancelado. Estoque e cotas devolvidos.");
                    dialog.dispose();
                    concluirPedidos();
                }
            });

            // Botão Confirmar Retirada
            JButton btnConcluir = new JButton("Confirmar Retirada");
            btnConcluir.setFont(new Font("Arial", Font.BOLD, 13));
            btnConcluir.setForeground(BRANCO);
            btnConcluir.setBackground(new Color(0, 150, 0));
            btnConcluir.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            btnConcluir.setFocusPainted(false);
            btnConcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnConcluir.addActionListener(e -> {
                sistema.concluirPedido(p.getId());
                JOptionPane.showMessageDialog(dialog, "Pedido #" + p.getId() + " concluído!");
                dialog.dispose();
                concluirPedidos();
            });

            botoes.add(btnCancelar);
            botoes.add(btnConcluir);
            card.add(botoes, BorderLayout.EAST);

            listaPedidos.add(card);
            listaPedidos.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!temAprovados) {
            JLabel aviso = new JLabel("Nenhum pedido aguardando retirada.");
            aviso.setFont(new Font("Arial", Font.PLAIN, 15));
            aviso.setForeground(Color.GRAY);
            aviso.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaPedidos.add(aviso);
        }

        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scroll, BorderLayout.CENTER);
        dialog.add(main);
        dialog.setVisible(true);
    }

    private void consultarPedidos() {
        JDialog dialog = criarDialog("Consultar Pedidos", 1000, 580);
        JPanel main    = criarMainDialog("Histórico de Todos os Pedidos");

        JPanel listaPedidos = new JPanel();
        listaPedidos.setLayout(new BoxLayout(listaPedidos, BoxLayout.Y_AXIS));
        listaPedidos.setBackground(FUNDO);

        List<Pedido> pedidos = sistema.getPedidos();
        if (pedidos.isEmpty()) {
            JLabel aviso = new JLabel("Nenhum pedido registrado ainda.");
            aviso.setFont(new Font("Arial", Font.PLAIN, 15));
            aviso.setForeground(Color.GRAY);
            aviso.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaPedidos.add(aviso);
        } else {
            for (Pedido p : pedidos) {
                listaPedidos.add(criarCardPedido(p));
                listaPedidos.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scroll, BorderLayout.CENTER);
        dialog.add(main);
        dialog.setVisible(true);
    }

    /** Cria um card visual para exibir informações de um pedido. */
    private JPanel criarCardPedido(Pedido p) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AZUL_SUS, 2, true),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BRANCO);

        JLabel lblId = new JLabel("Pedido #" + p.getId());
        lblId.setFont(new Font("Arial", Font.BOLD, 18));
        lblId.setForeground(AZUL_SUS);

        JLabel lblPac = new JLabel(
            "Paciente: " + p.getUsuario().getNome() + " (CPF: " + p.getUsuario().getCpf() + ")");
        lblPac.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPac.setForeground(Color.GRAY);

        JLabel lblItens = new JLabel(p.getRemedios().size() + " medicamento(s)");
        lblItens.setFont(new Font("Arial", Font.PLAIN, 13));
        lblItens.setForeground(Color.DARK_GRAY);

        Color corStatus = corParaStatus(p.getStatus());
        JLabel lblStatus = new JLabel("● " + p.getStatus());
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(corStatus);

        info.add(lblId);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(lblPac);
        info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(lblItens);
        info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(lblStatus);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JDialog criarDialog(String titulo, int w, int h) {
        JDialog d = new JDialog(this, titulo, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(FUNDO);
        return d;
    }

    private JPanel criarMainDialog(String tituloStr) {
        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titulo = new JLabel(tituloStr, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(AZUL_SUS);
        main.add(titulo, BorderLayout.NORTH);
        return main;
    }

    private JList<String> criarLista(DefaultListModel<String> model) {
        JList<String> lista = new JList<>(model);
        lista.setFont(new Font("Arial", Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return lista;
    }

    private JButton criarBotaoAcao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(BRANCO);
        btn.setBackground(AZUL_SUS);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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

    private JLabel carregarLogo() {
        try {
            File f = new File("imagens/logo.png");
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(80, 40, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        JLabel l = new JLabel("SUS");
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(AZUL_SUS);
        return l;
    }

    private Color corParaStatus(String status) {
        return switch (status) {
            case "Concluído" -> AZUL_SUS;
            case "Aprovado"  -> new Color(0, 150, 0);
            case "Cancelado" -> Color.RED;
            default          -> new Color(255, 140, 0);
        };
    }

    private int extrairId(String item) {
        return Integer.parseInt(item.split("ID: ")[1].split(" ")[0]);
    }

    private void aviso(JDialog parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void carregarIcone() {
        try { setIconImage(ImageIO.read(new File("imagens/icon.png"))); }
        catch (Exception ignored) {}
    }
}