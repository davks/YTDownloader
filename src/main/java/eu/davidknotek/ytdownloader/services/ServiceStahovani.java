package eu.davidknotek.ytdownloader.services;

import eu.davidknotek.ytdownloader.tasks.Stahovani;
import eu.davidknotek.ytdownloader.typy.Fronta;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

public class ServiceStahovani extends Service<String> {

    private Fronta fronta;
    private String cesta;
    private Label lblUkazatelPrubehu;
    private Label lblZbyvajiciCas;
    private String ytDownloadTool;

    @Override
    protected Task<String> createTask() {
        Stahovani stahovani = new Stahovani(fronta, ytDownloadTool);
        stahovani.setLblUkazatelPrubehu(lblUkazatelPrubehu);
        stahovani.setLblZbyvajiciCas(lblZbyvajiciCas);
        stahovani.setCesta(cesta);
        return stahovani;
    }

    public void setFronta(Fronta fronta) {
        this.fronta = fronta;
    }

    public void setCesta(String cesta) {
        this.cesta = cesta;
    }

    public void setYtDownloadTool(String ytDownloadTool) {
        this.ytDownloadTool = ytDownloadTool;
    }

    public void setLblUkazatelPrubehu(Label lblUkazatelPrubehu) {
        this.lblUkazatelPrubehu = lblUkazatelPrubehu;
    }

    public void setLblZbyvajiciCas(Label lblZbyvajiciCas) {
        this.lblZbyvajiciCas = lblZbyvajiciCas;
    }
}
