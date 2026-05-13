package infra;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * GerenciadorDados — responsável por salvar e carregar os dados do sistema.
 *
 * Os dados são armazenados em um arquivo "dados.json" na pasta do projeto.
 * Não usamos banco de dados — tudo em memória e arquivo texto, conforme requisito.
 *
 * ESTRUTURA DO JSON:
 * {
 *   "usuarios": [ {...}, {...} ],
 *   "remedios":  [ {...}, {...} ],
 *   "pedidos":   [ {...}, {...} ],
 *   "ubs":       [ {...}, {...} ]
 * }
 */
public class GerenciadorJSON {

    // Nome do arquivo onde os dados são salvos
    private static final String ARQUIVO = "dados.json";

    // ─── Carregar ──────────────────────────────────────────────────────────────

    /**
     * Carrega os dados do arquivo JSON.
     * Se o arquivo não existir, cria os dados iniciais (UBS, admin e remédios padrão).
     */
    public static Map<String, Object> carregarDados() {
        try {
            File arquivo = new File(ARQUIVO);
            if (!arquivo.exists()) {
                return criarDadosIniciais();
            }
            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO)));
            return parseJSON(conteudo);
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            return criarDadosIniciais();
        }
    }

    // ─── Dados Iniciais ────────────────────────────────────────────────────────

    /**
     * Cria os dados padrão do sistema na primeira execução.
     *
     * REGRAS DE NEGÓCIO:
     * - Apenas 2 UBS fixas (Jordanópolis e Vila Mariana)
     * - Admin padrão: cpf="administrador", senha="123"
     * - Remédios pré-cadastrados em cada UBS
     */
    private static Map<String, Object> criarDadosIniciais() {
        Map<String, Object> dados = new HashMap<>();

        // ── Admin padrão ──────────────────────────────────────────────────────
        List<Map<String, Object>> usuarios = new ArrayList<>();
        Map<String, Object> admin = new HashMap<>();
        admin.put("tipo",  "admin");
        admin.put("nome",  "Administrador");
        admin.put("cpf",   "administrador");
        admin.put("email", "admin@sus.gov.br");
        admin.put("senha", "123");
        usuarios.add(admin);

        // ── 2 UBS fixas ──────────────────────────────────────────────────────
        List<Map<String, Object>> ubs = new ArrayList<>();
        ubs.add(criarUBS(1, "UBS Jordanópolis",
                "Viela Jangada Nova", "75", "Jardim Pres. Dutra", "SP", "04830-200"));
        ubs.add(criarUBS(2, "UBS Vila Mariana",
                "Rua das Flores", "123", "Vila Mariana", "SP", "04567-890"));

        // ── Remédios para UBS 1 (Jordanópolis) ───────────────────────────────
        List<Map<String, Object>> remedios = new ArrayList<>();
        remedios.add(criarRemedio(1,  "Paracetamol", 100, false, 1, "Analgésico e antitérmico",        "Comprimido", "500mg"));
        remedios.add(criarRemedio(2,  "Ibuprofeno",  50,  false, 1, "Anti-inflamatório",               "Comprimido", "600mg"));
        remedios.add(criarRemedio(3,  "Amoxicilina", 30,  true,  1, "Antibiótico (requer receita)",    "Cápsula",    "500mg"));
        remedios.add(criarRemedio(4,  "Metformina",  70,  false, 1, "Antidiabético oral",              "Comprimido", "850mg"));
        remedios.add(criarRemedio(5,  "Captopril",   90,  false, 1, "Anti-hipertensivo inibidor ECA",  "Comprimido", "25mg"));

        // ── Remédios para UBS 2 (Vila Mariana) ───────────────────────────────
        remedios.add(criarRemedio(6,  "Dipirona",    80,  false, 2, "Analgésico e antitérmico potente","Comprimido", "1g"));
        remedios.add(criarRemedio(7,  "Losartana",   60,  false, 2, "Anti-hipertensivo",               "Comprimido", "50mg"));
        remedios.add(criarRemedio(8,  "Atenolol",    55,  false, 2, "Beta-bloqueador cardioseletivo",  "Comprimido", "25mg"));
        remedios.add(criarRemedio(9,  "Azitromicina",25,  true,  2, "Antibiótico (requer receita)",    "Comprimido", "500mg"));
        remedios.add(criarRemedio(10, "Omeprazol",   40,  false, 2, "Inibidor da bomba de prótons",    "Cápsula",    "20mg"));

        dados.put("usuarios", usuarios);
        dados.put("ubs",      ubs);
        dados.put("remedios", remedios);
        dados.put("pedidos",  new ArrayList<Map<String, Object>>());

        salvarDados(dados);
        return dados;
    }

    /** Cria o mapa de dados de uma UBS. */
    private static Map<String, Object> criarUBS(int id, String nome,
            String logradouro, String numero, String bairro, String estado, String cep) {
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

    /** Cria o mapa de dados de um remédio. */
    private static Map<String, Object> criarRemedio(int id, String nome,
            int estoque, boolean precisaReceita, int ubsId,
            String descricao, String tipo, String gramatura) {
        Map<String, Object> r = new HashMap<>();
        r.put("idRemedio",      id);
        r.put("nome",           nome);
        r.put("estoque",        estoque);
        r.put("precisaReceita", precisaReceita);
        r.put("ubsId",          ubsId);
        r.put("descricao",      descricao);
        r.put("tipo",           tipo);
        r.put("gramatura",      gramatura);
        return r;
    }

    // ─── Salvar ────────────────────────────────────────────────────────────────

    /**
     * Salva os dados no arquivo JSON.
     * Chamado sempre que qualquer alteração é feita.
     */
    public static void salvarDados(Map<String, Object> dados) {
        try {
            String json = paraJSON(dados);
            Files.write(Paths.get(ARQUIVO), json.getBytes());
        } catch (Exception e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    // ─── Serialização JSON manual ──────────────────────────────────────────────
    // (Sem bibliotecas externas, apenas Java puro)

    /** Converte qualquer objeto Java para string JSON. */
    @SuppressWarnings("unchecked")
    private static String paraJSON(Object obj) {
        if (obj == null)                                   return "null";
        if (obj instanceof Boolean || obj instanceof Number) return obj.toString();
        if (obj instanceof String)
            return "\"" + obj.toString().replace("\"", "\\\"") + "\"";

        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean primeiro = true;
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (!primeiro) sb.append(",");
                sb.append("\"").append(e.getKey()).append("\":").append(paraJSON(e.getValue()));
                primeiro = false;
            }
            return sb.append("}").toString();
        }

        if (obj instanceof List) {
            List<?> lista = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean primeiro = true;
            for (Object item : lista) {
                if (!primeiro) sb.append(",");
                sb.append(paraJSON(item));
                primeiro = false;
            }
            return sb.append("]").toString();
        }

        return "\"" + obj.toString() + "\"";
    }

    // ─── Desserialização JSON manual ───────────────────────────────────────────

    /** Ponto de entrada do parse — espera um objeto JSON (começa com {). */
    private static Map<String, Object> parseJSON(String json) {
        json = json.trim();
        if (json.startsWith("{")) return parseObjeto(json);
        return new HashMap<>();
    }

    /** Faz o parse de um objeto JSON { "chave": valor, ... }. */
    private static Map<String, Object> parseObjeto(String json) {
        Map<String, Object> mapa = new HashMap<>();
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return mapa;

        List<String> tokens = tokenizar(json);
        for (int i = 0; i < tokens.size(); i += 2) {
            String chave = tokens.get(i).replace("\"", "");
            if (i + 1 < tokens.size()) {
                mapa.put(chave, parseValor(tokens.get(i + 1)));
            }
        }
        return mapa;
    }

    /** Faz o parse de um array JSON [ valor, valor, ... ]. */
    private static List<Object> parseArray(String json) {
        List<Object> lista = new ArrayList<>();
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return lista;

        StringBuilder atual = new StringBuilder();
        boolean emString = false;
        int profundidade = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                emString = !emString;
                atual.append(c);
            } else if (!emString && (c == '{' || c == '[')) {
                profundidade++;
                atual.append(c);
            } else if (!emString && (c == '}' || c == ']')) {
                profundidade--;
                atual.append(c);
            } else if (!emString && c == ',' && profundidade == 0) {
                lista.add(parseValor(atual.toString().trim()));
                atual = new StringBuilder();
            } else {
                atual.append(c);
            }
        }
        if (atual.length() > 0) lista.add(parseValor(atual.toString().trim()));
        return lista;
    }

    /** Converte uma string JSON em um valor Java (String, Number, Boolean, Map ou List). */
    private static Object parseValor(String valor) {
        valor = valor.trim();
        if (valor.equals("null"))  return null;
        if (valor.equals("true"))  return true;
        if (valor.equals("false")) return false;
        if (valor.startsWith("\"") && valor.endsWith("\""))
            return valor.substring(1, valor.length() - 1);
        if (valor.startsWith("[")) return parseArray(valor);
        if (valor.startsWith("{")) return parseObjeto(valor);
        try {
            if (valor.contains(".")) return Double.parseDouble(valor);
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return valor;
        }
    }

    /**
     * Divide o conteúdo de um objeto JSON em tokens [chave, valor, chave, valor, ...].
     * Respeita strings e estruturas aninhadas.
     */
    private static List<String> tokenizar(String json) {
        List<String> tokens = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean emString = false;
        int profundidade = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                emString = !emString;
                atual.append(c);
            } else if (!emString && (c == '{' || c == '[')) {
                profundidade++;
                atual.append(c);
            } else if (!emString && (c == '}' || c == ']')) {
                profundidade--;
                atual.append(c);
            } else if (!emString && c == ':' && profundidade == 0) {
                tokens.add(atual.toString().trim());
                atual = new StringBuilder();
            } else if (!emString && c == ',' && profundidade == 0) {
                tokens.add(atual.toString().trim());
                atual = new StringBuilder();
            } else {
                atual.append(c);
            }
        }
        if (atual.length() > 0) tokens.add(atual.toString().trim());
        return tokens;
    }
}