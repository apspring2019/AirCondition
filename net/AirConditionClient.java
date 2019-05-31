package net;

import com.google.gson.Gson;
import com.google.gson.JsonStreamParser;
import models.AirCondition;
import utils.MyObservable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class AirConditionClient
{
    private Socket socket;

    private MyObservable<AirCondition> airCondition;

    private MyObservable<Boolean> socketState;

    private Thread readerThread;

    public AirConditionClient(int port) throws IOException
    {
        this.socket = new Socket("127.0.0.1", port);
        this.airCondition = new MyObservable<>(new AirCondition());
        initClient();
    }

    private void initClient()
    {
        this.socketState = new MyObservable<>(true);

        //We use another thread because reading may block our program
        this.readerThread = new Thread(() ->
        {
            Gson deserializer = new Gson();
            try (InputStream inputStream = socket.getInputStream())
            {
                JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(inputStream));
                while (parser.hasNext())
                    airCondition.setState(deserializer.fromJson(parser.next(), AirCondition.class));
                socket.close();
            }
            catch (IOException e) { e.printStackTrace(); }
            finally { socketState.setState(false); }
        });
        this.readerThread.start();
    }

    public MyObservable<AirCondition> getAirCondition()
    {
        return airCondition;
    }

    public MyObservable<Boolean> getSocketState()
    {
        return socketState;
    }

    public void close() throws IOException
    {
        try
        {
            readerThread.interrupt();
            socket.close();
        }
        finally
        {
            socketState.setState(false);
        }
    }
}
