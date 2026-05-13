package modelo;


public class Admin extends Pessoa {

    /**
     * Construtor do Admin.
     * O admin padrão do sistema é criado com cpf="administrador" e senha="123".
     */
    public Admin(String nome, String cpf, String email, String senha) {
        super(nome, cpf, email, senha); 
    }

   
    @Override
    public String getDescricao() {
        return "Administrador";
    }
}