package net.obvj.agents.test;

import net.obvj.agents.AgentManager;

public class AgentsTestDrive
{
    public static void main(String[] args) throws InterruptedException
    {
        AgentManager agentManager = AgentManager.defaultInstance();
        agentManager.scanPackage("net.obvj.agents.test.agents.valid");
        agentManager.startAllAgents();
    }

}
