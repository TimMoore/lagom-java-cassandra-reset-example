package com.example.hello.impl;

import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloService;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HelloServiceTest {

  private static final ServiceTest.Setup setup = defaultSetup().withCassandra();
  private static ServiceTest.TestServer server;

  @BeforeClass
  public static void startServer() {
    server = ServiceTest.startServer(setup);
  }

  @AfterClass
  public static void stopServer() {
    if (server != null) {
      server.stop();
    }
    server = null;
  }

  @Test
  public void should1StorePersonalizedGreeting() throws Exception {
    HelloService service = server.client(HelloService.class);

    String msg1 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals("Hello, Alice!", msg1); // default greeting

    service.useGreeting("Alice").invoke(new GreetingMessage("Hi")).toCompletableFuture().get(5, SECONDS);
    String msg2 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals("Hi, Alice!", msg2);

    String msg3 = service.hello("Bob").invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals("Hello, Bob!", msg3); // default greeting
  }

  @Test
  public void should2IsolateTestData() throws Exception {
    HelloService service = server.client(HelloService.class);

    String msg1 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals("Hello, Alice!", msg1); // default greeting, not affected by the change in the previous test
  }

}
