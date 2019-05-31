package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.AirCondition;
import net.AirConditionClient;
import utils.MyObserver;

import java.io.IOException;

public class ClientStageController implements MyObserver<AirCondition>
{
    public VBox vbConnect;
    public TextField txtPort;
    public GridPane gpCondition;
    public Label lblCondition;
    public Label lblParticulates;
    public Label lblCORate;


    private AirConditionClient client;

    @FXML
    private void onBtnConnectClicked()
    {
        try
        {
            client = new AirConditionClient(Integer.parseInt(txtPort.getText()));
            client.getAirCondition().addObserver(this);
            client.getSocketState().addObserver(newState ->
                    Platform.runLater(() ->
                    {
                        vbConnect.setDisable(newState);
                        gpCondition.setDisable(!newState);
                    }));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStateChanged(AirCondition newState)
    {
        Platform.runLater(() -> bindWithAirCondition(newState));
    }

    private void bindWithAirCondition(AirCondition airCondition)
    {
        lblCondition.setText(airCondition.getAirState().toString());
        lblParticulates.setText(Integer.toString(airCondition.getParticulatesRate()));
        lblCORate.setText(Integer.toString(airCondition.getCORate()));
    }
}
