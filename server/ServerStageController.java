package server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.AirCondition;
import net.AirConditionServer;

import utils.MyObservable;

import java.io.IOException;

public class ServerStageController
{
    public Button btnStartServer;

    public VBox vbServerOptions;
    public Label lblListenPort;

    public TextField txtParticulates;
    public TextField txtCO;

    private MyObservable<AirCondition> airCondition = new MyObservable<>(new AirCondition());

    private AirConditionServer server;

    @FXML
    private void onBtnStartServerClicked()
    {
        try
        {
            server = new AirConditionServer(airCondition);
            server.getSocketState().addObserver(this::onServerStateChanged);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBtnUpdateClicked()
    {
        int particulates = Integer.parseInt(txtParticulates.getText());
        int CO = Integer.parseInt(txtCO.getText());
        int mean = (particulates + CO) / 2;
        AirCondition.State state;
        if (mean < 30)
            state = AirCondition.State.CLEAN;
        else if (mean < 60)
            state = AirCondition.State.HEALTHY;
        else
            state = AirCondition.State.UNHEALTHY;
        airCondition.setState(new AirCondition(particulates, CO, state));
    }

    public void onServerStateChanged(Boolean newState)
    {
        Platform.runLater(()->
        {
            btnStartServer.setDisable(newState);
            vbServerOptions.setDisable(!newState);

            if (newState)
                lblListenPort.setText(Integer.toString(server.getServerSocket().getLocalPort()));
        });
    }
}
