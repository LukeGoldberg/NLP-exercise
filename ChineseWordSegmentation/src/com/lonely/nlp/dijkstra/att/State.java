package com.lonely.nlp.dijkstra.att;

/**
 * @author hankcs
 */
public class State implements Comparable<State>
{
    /**
     * 路径花费
     */
    public double cost;
    /**
     * 当前位置
     */
    public int vertex;

    @Override
    public int compareTo(State o)
    {
        return Double.compare(cost, o.cost);
    }

    public State(double cost, int vertex)
    {
        this.cost = cost;
        this.vertex = vertex;
    }
}
