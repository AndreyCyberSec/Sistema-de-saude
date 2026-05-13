package modelo;

public class Unidade {

    private int    idUnidade;
    private String nome;
    private String logradouro;
    private String numero;
    private String bairro;
    private String estado;
    private String cep;

    /**
     * Construtor completo da UBS.
     */
    public Unidade(int id, String nome, String logradouro, String numero,
               String bairro, String estado, String cep) {
        this.idUnidade  = id;
        this.nome       = nome;
        this.logradouro = logradouro != null ? logradouro : ""; //condição ternaria para evitar muito if e else
        this.numero     = numero     != null ? numero     : "";
        this.bairro     = bairro     != null ? bairro     : "";
        this.estado     = estado     != null ? estado     : "";
        this.cep        = cep        != null ? cep        : "";
    }

    public int    getId()          { return idUnidade; }
    public String getNome()        { return nome; }
    public String getLogradouro()  { return logradouro; }
    public String getNumero()      { return numero; }
    public String getBairro()      { return bairro; }
    public String getEstado()      { return estado; }
    public String getCep()         { return cep; }

    public String getEnderecoCompleto() {
        return logradouro + ", " + numero + " - " + bairro + ", " + estado
               + " | CEP: " + cep;
    }

    public void setNome(String nome)             { this.nome = nome; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public void setNumero(String numero)         { this.numero = numero; }
    public void setBairro(String bairro)         { this.bairro = bairro; }
    public void setEstado(String estado)         { this.estado = estado; }
    public void setCep(String cep)               { this.cep = cep; }
}