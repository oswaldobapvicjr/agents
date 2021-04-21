# agents
A lightweight timer/cron agents framework for Java applications

## Agents overview

Any class annotated with **@Agent**, and declaring a main method annotated with **@Run**, can be managed by the service, which looks up for such classes by scanning specific user-specified base packages.

An agent can be o type **Timer** or **Cron**, both designed to run particular tasks periodically in the JVM.

### Timer agents

A Timer agent can be executed periodically, in a fixed run frequency, which must be in seconds, minutes, or hours. For example:

```java
@Agent(type = AgentType.TIMER, interval = "30 seconds")
public class MyAgent {
    @Run
    public void execute() {
       // This method will be called every 30 seconds...
    }
}
```

### Cron agents

A Cron agent can be executed at specific dates and times, comparable to the Cron Service available in Unix/Linux systems. Although they are more robust in terms of configuration flexibility, the interval between executions cannot be lower than 1 minute.

For example, the following agent is configured to execute every weekday at 2:00 AM:

```java
@Agent(type = AgentType.CRON, frequency = "0 2 * * MON-FRI")
public class MyAgent {
    @Run
    public void execute() {
        // This method will be called every weekday at 2:00 AM
    }
}
```
