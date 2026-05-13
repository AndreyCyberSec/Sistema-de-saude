package visao;

import visao.TelaSistemaSaude;
import modelo.Admin;
import modelo.Pessoa;
import modelo.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * TelaInicial — tela de Login e Cadastro do sistema.
 *
 * FLUXO:
 *  - Usuário digita CPF + senha → clica "Entrar"
 *  - Se for Admin  → abre TelaAdmin
 *  - Se for Paciente → abre TelaSelecaoUBS
 *  - Botão "Cadastrar-se" abre o formulário de cadastro
 *
 * LAYOUT: painel esquerdo (formulário) + painel direito (imagem SUS)
 */
public class TelaInicial extends JFrame {
    private static final Color AZUL_SUS  = new Color(0, 94, 184);
    private static final Color FUNDO     = new Color(232, 244, 248);
    private static final Color BRANCO    = Color.WHITE;
    private static final Color CINZA_BTN = new Color(100, 100, 100);

    private final TelaSistemaSaude sistema = TelaSistemaSaude.getInstance();

    public TelaInicial() {
        setTitle("Sistema SUS – Farmácia Popular");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FUNDO);
        carregarIcone();

        // Divide a janela em dois painéis lado a lado
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(criarPainelLogin());
        mainPanel.add(criarPainelImagem());

        add(mainPanel);
        setVisible(true);
    }

    private JPanel criarPainelLogin() {
        JPanel externo = new JPanel(new GridBagLayout());
        externo.setBackground(FUNDO);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        // Título
        JLabel titulo = new JLabel("Entrar");
        titulo.setFont(new Font("Arial", Font.BOLD, 42));
        titulo.setForeground(AZUL_SUS);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campos CPF e Senha
        JTextField    txtCpf   = criarCampo("CPF");
        JPasswordField txtSenha = criarCampoSenha("Senha");

        // Permite fazer login pressionando Enter
        txtCpf.addActionListener(e  -> fazerLogin(txtCpf, txtSenha));
        txtSenha.addActionListener(e -> fazerLogin(txtCpf, txtSenha));

        // Botão Entrar
        JButton btnEntrar = criarBotao("Entrar", AZUL_SUS);
        btnEntrar.addActionListener(e -> fazerLogin(txtCpf, txtSenha));

        // Botão Cadastrar-se
        JButton btnCadastrar = criarBotao("Cadastrar-se", CINZA_BTN);
        btnCadastrar.addActionListener(e -> abrirCadastro());

        // Monta o card
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(new JLabel(" "));          // espaço visual
        card.add(txtCpf);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(txtSenha);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(btnEntrar);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(btnCadastrar);

        externo.add(card);
        return externo;
    }

    private JPanel criarPainelImagem() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(FUNDO);

        try {
            File imgFile = new File("imagens/tela_inicial.png");
            if (imgFile.exists()) {
                Image img = ImageIO.read(imgFile)
                        .getScaledInstance(600, 500, Image.SCALE_SMOOTH);
                painel.add(new JLabel(new ImageIcon(img)));
            } else {
                // Fallback: texto grande com a sigla
                JLabel lblSus = new JLabel("SUS");
                lblSus.setFont(new Font("Arial", Font.BOLD, 120));
                lblSus.setForeground(AZUL_SUS);
                painel.add(lblSus);
            }
        } catch (Exception e) {
            System.err.println("Imagem não carregada: " + e.getMessage());
        }
        return painel;
    }

    private void fazerLogin(JTextField txtCpf, JPasswordField txtSenha) {
        String cpf   = txtCpf.getText().trim().toLowerCase();
        String senha = new String(txtSenha.getPassword());

        if (cpf.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Preencha CPF e Senha!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pessoa pessoa = sistema.autenticar(cpf, senha);

        if (pessoa == null) {
            JOptionPane.showMessageDialog(this,
                "CPF ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //verifica o tipo real do objeto
        if (pessoa instanceof Admin) {
            new TelaAdmin();
        } else {
            new TelaSelecaoUBS((Usuario) pessoa);
        }
        dispose();
    }
    private void abrirCadastro() {
        JDialog dialog = new JDialog(this, "Criar Conta", true);
        dialog.setSize(500, 480);
        dialog.setLocationRelativeTo(this);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(BRANCO);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Criar Conta");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(AZUL_SUS);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos do formulário (com placeholder via borda)
        JTextField    txtCpf   = criarCampoDialog("CPF (apenas números, 11 dígitos)");
        JTextField    txtNome  = criarCampoDialog("Nome Completo");
        JTextField    txtEmail = criarCampoDialog("E-mail");
        JPasswordField txtSenha = criarCampoSenhaDialog("Senha");

        JButton btnCadastrar = criarBotao("Cadastrar", AZUL_SUS);
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> {
            String cpf   = txtCpf.getText().trim();
            String nome  = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword());

            if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                sistema.cadastrarUsuario(cpf, nome, email, senha);
                JOptionPane.showMessageDialog(dialog,
                    "Cadastro realizado! Faça seu login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        painel.add(titulo);
        painel.add(Box.createRigidArea(new Dimension(0, 20)));
        painel.add(txtCpf);
        painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(txtNome);
        painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(txtEmail);
        painel.add(Box.createRigidArea(new Dimension(0, 12)));
        painel.add(txtSenha);
        painel.add(Box.createRigidArea(new Dimension(0, 25)));
        painel.add(btnCadastrar);

        dialog.add(painel);
        dialog.setVisible(true);
    }

    /** Campo de texto com título via borda (para o card de login principal). */
    private JTextField criarCampo(String titulo) {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(450, 50));
        campo.setMaximumSize(new Dimension(450, 50));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBackground(new Color(240, 240, 240));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    /** Campo de senha para o formulário principal. */
    private JPasswordField criarCampoSenha(String titulo) {
        JPasswordField campo = new JPasswordField();
        campo.setPreferredSize(new Dimension(450, 50));
        campo.setMaximumSize(new Dimension(450, 50));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBackground(new Color(240, 240, 240));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }

    /** Campo de texto para os dialogs (cadastro). */
    private JTextField criarCampoDialog(String titulo) {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(400, 45));
        campo.setMaximumSize(new Dimension(400, 45));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBackground(new Color(240, 240, 240));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return campo;
    }

    /** Campo de senha para os dialogs. */
    private JPasswordField criarCampoSenhaDialog(String titulo) {
        JPasswordField campo = new JPasswordField();
        campo.setPreferredSize(new Dimension(400, 45));
        campo.setMaximumSize(new Dimension(400, 45));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBackground(new Color(240, 240, 240));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        return campo;
    }

    /** Cria um botão padrão do sistema. */
    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(450, 45));
        btn.setMaximumSize(new Dimension(450, 45));
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(BRANCO);
        btn.setBackground(cor);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Carrega o ícone da janela. */
    private void carregarIcone() {
        try {
            setIconImage(ImageIO.read(new File("imagens/icon.png")));
        } catch (Exception e) {
            System.err.println("Ícone não encontrado.");
        }
    }
}