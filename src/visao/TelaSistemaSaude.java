package visao;

import infra.GerenciadorJSON;
import modelo.*;

import java.time.YearMonth;
import java.util.*;

/**
 * SistemaSUS — classe central que gerencia todos os dados e operações.
 *
 * CONCEITO POO: SINGLETON
 * - Existe apenas UMA instância desta classe em toda a aplicação
 * - Acesso via SistemaSUS.getInstance()
 * - Garante que todos usem os mesmos dados em memória
 *
 * CHAVES DO JSON:
 *   Unidade  → "idUnidade",  "nome", "logradouro", "numero", "bairro", "estado", "cep"
 *   Remedio  → "idRemedio",  "nome", "estoque", "precisaReceita", "ubsId", ...
 *   Pedido   → "idPedido",   "usuario", "remedios", "status"
 *   Usuario  → "tipo", "cpf", "nome", "email", "senha", "cotaMensal", ...
 */
public class TelaSistemaSaude {

    private static TelaSistemaSaude instancia;

    public static TelaSistemaSaude getInstance() {
        if (instancia == null) instancia = new TelaSistemaSaude();
        return instancia;
    }

    private Map<String, Object> dados;

    private TelaSistemaSaude() {
        dados = GerenciadorJSON.carregarDados();
    }

    private void salvar() {
        GerenciadorJSON.salvarDados(dados);
    }

    // AUTENTICACAO E CADASTRO

    @SuppressWarnings("unchecked")
    public Pessoa autenticar(String cpf, String senha) {
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : usuarios) {
            try {
                if (obj.get("cpf") == null || obj.get("senha") == null) continue;
                if (obj.get("cpf").toString().equalsIgnoreCase(cpf)) {
                    return obj.get("senha").equals(senha) ? construirPessoa(obj) : null;
                }
            } catch (Exception e) {
                System.err.println("Erro na autenticacao: " + e.getMessage());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void cadastrarUsuario(String cpf, String nome, String email, String senha) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("[0-9]+"))
            throw new IllegalArgumentException("CPF deve conter exatamente 11 digitos numericos!");
        if (senha == null || senha.trim().isEmpty())
            throw new IllegalArgumentException("A senha nao pode ser vazia!");

        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : usuarios) {
            if (obj.get("cpf") != null && obj.get("cpf").equals(cpf))
                throw new IllegalArgumentException("CPF ja cadastrado!");
        }

        Map<String, Object> novo = new HashMap<>();
        novo.put("tipo", "usuario");
        novo.put("cpf", cpf);
        novo.put("nome", nome);
        novo.put("email", email);
        novo.put("senha", senha);
        novo.put("cotaMensal", 10);
        novo.put("cotaUtilizada", 0);
        novo.put("mesReferencia", YearMonth.now().toString());
        usuarios.add(novo);
        salvar();
    }

    // USUARIOS

    @SuppressWarnings("unchecked")
    public List<Usuario> getUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : arr) {
            try {
                if (obj.get("cpf") == null || obj.get("senha") == null) continue;
                if (!"admin".equals(obj.get("tipo"))) lista.add(construirUsuario(obj));
            } catch (Exception e) {
                System.err.println("Usuario ignorado: " + e.getMessage());
            }
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public void definirCota(String cpf, int novaCota) {
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : usuarios) {
            try {
                if (obj.get("cpf") == null) continue;
                if (obj.get("cpf").equals(cpf)) { obj.put("cotaMensal", novaCota); break; }
            } catch (Exception e) { System.err.println("Erro ao definir cota: " + e.getMessage()); }
        }
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void consumirCota(String cpf, int quantidade) {
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : usuarios) {
            try {
                if (obj.get("cpf") == null) continue;
                if (obj.get("cpf").equals(cpf)) {
                    int atual = obj.get("cotaUtilizada") != null ? ((Number) obj.get("cotaUtilizada")).intValue() : 0;
                    obj.put("cotaUtilizada", atual + quantidade); break;
                }
            } catch (Exception e) { System.err.println("Erro ao consumir cota: " + e.getMessage()); }
        }
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void devolverCota(String cpf, int quantidade) {
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) dados.get("usuarios");
        for (Map<String, Object> obj : usuarios) {
            try {
                if (obj.get("cpf") == null) continue;
                if (obj.get("cpf").equals(cpf)) {
                    int atual = obj.get("cotaUtilizada") != null ? ((Number) obj.get("cotaUtilizada")).intValue() : 0;
                    obj.put("cotaUtilizada", Math.max(0, atual - quantidade)); break;
                }
            } catch (Exception e) { System.err.println("Erro ao devolver cota: " + e.getMessage()); }
        }
        salvar();
    }

    // UNIDADES (2 fixas)

    @SuppressWarnings("unchecked")
    public List<Unidade> getUnidades() {
        List<Unidade> lista = new ArrayList<>();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) dados.get("ubs");

        boolean temValidas = false;
        for (Map<String, Object> obj : arr) {
            if (obj.get("idUnidade") != null) { temValidas = true; break; }
        }
        if (!temValidas) {
            System.err.println("Unidades corrompidas - recriando...");
            arr.clear();
            arr.add(criarMapUnidade(1, "UBS Jordanopolis", "Viela Jangada Nova", "75", "Jd. Pres. Dutra", "SP", "04830-200"));
            arr.add(criarMapUnidade(2, "UBS Vila Mariana",  "Rua das Flores",     "123", "Vila Mariana",   "SP", "04567-890"));
            salvar();
        }

        for (Map<String, Object> obj : arr) {
            try {
                if (obj.get("idUnidade") == null || obj.get("nome") == null) continue;
                lista.add(new Unidade(
                    ((Number) obj.get("idUnidade")).intValue(),
                    (String) obj.get("nome"),
                    (String) obj.getOrDefault("logradouro", ""),
                    (String) obj.getOrDefault("numero",     ""),
                    (String) obj.getOrDefault("bairro",     ""),
                    (String) obj.getOrDefault("estado",     ""),
                    (String) obj.getOrDefault("cep",        "")
                ));
            } catch (Exception e) {
                System.err.println("Unidade ignorada: " + e.getMessage());
            }
        }
        return lista;
    }

    private Map<String, Object> criarMapUnidade(int id, String nome, String logradouro,
            String numero, String bairro, String estado, String cep) {
        Map<String, Object> u = new HashMap<>();
        u.put("idUnidade",  id);
        u.put("nome",       nome);
        u.put("logradouro", logradouro);
        u.put("numero",     numero);
        u.put("bairro",     bairro);
        u.put("estado",     estado);
        u.put("cep",        cep);
        return u;
    }

    // REMEDIOS

    @SuppressWarnings("unchecked")
    public List<Remedio> getRemedios() {
        List<Remedio> lista = new ArrayList<>();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) dados.get("remedios");
        for (Map<String, Object> obj : arr) {
            try {
                // Compatibilidade: aceita tanto "idRemedio" (novo) quanto "id" (antigo)
                Object idObj = obj.get("idRemedio") != null ? obj.get("idRemedio") : obj.get("id");
                if (idObj == null || obj.get("nome") == null
                        || obj.get("estoque") == null || obj.get("ubsId") == null) continue;

                // Normaliza: garante que a chave "idRemedio" exista para salvas futuras
                if (obj.get("idRemedio") == null) obj.put("idRemedio", idObj);

                lista.add(new Remedio(
                    ((Number) idObj).intValue(),
                    (String) obj.get("nome"),
                    ((Number) obj.get("estoque")).intValue(),
                    obj.get("precisaReceita") != null && (Boolean) obj.get("precisaReceita"),
                    ((Number) obj.get("ubsId")).intValue(),
                    (String) obj.getOrDefault("descricao", ""),
                    (String) obj.getOrDefault("tipo",      ""),
                    (String) obj.getOrDefault("gramatura", "")
                ));
            } catch (Exception e) {
                System.err.println("Remedio ignorado: " + e.getMessage());
            }
        }
        return lista;
    }

    public List<Remedio> getRemediosPorUnidade(int unidadeId) {
        List<Remedio> filtrados = new ArrayList<>();
        for (Remedio r : getRemedios())
            if (r.getUbsId() == unidadeId) filtrados.add(r);
        return filtrados;
    }

    @SuppressWarnings("unchecked")
    public void cadastrarRemedio(String nome, int estoque, boolean precisaReceita,
                                  int ubsId, String descricao, String tipo, String gramatura) {
        List<Map<String, Object>> remedios = (List<Map<String, Object>>) dados.get("remedios");
        int novoId;
        try {
            novoId = remedios.stream()
                .filter(r -> r.get("idRemedio") != null)
                .mapToInt(r -> ((Number) r.get("idRemedio")).intValue())
                .max().orElse(0) + 1;
        } catch (Exception e) { novoId = remedios.size() + 1; }

        Map<String, Object> rem = new HashMap<>();
        rem.put("idRemedio",      novoId);
        rem.put("nome",           nome);
        rem.put("estoque",        estoque);
        rem.put("precisaReceita", precisaReceita);
        rem.put("ubsId",          ubsId);
        rem.put("descricao",      descricao != null ? descricao : "");
        rem.put("tipo",           tipo      != null ? tipo      : "");
        rem.put("gramatura",      gramatura != null ? gramatura : "");
        remedios.add(rem);
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void atualizarRemedio(int id, String nome, int estoque, boolean precisaReceita,
                                  int ubsId, String descricao, String tipo, String gramatura) {
        List<Map<String, Object>> remedios = (List<Map<String, Object>>) dados.get("remedios");
        for (Map<String, Object> obj : remedios) {
            try {
                Object idObj = obj.get("idRemedio") != null ? obj.get("idRemedio") : obj.get("id");
                if (idObj == null) continue;
                if (((Number) idObj).intValue() == id) {
                    obj.put("nome", nome); obj.put("estoque", estoque);
                    obj.put("precisaReceita", precisaReceita); obj.put("ubsId", ubsId);
                    obj.put("descricao", descricao != null ? descricao : "");
                    obj.put("tipo",      tipo      != null ? tipo      : "");
                    obj.put("gramatura", gramatura != null ? gramatura : "");
                    break;
                }
            } catch (Exception e) { System.err.println("Erro ao atualizar remedio: " + e.getMessage()); }
        }
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void removerRemedio(int id) {
        List<Map<String, Object>> remedios = (List<Map<String, Object>>) dados.get("remedios");
        remedios.removeIf(obj -> {
            try {
                Object idObj = obj.get("idRemedio") != null ? obj.get("idRemedio") : obj.get("id");
                return idObj != null && ((Number) idObj).intValue() == id;
            }
            catch (Exception e) { return false; }
        });
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void diminuirEstoque(int remedioId, int quantidade) {
        List<Map<String, Object>> remedios = (List<Map<String, Object>>) dados.get("remedios");
        for (Map<String, Object> obj : remedios) {
            try {
                Object idObj = obj.get("idRemedio") != null ? obj.get("idRemedio") : obj.get("id");
                if (idObj == null) continue;
                if (((Number) idObj).intValue() == remedioId) {
                    int atual = ((Number) obj.get("estoque")).intValue();
                    obj.put("estoque", Math.max(0, atual - quantidade)); break;
                }
            } catch (Exception e) { System.err.println("Erro ao diminuir estoque: " + e.getMessage()); }
        }
        salvar();
    }

    @SuppressWarnings("unchecked")
    public void aumentarEstoque(int remedioId, int quantidade) {
        List<Map<String, Object>> remedios = (List<Map<String, Object>>) dados.get("remedios");
        for (Map<String, Object> obj : remedios) {
            try {
                Object idObj = obj.get("idRemedio") != null ? obj.get("idRemedio") : obj.get("id");
                if (idObj == null) continue;
                if (((Number) idObj).intValue() == remedioId) {
                    int atual = ((Number) obj.get("estoque")).intValue();
                    obj.put("estoque", atual + quantidade); break;
                }
            } catch (Exception e) { System.err.println("Erro ao aumentar estoque: " + e.getMessage()); }
        }
        salvar();
    }

    // PEDIDOS

    @SuppressWarnings("unchecked")
    public List<Pedido> getPedidos() {
        List<Pedido> lista = new ArrayList<>();
        List<Map<String, Object>> arr = (List<Map<String, Object>>) dados.get("pedidos");
        for (Map<String, Object> obj : arr) {
            try {
                // Compatibilidade: aceita "idPedido" (novo) ou "id" (antigo)
                Object pidObj = obj.get("idPedido") != null ? obj.get("idPedido") : obj.get("id");
                if (pidObj == null || obj.get("usuario") == null) continue;
                Map<String, Object> usuarioObj = (Map<String, Object>) obj.get("usuario");
                Usuario usuario = construirUsuarioParcial(usuarioObj);
                String status = (String) obj.getOrDefault("status", "Pendente");
                Pedido pedido = new Pedido(((Number) pidObj).intValue(), usuario, status);

                if (obj.containsKey("remedios")) {
                    List<Map<String, Object>> remsArr = (List<Map<String, Object>>) obj.get("remedios");
                    for (Map<String, Object> rem : remsArr) {
                        // Compatibilidade: aceita "idRemedio" ou "id" dentro do pedido
                        Object ridObj = rem.get("idRemedio") != null ? rem.get("idRemedio") : rem.get("id");
                        if (ridObj == null || rem.get("nome") == null) continue;
                        pedido.adicionarRemedio(new Remedio(
                            ((Number) ridObj).intValue(),
                            (String) rem.get("nome"),
                            rem.containsKey("estoque") ? ((Number) rem.get("estoque")).intValue() : 0,
                            rem.get("precisaReceita") != null && (Boolean) rem.get("precisaReceita"),
                            rem.containsKey("ubsId") ? ((Number) rem.get("ubsId")).intValue() : 1,
                            "", "", ""
                        ));
                    }
                }
                lista.add(pedido);
            } catch (Exception e) {
                System.err.println("Pedido ignorado: " + e.getMessage());
            }
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public Pedido criarPedido(Usuario usuario, List<Remedio> remedios) {
        List<Map<String, Object>> pedidos = (List<Map<String, Object>>) dados.get("pedidos");
        int novoId;
        try {
            novoId = pedidos.stream()
                .filter(p -> p.get("idPedido") != null)
                .mapToInt(p -> ((Number) p.get("idPedido")).intValue())
                .max().orElse(0) + 1;
        } catch (Exception e) { novoId = pedidos.size() + 1; }

        Map<String, Object> usuarioSnap = new HashMap<>();
        usuarioSnap.put("cpf",   usuario.getCpf());
        usuarioSnap.put("nome",  usuario.getNome());
        usuarioSnap.put("email", usuario.getEmail());

        List<Map<String, Object>> remsArr = new ArrayList<>();
        for (Remedio r : remedios) {
            Map<String, Object> rm = new HashMap<>();
            rm.put("idRemedio",      r.getId());
            rm.put("nome",           r.getNome());
            rm.put("precisaReceita", r.isPrecisaReceita());
            rm.put("estoque",        r.getEstoque());
            rm.put("ubsId",          r.getUbsId());
            remsArr.add(rm);
        }

        Map<String, Object> pedidoObj = new HashMap<>();
        pedidoObj.put("idPedido", novoId);
        pedidoObj.put("usuario",  usuarioSnap);
        pedidoObj.put("remedios", remsArr);
        pedidoObj.put("status",   "Pendente");
        pedidos.add(pedidoObj);
        salvar();

        Pedido pedido = new Pedido(novoId, usuario);
        for (Remedio r : remedios) pedido.adicionarRemedio(r);
        return pedido;
    }

    public void aprovarPedido(int id)  { atualizarStatusPedido(id, "Aprovado");  }
    public void cancelarPedido(int id) { atualizarStatusPedido(id, "Cancelado"); }
    public void concluirPedido(int id) { atualizarStatusPedido(id, "Concluido"); }

    @SuppressWarnings("unchecked")
    private void atualizarStatusPedido(int id, String novoStatus) {
        List<Map<String, Object>> pedidos = (List<Map<String, Object>>) dados.get("pedidos");
        for (Map<String, Object> obj : pedidos) {
            try {
                Object pidObj = obj.get("idPedido") != null ? obj.get("idPedido") : obj.get("id");
                if (pidObj == null) continue;
                if (((Number) pidObj).intValue() == id) {
                    obj.put("status", novoStatus); break;
                }
            } catch (Exception e) { System.err.println("Erro ao atualizar pedido: " + e.getMessage()); }
        }
        salvar();
    }

    // METODOS AUXILIARES - POLIMORFISMO

    private Pessoa construirPessoa(Map<String, Object> obj) {
        String tipo = (String) obj.getOrDefault("tipo", "usuario");
        if ("admin".equals(tipo)) {
            return new Admin(
                (String) obj.get("nome"), (String) obj.get("cpf"),
                (String) obj.getOrDefault("email", ""), (String) obj.get("senha")
            );
        }
        return construirUsuario(obj);
    }

    private Usuario construirUsuario(Map<String, Object> obj) {
        int cotaMensal    = obj.containsKey("cotaMensal")    ? ((Number) obj.get("cotaMensal")).intValue()    : 10;
        int cotaUtilizada = obj.containsKey("cotaUtilizada") ? ((Number) obj.get("cotaUtilizada")).intValue() : 0;
        String mesRef     = (String) obj.getOrDefault("mesReferencia", YearMonth.now().toString());
        return new Usuario(
            (String) obj.get("nome"), (String) obj.get("cpf"),
            (String) obj.getOrDefault("email", ""), (String) obj.get("senha"),
            cotaMensal, cotaUtilizada, mesRef
        );
    }

    private Usuario construirUsuarioParcial(Map<String, Object> obj) {
        return new Usuario(
            (String) obj.getOrDefault("nome", ""), (String) obj.getOrDefault("cpf", ""),
            (String) obj.getOrDefault("email", ""), "", 10, 0, YearMonth.now().toString()
        );
    }
}