package models;

public class AirCondition
{
    private int particulatesRate = 0;
    private int coRate = 0;

    public enum State
    {
        CLEAN, HEALTHY, UNHEALTHY
    }

    private State airState = State.CLEAN;

    public AirCondition() { }

    public AirCondition(int particulatesRate, int coRate, State airState)
    {
        this.particulatesRate = particulatesRate;
        this.coRate = coRate;
        this.airState = airState;
    }

    public int getParticulatesRate()
    {
        return particulatesRate;
    }

    public void setParticulatesRate(int particulatesRate)
    {
        this.particulatesRate = particulatesRate;
    }

    public int getCORate()
    {
        return coRate;
    }

    public void setCoRate(int coRate)
    {
        this.coRate = coRate;
    }

    public State getAirState()
    {
        return airState;
    }

    public void setAirState(State airState)
    {
        this.airState = airState;
    }
}
