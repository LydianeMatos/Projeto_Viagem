package cabenobolso;

import java.util.List;
import java.util.Map;

public class PlanejamentoControle {

    private Planejamento planejamento;
    private final IPersistencia persistencia;

    public PlanejamentoControle() {
        this.planejamento = new Planejamento();
        this.persistencia = new Persistencia();
    }

    public void novoPlanejamento() {
        this.planejamento = new Planejamento();
    }

    public void salvarParticipante(int codigo, String nome, double orcamento, boolean isEdicao) {
        if (isEdicao) {
            for (Participante p : planejamento.getParticipantes()) {
                if (p.getCodigo() == codigo) {
                    p.setNome(nome);
                    p.setOrcamento(orcamento);
                    return;
                }
            }
        }
        planejamento.adicionarParticipante(new Participante(codigo, nome, orcamento));
    }

    public void removerParticipante(Participante p) {
        planejamento.removerParticipante(p);
    }

    public void salvarDespesa(Despesa antiga, String descricao, double valor, String tipo, List<Participante> vinculados) {
        Despesa nova = "Fixa".equals(tipo)
                ? new DespesaFixa(descricao, valor, vinculados)
                : new DespesaPorPessoa(descricao, valor, vinculados);

        if (antiga != null) {
            planejamento.removerDespesa(antiga);
        }
        planejamento.adicionarDespesa(nova);
    }

    public void removerDespesa(Despesa d) {
        planejamento.removerDespesa(d);
    }

    public List<Participante> getParticipantes() { return planejamento.getParticipantes(); }
    public List<Despesa> getDespesas() { return planejamento.getDespesas(); }
    public double getCustoTotal() { return planejamento.calcularCustoTotal(); }
    public Map<Participante, Double> getTotaisIndividuais() { return planejamento.calcularTotaisIndividuais(); }

    public void salvar(String caminho) throws Exception { persistencia.salvar(planejamento, caminho); }
    public void carregar(String caminho) throws Exception { this.planejamento = persistencia.carregar(caminho); }
}
