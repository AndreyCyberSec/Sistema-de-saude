package modelo;

import java.time.YearMonth;

public class Usuario extends Pessoa {

    // Controle de cotas mensais
    private int cotaMensal;      // quantos remédios pode pedir por mês (padrão: 10)
    private int cotaUtilizada;   // quantos já pediu no mês atual
    private String mesReferencia; // mês de referência para renovação automática

    /**
     * Construtor principal — cria um usuário com cota padrão de 10.
     */
    public Usuario(String nome, String cpf, String email, String senha) {
        super(nome, cpf, email, senha); 
        this.cotaMensal    = 10;
        this.cotaUtilizada = 0;
        this.mesReferencia = YearMonth.now().toString();
    }

    /**
     * Construtor completo — usado ao carregar dados salvos.
     */
    public Usuario(String nome, String cpf, String email, String senha,
                   int cotaMensal, int cotaUtilizada, String mesReferencia) {
        super(nome, cpf, email, senha);
        this.cotaMensal    = cotaMensal > 0 ? cotaMensal : 10;
        this.cotaUtilizada = cotaUtilizada;
        this.mesReferencia = mesReferencia != null ? mesReferencia : YearMonth.now().toString();
    }

    private void verificarRenovacaoCota() {
        String mesAtual = YearMonth.now().toString();
        if (!mesAtual.equals(mesReferencia)) {
            cotaUtilizada = 0;
            mesReferencia = mesAtual;
        }
    }

    public int getCotasDisponiveis() {
        verificarRenovacaoCota();
        return cotaMensal - cotaUtilizada;
    }

    public boolean temCotaDisponivel(int quantidade) {
        verificarRenovacaoCota();
        return (cotaUtilizada + quantidade) <= cotaMensal;
    }

    public void consumirCota(int quantidade) {
        verificarRenovacaoCota();
        cotaUtilizada += quantidade;
    }

    public int    getCotaMensal()                    { return cotaMensal; }
    public void   setCotaMensal(int cotaMensal)      { this.cotaMensal = Math.max(1, cotaMensal); }
    public int    getCotaUtilizada()                 { return cotaUtilizada; }
    public void   setCotaUtilizada(int v)            { this.cotaUtilizada = v; }
    public String getMesReferencia()                 { return mesReferencia; }
    public void   setMesReferencia(String m)         { this.mesReferencia = m; }

   
    @Override
    public String getDescricao() {
        return "Paciente: " + getNome();
    }
}