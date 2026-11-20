package cabenobolso;

import java.io.*;

interface IPersistencia {
    void salvar(Planejamento planejamento, String caminhoArquivo) throws Exception;
    Planejamento carregar(String caminhoArquivo) throws Exception;
}

public class Persistencia implements IPersistencia {

    @Override
    public void salvar(Planejamento planejamento, String caminhoArquivo) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(planejamento);
        }
    }

    @Override
    public Planejamento carregar(String caminhoArquivo) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            return (Planejamento) ois.readObject();
        }
    }
}
