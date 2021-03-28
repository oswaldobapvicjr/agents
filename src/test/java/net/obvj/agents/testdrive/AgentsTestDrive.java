package net.obvj.agents.testdrive;

import net.obvj.agents.AgentManager;

public class AgentsTestDrive
{
    public static void main(String[] args) throws InterruptedException
    {
        AgentManager agentManager = AgentManager.getInstance();
        agentManager.scanPackage("net.obvj.agents");
        agentManager.startAllAgents();

        Thread.sleep(5000);

        agentManager.stopAgent("net.obvj.agents.dummy.DummyAgent");
        agentManager.resetAgent("net.obvj.agents.dummy.DummyAgent");
        agentManager.startAgent("net.obvj.agents.dummy.DummyAgent");

    }
}
