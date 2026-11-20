package cabenobolso;

import javax.swing.*;
//import java.awt.*;
import java.util.List;
import java.util.Map;

public class TelaPrincipal extends JFrame {

    private PlanejamentoControle controle = new PlanejamentoControle();

    // Componentes Participante
    private JTextField txtCodigo = new JTextField(5);
    private JTextField txtNome = new JTextField(12);
    private JTextField txtOrcamento = new JTextField(6);
    private JTextArea areaParticipantes = new JTextArea(12, 40);

    // Componentes Despesa
    private JTextField txtDescricao = new JTextField(12);
    private JTextField txtValor = new JTextField(6);
    private JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Fixa", "Por Pessoa"});
    private DefaultListModel<Participante> listModel = new DefaultListModel<>();
    private JList<Participante> listParticipantes = new JList<>(listModel);
    private JTextArea areaDespesas = new JTextArea(10, 40);

    // Componentes Resumo
    private JTextArea areaResumo = new JTextArea(15, 40);

    public TelaPrincipal() {
        setTitle("Cabe no bolso");
        setSize(650, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu Arquivo
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Arquivo");
        JMenuItem itemNovo = new JMenuItem("Novo");
        JMenuItem itemSalvar = new JMenuItem("Salvar");
        JMenuItem itemCarregar = new JMenuItem("Carregar");

        itemNovo.addActionListener(e -> {
            controle.novoPlanejamento();
            atualizarTela();
        });

        itemSalvar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    controle.salvar(fc.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Salvo!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                }
            }
        });

        itemCarregar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    controle.carregar(fc.getSelectedFile().getAbsolutePath());
                    atualizarTela();
                    JOptionPane.showMessageDialog(this, "Carregado!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar.");
                }
            }
        });

        menu.add(itemNovo);
        menu.add(itemSalvar);
        menu.add(itemCarregar);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Abas
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Participantes", criarPainelParticipantes());
        abas.addTab("Despesas", criarPainelDespesas());
        abas.addTab("Resumo", criarPainelResumo());
        add(abas);
    }

    private JPanel criarPainelParticipantes() {
        JPanel p = new JPanel();
        p.add(new JLabel("Código:"));
        p.add(txtCodigo);
        p.add(new JLabel("Nome:"));
        p.add(txtNome);
        p.add(new JLabel("Orçamento:"));
        p.add(txtOrcamento);

        JButton btnAdd = new JButton("Salvar");
        JButton btnRemover = new JButton("Remover por Código");
        p.add(btnAdd);
        p.add(btnRemover);

        areaParticipantes.setEditable(false);
        p.add(new JScrollPane(areaParticipantes));

        btnAdd.addActionListener(e -> {
            try {
                int cod = Integer.parseInt(txtCodigo.getText());
                String nome = txtNome.getText();
                double orc = Double.parseDouble(txtOrcamento.getText().replace(",", "."));
                controle.salvarParticipante(cod, nome, orc, false);
                txtCodigo.setText("");
                txtNome.setText("");
                txtOrcamento.setText("");
                atualizarTela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnRemover.addActionListener(e -> {
            try {
                int cod = Integer.parseInt(JOptionPane.showInputDialog("Digite o código para remover:"));
                for (Participante part : controle.getParticipantes()) {
                    if (part.getCodigo() == cod) {
                        controle.removerParticipante(part);
                        atualizarTela();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Código não encontrado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel criarPainelDespesas() {
        JPanel p = new JPanel();
        p.add(new JLabel("Descrição:"));
        p.add(txtDescricao);
        p.add(new JLabel("Valor:"));
        p.add(txtValor);
        p.add(cbTipo);

        p.add(new JLabel("Participantes (Ctrl+Clique):"));
        listParticipantes.setVisibleRowCount(3);
        p.add(new JScrollPane(listParticipantes));

        JButton btnAdd = new JButton("Salvar Despesa");
        JButton btnRemover = new JButton("Remover Última");
        p.add(btnAdd);
        p.add(btnRemover);

        areaDespesas.setEditable(false);
        p.add(new JScrollPane(areaDespesas));

        btnAdd.addActionListener(e -> {
            try {
                String desc = txtDescricao.getText();
                double val = Double.parseDouble(txtValor.getText().replace(",", "."));
                String tipo = (String) cbTipo.getSelectedItem();
                List<Participante> sel = listParticipantes.getSelectedValuesList();

                controle.salvarDespesa(null, desc, val, tipo, sel);
                txtDescricao.setText("");
                txtValor.setText("");
                atualizarTela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnRemover.addActionListener(e -> {
            List<Despesa> lista = controle.getDespesas();
            if (!lista.isEmpty()) {
                controle.removerDespesa(lista.get(lista.size() - 1));
                atualizarTela();
            }
        });

        return p;
    }

    private JPanel criarPainelResumo() {
        JPanel p = new JPanel();
        areaResumo.setEditable(false);
        p.add(new JScrollPane(areaResumo));
        return p;
    }

    private void atualizarTela() {
        // Lista Participantes
        areaParticipantes.setText("");
        listModel.clear();
        for (Participante p : controle.getParticipantes()) {
            areaParticipantes.append("Cód: " + p.getCodigo() + " | Nome: " + p.getNome() + " | Orçamento: R$ " + p.getOrcamento() + "\n");
            listModel.addElement(p);
        }

        // Lista Despesas
        areaDespesas.setText("");
        for (Despesa d : controle.getDespesas()) {
            String tipo = (d instanceof DespesaFixa) ? "Fixa" : "Por Pessoa";
            areaDespesas.append(d.getDescricao() + " (" + tipo + ") - Total: R$ " + d.getCustoTotal() + "\n");
        }

        // Resumo
        areaResumo.setText("CUSTO TOTAL DO PASSEIO: R$ " + String.format("%.2f", controle.getCustoTotal()) + "\n\n");
        Map<Participante, Double> totais = controle.getTotaisIndividuais();
        for (Participante p : totais.keySet()) {
            double gasto = totais.get(p);
            double orc = p.getOrcamento();
            String status = (gasto <= orc) ? "Dentro do limite" : "Excedido em R$ " + String.format("%.2f", gasto - orc);
            areaResumo.append(p.getNome() + " -> Gasto: R$ " + String.format("%.2f", gasto) + " | Orçamento: R$ " + orc + " (" + status + ")\n");
        }
    }
}