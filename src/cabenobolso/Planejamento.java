package cabenobolso;

import java.io.Serializable;
import java.util.*;

public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Participante> participantes = new ArrayList<>();
    private final List<Despesa> despesas = new ArrayList<>();

    public void adicionarParticipante(Participante p) {
        if (participantes.contains(p)) {
            throw new IllegalArgumentException("Já existe um participante com o código " + p.getCodigo() + ".");
        }
        participantes.add(p);
        Collections.sort(participantes);
    }

    public void removerParticipante(Participante p) {
        for (Despesa d : despesas) {
            if (d.getParticipantes().contains(p)) {
                throw new IllegalStateException("Participante vinculado à despesa '" + d.getDescricao() + "'.");
            }
        }
        participantes.remove(p);
    }

    public void adicionarDespesa(Despesa d) {
        despesas.add(d);
    }

    public void removerDespesa(Despesa d) {
        despesas.remove(d);
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public List<Despesa> getDespesas() {
        return Collections.unmodifiableList(despesas);
    }

    public double calcularCustoTotal() {
        double total = 0.0;
        for (Despesa d : despesas) {
            total += d.getCustoTotal();
        }
        return total;
    }

    public Map<Participante, Double> calcularTotaisIndividuais() {
        Map<Participante, Double> totais = new LinkedHashMap<>();
        for (Participante p : participantes) {
            totais.put(p, 0.0);
        }
        for (Despesa d : despesas) {
            for (Map.Entry<Participante, Double> item : d.calcularRateio().entrySet()) {
                Participante p = item.getKey();
                if (totais.containsKey(p)) {
                    double acumulado = Math.round((totais.get(p) + item.getValue()) * 100) / 100.0;
                    totais.put(p, acumulado);
                }
            }
        }
        return totais;
    }
}
