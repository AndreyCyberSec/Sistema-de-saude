package modelo;

public class Remedio {

    private int    idRemedio;
    private String nome;
    private int    estoque;
    private boolean precisaReceita; // true = exige receita médica
    private int    ubsId;           // ID da UBS a qual este remédio pertence
    private String descricao;
    private String tipo;       // ex: "Comprimido", "Cápsula"
    private String gramatura;  // ex: "500mg", "20mg"

    public Remedio(int id, String nome, int estoque, boolean precisaReceita,
                   int ubsId, String descricao, String tipo, String gramatura) {
        this.idRemedio = id;
        this.nome = nome;
        this.estoque = estoque;
        this.precisaReceita = precisaReceita;
        this.ubsId  = ubsId;
        this.descricao = descricao  != null ? descricao  : ""; //condição ternaria para ter um codigo limpo
        this.tipo = tipo       != null ? tipo : "";
        this.gramatura = gramatura  != null ? gramatura  : "";
    }
    public int     getId()              { return idRemedio; }
    public String  getNome()            { return nome; }
    public int     getEstoque()         { return estoque; }
    public boolean isPrecisaReceita()   { return precisaReceita; }
    public int     getUbsId()           { return ubsId; }
    public String  getDescricao()       { return descricao; }
    public String  getTipo()            { return tipo; }
    public String  getGramatura()       { return gramatura; }

    public void setNome(String nome)                   { this.nome = nome; }
    public void setEstoque(int estoque)                { this.estoque = estoque; }
    public void setPrecisaReceita(boolean v)           { this.precisaReceita = v; }
    public void setUbsId(int ubsId)                    { this.ubsId = ubsId; }
    public void setDescricao(String descricao)         { this.descricao = descricao; }
    public void setTipo(String tipo)                   { this.tipo = tipo; }
    public void setGramatura(String gramatura)         { this.gramatura = gramatura; }
}