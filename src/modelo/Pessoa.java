package modelo;

/**
 * Classe abstrata Pessoa.
 * 
 * Representa uma pessoa no sistema. É a classe base (pai) que
 * contém atributos comuns a todos os tipos de usuário.
 */
public abstract class Pessoa {

    // Atributos encapsulados (private = só acessíveis por getters/setters)
    private String nome;
    private String cpf;
    private String email;
    private String senha;


    public Pessoa(String nome, String cpf, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

   

    public String getNome()              { return nome; }
    public String getCpf()               { return cpf; }
    public String getEmail()             { return email; }
    public String getSenha()             { return senha; }
    public void   setSenha(String senha) { this.senha = senha; }

    /**
     * Método abstrato — cada subclasse DEVE implementar.
     * Admin retorna "Administrador", Usuario retorna "Paciente: <nome>"
     */
    public abstract String getDescricao();
}