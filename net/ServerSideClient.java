package net;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import models.AirCondition;
import utils.MyObservable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerSideClient
{
    private Socket socket;

    private Gson serializer;
    private JsonWriter jsonWriter;

    private MyObservable<Boolean> socketState;

    private Thread writerThread;

    private final BlockingQueue<AirCondition> sendQueue = new LinkedBlockingDeque<>();

    public ServerSideClient(Socket socket) throws IOException
    {
        this.socket = socket;
        this.serializer = new Gson();
        this.jsonWriter = new JsonWriter(new OutputStreamWriter(socket.getOutputStream()));

        initSocket();
    }

    private void initSocket()
    {
        this.socketState = new MyObservable<>(true);

        //We use another thread because writing may block our program
        this.writerThread = new Thread(() ->
        {
            try
            {
                while (true)
                {
                    serializer.toJson(sendQueue.take(), AirCondition.class, jsonWriter);
                    jsonWriter.flush();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                try { socket.close(); }
                catch (Exception ignored) {}
            }
            catch (InterruptedException ignored) { }
            finally { socketState.setState(false); }
        });
        this.writerThread.start();
    }

    public MyObservable<Boolean> getSocketState()
    {
        return socketState;
    }

    public void sendState(AirCondition state)
    {
        try { sendQueue.put(state); }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void close() throws IOException
    {

        try
        {
            writerThread.interrupt();
            socket.close();
        }
        finally
        {
            socketState.setState(false);
        }
    }
}
