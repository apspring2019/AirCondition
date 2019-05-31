package net;

import models.AirCondition;
import utils.MyObserver;
import utils.MyObservable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirConditionServer implements MyObserver<AirCondition>
{
    private ServerSocket serverSocket;
    private final List<ServerSideClient> clients = Collections.synchronizedList(new ArrayList<>());

    private MyObservable<AirCondition> airCondition;

    private MyObservable<Boolean> socketState;

    private Thread acceptorThread;

    public AirConditionServer(MyObservable<AirCondition> airCondition) throws IOException
    {
        this.airCondition = airCondition;
        airCondition.addObserver(this);
        this.serverSocket = new ServerSocket(0);
        initServer();
    }

    private void initServer()
    {
        socketState = new MyObservable<>(true);
        acceptorThread = new Thread(() ->
        {
            try
            {
                while (true)
                    onClientConnected(serverSocket.accept());
            }
            catch (IOException ignored) { }
            finally
            {
                try { close(); } catch (Exception ignored) {}
            }
        });
        acceptorThread.start();
    }

    private void onClientConnected(Socket socket) throws IOException
    {
        ServerSideClient client = new ServerSideClient(socket);
        client.sendState(airCondition.getState());
        client.getSocketState().addObserver(newState ->
        {
            if (!newState)
                clients.remove(client);
        });
        clients.add(client);
    }

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

    public MyObservable<Boolean> getSocketState()
    {
        return socketState;
    }

    @Override
    public void onStateChanged(AirCondition newState)
    {
        broadcastState(newState);
    }

    private void broadcastState(AirCondition newState)
    {
        clients.forEach(client -> client.sendState(newState));
    }

    public void close() throws IOException
    {
        try
        {
            acceptorThread.interrupt();
            serverSocket.close();
        }
        finally
        {
            socketState.setState(false);
        }
    }
}
