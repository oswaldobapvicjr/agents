# agents

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/oswaldobapvicjr/agents/Java%20CI%20with%20Maven)](https://github.com/oswaldobapvicjr/agents/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/codecov/c/github/oswaldobapvicjr/agents)](https://codecov.io/gh/oswaldobapvicjr/agents)


[![Build Status](https://travis-ci.org/oswaldobapvicjr/agents.svg?branch=main)](https://travis-ci.org/oswaldobapvicjr/agents)
[![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/agents/badge.svg?branch=main)](https://coveralls.io/github/oswaldobapvicjr/agents?branch=main)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/agents/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/agents)
[![Javadoc](https://javadoc.io/badge2/net.obvj/agents/javadoc.svg)](https://javadoc.io/doc/net.obvj/agents)

A lightweight timer/cron agents framework for Java applications.

---

## Agents overview

Convert any Java class into an Agent by adding the **@Agent** annotation at the class, then mark one method with the **@Run** annotation so it will be called automatically. The framework looks up for agent classes by scanning user-specified packages at runtime.

An agent can be o type **Timer** or **Cron**, both designed to run particular tasks periodically in the JVM.

### Timer agents

A Timer agent can be executed periodically, in a fixed run frequency, which must be in seconds, minutes, or hours. For example:

```java
package com.mycompany.agents;
...
@Agent(type = AgentType.TIMER, interval = "30 seconds")
public class MyTimerAgent {
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
package com.mycompany.agents;
...
@Agent(type = AgentType.CRON, frequency = "0 2 * * MON-FRI")
public class MyCronAgent {
    @Run
    public void execute() {
        // This method will be called every weekday at 2:00 AM
    }
}
```

---

## Usage

1. Scan one or more base packages to search for agents

```java
AgentManager manager = AgentManager.defaultInstance();
manager.scanPackage("com.mycompany.agents");
```

3. Start all agents

```java
manager.startAllAgents();
```

---

## How to include it

If you are using Maven, add **Agents** as a dependency on your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>agents</artifactId>
    <version>0.1.0</version>
</dependency>
```

If you use other dependency managers (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/agents).
