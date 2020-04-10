package com.redroundrobin.thirema.gateway;

import com.redroundrobin.thirema.gateway.models.Device;
import com.redroundrobin.thirema.gateway.models.Sensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GatewayTest {
    Gateway gateway;
    String config;
    String otherConfig;
    String wrongConfig;
    private final PrintStream OriginalTestStartShouldThrowSocketTimeOutException = System.out;
    private final ByteArrayOutputStream TestStartShouldThrowSocketTimeOutExceptionContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(TestStartShouldThrowSocketTimeOutExceptionContent));
        config = "{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [{\"[deviceId\":0,\"timestamp\":0,\"sensors\":[{\"sensorId\":0,\"timestamp\":0,\"data\":0},{\"sensorId\":1,\"timestamp\":0,\"data\":1},{\"sensorId\":2,\"timestamp\":0,\"data\":2},{\"sensorId\":3,\"timestamp\":0,\"data\":3}]},{\"deviceId\":1,\"timestamp\":0,\"sensors\":[{\"sensorId\":0,\"timestamp\":0,\"data\":0},{\"sensorId\":1,\"timestamp\":0,\"data\":1},{\"sensorId\":2,\"timestamp\":0,\"data\":2},{\"sensorId\":3,\"timestamp\":0,\"data\":3}]}],  \"storedPacket\":5,  \"storingTime\":6000}";
        otherConfig ="{\"address\":\"127.0.1.1\",\"port\":6969,\"name\":\"US-GATEWAY-1\",  \"devices\":  [{\"[deviceId\":0,\"timestamp\":0,\"sensors\":[{\"sensorId\":0,\"timestamp\":0,\"data\":0}]}],  \"storedPacket\":5,  \"storingTime\":6000}";

        wrongConfig = "";
        gateway = Gateway.BuildFromConfig(config);
    }

    @After
    public void restoreStreams() {
        System.setOut(OriginalTestStartShouldThrowSocketTimeOutException);
    }

    @Test
    public void TestNameGetter() throws UnknownHostException {
        Gateway test = new Gateway(InetAddress.getByName("127.0.0.1"), 6969, "US-GATEWAY-1", new ArrayList<>(), 5, 3);
        assertEquals("US-GATEWAY-1", test.getName());
    }

    @Test
    public void TestCreateRequestPacket() {
        byte[] packet = gateway.createRequestPacket(0, 0);
        int device = (int) packet[0]; // prendo uno tra gli id
        int operation = (int) packet[1];
        int sensor = (int) packet[3]; // prendo uno dei sensori del dispositivo
        assertEquals(0,device);
        assertEquals(0,operation);
        assertEquals(0,sensor);


    }

    @Test
    public void TestBuildFromRightConfig() {
        Gateway rightGateway = Gateway.BuildFromConfig(config);
        assertEquals("US-GATEWAY-1", rightGateway.getName());
    }

    @Test(expected = NullPointerException.class)
    public void TestBuildFromWrongConfig() {
        Gateway wrong = Gateway.BuildFromConfig(wrongConfig);
        wrong.getName();
    }

    @Test
    public void TestInitShouldntFindAnySensor() {
        Gateway.BuildFromConfig(otherConfig).init();
        assertEquals("sensore in timeout n0 del device n0\n", TestStartShouldThrowSocketTimeOutExceptionContent.toString());
    }

    @Test
    public void TestStartShouldThrowSocketTimeout() throws UnknownHostException {
        List<Sensor> sens = new ArrayList<>();
        sens.add(new Sensor(1, 0));
        List<Device> devs = new ArrayList<>();
        devs.add(new Device(1, sens));

        Gateway test = new Gateway(InetAddress.getByName("127.0.0.1"), 6969, "US-GATEWAY-1", devs, 5, 3);
        test.start();
    }

}